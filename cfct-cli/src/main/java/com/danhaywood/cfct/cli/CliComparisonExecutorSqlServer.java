package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.spi.CommandAuditTouchedTableResolver;
import com.danhaywood.cfct.sql.TracingDataSourceProxyFactory;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import org.springframework.stereotype.Service;

import java.io.PrintStream;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

@Service
public class CliComparisonExecutorSqlServer implements CliComparisonExecutor {

    private final MultiTableComparisonService comparer;
    private final CliComparisonReportRenderer renderer;
    private final CommandAuditTouchedTableResolver touchedTableResolver;

    public CliComparisonExecutorSqlServer(
            final MultiTableComparisonService comparer,
            final CliComparisonReportRenderer renderer,
            final CommandAuditTouchedTableResolver touchedTableResolver) {
        this.comparer = comparer;
        this.renderer = renderer;
        this.touchedTableResolver = touchedTableResolver;
    }

    @Override
    public CliExecutionOutput execute(final CliArguments arguments) throws Exception {
        return execute(arguments, System.err);
    }

    @Override
    public CliExecutionOutput execute(final CliArguments arguments, final PrintStream err) throws Exception {
        final String leftJdbcUrl = jdbcUrl(arguments.jdbcUrl(), arguments.leftDatabase());
        final String rightJdbcUrl = jdbcUrl(arguments.jdbcUrl(), arguments.rightDatabase());
        ensureDriverRegistered(arguments.jdbcDriver());
        try (Connection leftConnection = openConnection(leftJdbcUrl, arguments.username(), arguments.password());
             Connection rightConnection = openConnection(rightJdbcUrl, arguments.username(), arguments.password())) {
            final ComparisonOptions options = new ComparisonOptions(
                    ComparisonOptions.defaults().businessKeyIndexSuffix(),
                    ComparisonOptions.defaults().ignoredColumnNames(),
                    event -> {
                        if (event.phase() == ComparisonProgressPhase.TABLE_STARTED) {
                            err.printf("[progress] %d/%d comparing %s%n",
                                    event.completedTables() + 1,
                                    event.totalTables(),
                                    event.table().displayName());
                        } else if (event.phase() == ComparisonProgressPhase.TABLE_COMPLETED) {
                            err.printf("[progress] %d/%d compared %s%n",
                                    event.completedTables(),
                                    event.totalTables(),
                                    event.table().displayName());
                        } else if (event.phase() == ComparisonProgressPhase.TABLE_FAILED) {
                            err.printf("[progress] failed %s: %s%n",
                                    event.table().displayName(),
                                    event.message() == null ? "comparison failed" : event.message());
                        }
                    });
            final List<TableRef> selectedTables = resolveTablesForExecution(arguments, leftConnection);
            final var result = comparer.compare(
                    leftConnection,
                    rightConnection,
                    new MultiTableComparisonRequest(selectedTables, options));
            err.flush();
            return renderer.render(result, arguments.outputFormat());
        }
    }

    protected Connection openConnection(final String jdbcUrl, final String username, final String password) throws Exception {
        final SQLServerDataSource sqlServerDataSource = new SQLServerDataSource();
        sqlServerDataSource.setURL(jdbcUrl);
        sqlServerDataSource.setUser(username);
        sqlServerDataSource.setPassword(password);
        final DataSource dataSource = TracingDataSourceProxyFactory.wrapIfEnabled(
                sqlServerDataSource,
                "cli-" + Integer.toHexString(jdbcUrl.hashCode()));
        return dataSource.getConnection();
    }

    protected List<TableRef> resolveTablesForExecution(final CliArguments arguments, final Connection leftConnection) throws Exception {
        if (!arguments.usesCommandTimeRangeSelection()) {
            return arguments.tables();
        }
        final List<String> interactionIds = selectInteractionIdsInRange(leftConnection, arguments.commandsFrom(), arguments.commandsTo());
        if (interactionIds.isEmpty()) {
            throw new IllegalArgumentException("No commands found in the provided command-time-range.");
        }
        final SortedSet<String> qualifiedTableNames = touchedTableResolver.resolveTouchedQualifiedTableNames(leftConnection, interactionIds);
        final List<TableRef> inferredTables = parseQualifiedTableRefs(qualifiedTableNames);
        if (inferredTables.isEmpty()) {
            throw new IllegalArgumentException("No business tables could be inferred from commands in the provided command-time-range.");
        }
        return inferredTables;
    }

    protected List<String> selectInteractionIdsInRange(
            final Connection leftConnection,
            final LocalDateTime commandsFrom,
            final LocalDateTime commandsTo) throws Exception {
        final String sql = """
                SELECT CONVERT(varchar(36), interactionId) AS interaction_id
                FROM causewayExtCommandLog.CommandLogEntry
                WHERE [timestamp] >= ? AND [timestamp] <= ?
                ORDER BY [timestamp] ASC, interactionId ASC
                """;
        try (PreparedStatement statement = leftConnection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(commandsFrom));
            statement.setTimestamp(2, Timestamp.valueOf(commandsTo));
            try (ResultSet resultSet = statement.executeQuery()) {
                final List<String> interactionIds = new ArrayList<>();
                while (resultSet.next()) {
                    final String interactionId = resultSet.getString("interaction_id");
                    if (interactionId != null && !interactionId.isBlank()) {
                        interactionIds.add(interactionId);
                    }
                }
                return List.copyOf(interactionIds);
            }
        }
    }

    private List<TableRef> parseQualifiedTableRefs(final Collection<String> qualifiedTableNames) {
        final List<TableRef> resolved = new ArrayList<>();
        for (String qualifiedName : qualifiedTableNames) {
            if (qualifiedName == null) {
                continue;
            }
            final String token = qualifiedName.trim();
            if (token.isBlank()) {
                continue;
            }
            final int separator = token.indexOf('.');
            if (separator <= 0 || separator != token.lastIndexOf('.') || separator == token.length() - 1) {
                continue;
            }
            final String schemaName = token.substring(0, separator).trim();
            final String tableName = token.substring(separator + 1).trim();
            if (!schemaName.isBlank() && !tableName.isBlank()) {
                resolved.add(new TableRef(schemaName, tableName));
            }
        }
        return List.copyOf(resolved);
    }

    private void ensureDriverRegistered(final String jdbcDriver) throws Exception {
        if (jdbcDriver == null || jdbcDriver.isBlank()) {
            return;
        }
        final Class<?> loaded = Class.forName(jdbcDriver.trim());
        if (!Driver.class.isAssignableFrom(loaded)) {
            throw new IllegalArgumentException("Configured JDBC driver is not a java.sql.Driver: " + jdbcDriver);
        }
    }

    private String jdbcUrl(final String jdbcUrlBase, final String databaseName) {
        final String base = jdbcUrlBase == null ? "" : jdbcUrlBase.trim();
        if (base.isBlank()) {
            return base;
        }
        if (base.contains("databaseName=")) {
            return base;
        }
        final String separator = base.contains(";") ? ";" : ";";
        return base + separator + "databaseName=" + databaseName;
    }
}
