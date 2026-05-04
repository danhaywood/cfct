package com.danhaywood.cfct.webapp.validation;

import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

@Service
public class SqlServerConnectivityValidationService {

    private static final List<String> REQUIRED_TARGET_OBJECTS = List.of(
            "causewayExtCommandLog.CommandLogEntry",
            "causewayExtAuditTrail.AuditTrailEntry",
            "util.LogicalTypeTableMapping");

    private final WebappDataSourceConfiguration dataSourceConfiguration;

    public SqlServerConnectivityValidationService(final WebappDataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    public void validate(final AuthenticatedConnectionContext context) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(context);

        try (Connection master = dataSources.master().getConnection()) {
            ensureDatabaseExists(master, context.leftDatabase());
            ensureDatabaseExists(master, context.rightDatabase());
        } catch (SQLException ex) {
            throw mapConnectionError(context.jdbcUrl(), ex);
        }

        validateDatabaseReachable(context.jdbcUrl(), dataSources.left());
        validateDatabaseReachable(context.jdbcUrl(), dataSources.right());
        validateRequiredTargetObjects(context.jdbcUrl(), dataSources.right());
    }

    private void ensureDatabaseExists(final Connection masterConnection, final String databaseName) throws SQLException {
        final String sql = "SELECT COUNT(*) FROM sys.databases WHERE name = ?";
        try (PreparedStatement statement = masterConnection.prepareStatement(sql)) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next() || resultSet.getInt(1) == 0) {
                    throw new SqlServerConnectivityValidationException("Configured database does not exist: " + databaseName);
                }
            }
        }
    }

    private void validateDatabaseReachable(
            final String server,
            final javax.sql.DataSource dataSource) {
        try (Connection ignored = dataSource.getConnection()) {
            // no-op
        } catch (SQLException ex) {
            throw mapConnectionError(server, ex);
        }
    }

    private void validateRequiredTargetObjects(
            final String server,
            final javax.sql.DataSource targetDataSource) {
        final Set<String> presentObjects = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        try (Connection connection = targetDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT TABLE_SCHEMA, TABLE_NAME
                     FROM INFORMATION_SCHEMA.TABLES
                     WHERE TABLE_TYPE IN ('BASE TABLE', 'VIEW')
                     """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                presentObjects.add(resultSet.getString("TABLE_SCHEMA") + "." + resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException ex) {
            throw mapConnectionError(server, ex);
        }

        final List<String> missingObjects = REQUIRED_TARGET_OBJECTS.stream()
                .filter(required -> !presentObjects.contains(required))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        if (!missingObjects.isEmpty()) {
            throw new SqlServerConnectivityValidationException(
                    "Required target system objects are missing: " + String.join(", ", missingObjects));
        }
    }

    private SqlServerConnectivityValidationException mapConnectionError(final String jdbcUrl, final SQLException ex) {
        final String message = (ex.getMessage() == null ? "" : ex.getMessage()).toLowerCase(Locale.ROOT);
        if (message.contains("login failed")) {
            return new SqlServerConnectivityValidationException(
                    "Authentication failed for configured SQL Server credentials.", ex);
        }
        if (message.contains("tcp/ip connection") || message.contains("connection refused") || message.contains("timed out")) {
            return new SqlServerConnectivityValidationException(
                    "Unable to reach SQL Server using configured JDBC URL '%s'.".formatted(jdbcUrl), ex);
        }
        return new SqlServerConnectivityValidationException(
                "SQL Server connectivity validation failed: " + ex.getMessage(), ex);
    }
}
