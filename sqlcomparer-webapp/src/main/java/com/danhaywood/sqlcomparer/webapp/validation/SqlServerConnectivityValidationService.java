package com.danhaywood.cfct.webapp.validation;

import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@Service
public class SqlServerConnectivityValidationService {

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
            throw mapConnectionError(context.server(), ex);
        }

        validateDatabaseReachable(context.server(), dataSources.left());
        validateDatabaseReachable(context.server(), dataSources.right());
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
}
