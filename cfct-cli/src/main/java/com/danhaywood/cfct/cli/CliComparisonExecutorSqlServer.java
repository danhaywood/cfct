package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.MultiTableComparisonService;

import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;

@Service
public class CliComparisonExecutorSqlServer implements CliComparisonExecutor {

    private final MultiTableComparisonService comparer;
    private final CliComparisonReportRenderer renderer;

    public CliComparisonExecutorSqlServer(
            final MultiTableComparisonService comparer,
            final CliComparisonReportRenderer renderer) {
        this.comparer = comparer;
        this.renderer = renderer;
    }

    @Override
    public CliExecutionOutput execute(final CliArguments arguments) throws Exception {
        return execute(arguments, System.err);
    }

    @Override
    public CliExecutionOutput execute(final CliArguments arguments, final PrintStream err) throws Exception {
        final String leftJdbcUrl = jdbcUrl(arguments.server(), arguments.leftDatabase());
        final String rightJdbcUrl = jdbcUrl(arguments.server(), arguments.rightDatabase());
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
            final var result = comparer.compare(
                    leftConnection,
                    rightConnection,
                    new MultiTableComparisonRequest(arguments.tables(), options));
            err.flush();
            return renderer.render(result, arguments.outputFormat());
        }
    }

    protected Connection openConnection(final String jdbcUrl, final String username, final String password) throws Exception {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private String jdbcUrl(final String server, final String databaseName) {
        return "jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true"
                .formatted(server, databaseName);
    }
}
