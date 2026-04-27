package com.danhaywood.sqlcomparer.webapp.config;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class WebappDataSourceConfiguration {

    public static final String MASTER_DATA_SOURCE = "webappMasterDataSource";
    public static final String LEFT_DATA_SOURCE = "webappLeftDataSource";
    public static final String RIGHT_DATA_SOURCE = "webappRightDataSource";

    @Bean(MASTER_DATA_SOURCE)
    public DataSource webappMasterDataSource(final WebappComparisonProperties properties) {
        return sqlServerDataSource(properties, "master");
    }

    @Bean(LEFT_DATA_SOURCE)
    public DataSource webappLeftDataSource(final WebappComparisonProperties properties) {
        return sqlServerDataSource(properties, properties.getConnection().getLeftDatabase());
    }

    @Bean(RIGHT_DATA_SOURCE)
    public DataSource webappRightDataSource(final WebappComparisonProperties properties) {
        return sqlServerDataSource(properties, properties.getConnection().getRightDatabase());
    }

    @Bean
    public WebappDataSources webappDataSources(
            @Qualifier(MASTER_DATA_SOURCE) final DataSource master,
            @Qualifier(LEFT_DATA_SOURCE) final DataSource left,
            @Qualifier(RIGHT_DATA_SOURCE) final DataSource right) {
        return new WebappDataSources(master, left, right);
    }

    public static DataSource sqlServerDataSource(final WebappComparisonProperties properties, final String databaseName) {
        final WebappComparisonProperties.Connection connection = properties.getConnection();
        final SQLServerDataSource dataSource = new SQLServerDataSource();
        applyServer(dataSource, connection.getServer());
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(connection.getUsername());
        dataSource.setPassword(connection.getPassword());
        dataSource.setEncrypt("false");
        dataSource.setTrustServerCertificate(true);
        return dataSource;
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
