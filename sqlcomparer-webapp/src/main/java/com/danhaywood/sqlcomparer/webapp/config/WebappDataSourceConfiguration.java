package com.danhaywood.sqlcomparer.webapp.config;

import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContext;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class WebappDataSourceConfiguration {

    public WebappDataSources dataSourcesFor(final AuthenticatedConnectionContext context) {
        return new WebappDataSources(
                sqlServerDataSource(context.server(), context.username(), context.password(), "master"),
                sqlServerDataSource(context.server(), context.username(), context.password(), context.leftDatabase()),
                sqlServerDataSource(context.server(), context.username(), context.password(), context.rightDatabase()));
    }

    public static DataSource sqlServerDataSource(
            final String server,
            final String username,
            final String password,
            final String databaseName) {
        final SQLServerDataSource dataSource = new SQLServerDataSource();
        applyServer(dataSource, server);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setEncrypt("false");
        dataSource.setTrustServerCertificate(true);
        return dataSource;
    }

    public static DataSource sqlServerDataSource(final WebappComparisonProperties properties, final String databaseName) {
        final WebappComparisonProperties.Connection connection = properties.getConnection();
        return sqlServerDataSource(
                connection.getServer(),
                connection.getUsername(),
                connection.getPassword(),
                databaseName);
    }

    private static void applyServer(final SQLServerDataSource dataSource, final String server) {
        if (server == null || server.isBlank()) {
            return;
        }
        final String trimmed = server.trim();
        final int separator = trimmed.lastIndexOf(':');
        if (separator > 0 && separator < trimmed.length() - 1) {
            dataSource.setServerName(trimmed.substring(0, separator));
            dataSource.setPortNumber(Integer.parseInt(trimmed.substring(separator + 1)));
            return;
        }
        dataSource.setServerName(trimmed);
    }
}
