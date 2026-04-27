package com.danhaywood.sqlcomparer.webapp.validation;

import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@Service
public class SqlServerConnectivityValidationService {

    private final WebappComparisonProperties properties;

    public SqlServerConnectivityValidationService(final WebappComparisonProperties properties) {
        this.properties = properties;
    }

    public void validateConfiguredTargets() {
        final WebappComparisonProperties.Connection connection = properties.getConnection();
        final String server = required(connection.getServer(), "connection.server");
        final String username = required(connection.getUsername(), "connection.username");
        final String password = required(connection.getPassword(), "connection.password");
        final String leftDatabase = required(connection.getLeftDatabase(), "connection.left-database");
        final String rightDatabase = required(connection.getRightDatabase(), "connection.right-database");

        try (Connection master = openConnection(server, "master", username, password)) {
            ensureDatabaseExists(master, leftDatabase);
            ensureDatabaseExists(master, rightDatabase);
        } catch (SQLException ex) {
            throw mapConnectionError(server, ex);
        }

        validateDatabaseReachable(server, leftDatabase, username, password);
        validateDatabaseReachable(server, rightDatabase, username, password);
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
            final String databaseName,
            final String username,
            final String password) {
        try (Connection ignored = openConnection(server, databaseName, username, password)) {
            // no-op
        } catch (SQLException ex) {
            throw mapConnectionError(server, ex);
        }
    }

    private Connection openConnection(
            final String server,
            final String databaseName,
            final String username,
            final String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl(server, databaseName), username, password);
    }

    private String jdbcUrl(final String server, final String databaseName) {
        return "jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true"
                .formatted(server, databaseName);
    }

    private SqlServerConnectivityValidationException mapConnectionError(final String server, final SQLException ex) {
        final String message = (ex.getMessage() == null ? "" : ex.getMessage()).toLowerCase(Locale.ROOT);
        if (message.contains("login failed")) {
            return new SqlServerConnectivityValidationException(
                    "Authentication failed for configured SQL Server credentials.", ex);
        }
        if (message.contains("tcp/ip connection") || message.contains("connection refused") || message.contains("timed out")) {
            return new SqlServerConnectivityValidationException(
                    "Unable to reach SQL Server at configured server '%s'.".formatted(server), ex);
        }
        return new SqlServerConnectivityValidationException(
                "SQL Server connectivity validation failed: " + ex.getMessage(), ex);
    }

    private String required(final String value, final String propertyName) {
        if (value == null || value.isBlank()) {
            throw new SqlServerConnectivityValidationException("Missing required property: sqlcomparer.webapp.comparison." + propertyName);
        }
        return value.trim();
    }
}
