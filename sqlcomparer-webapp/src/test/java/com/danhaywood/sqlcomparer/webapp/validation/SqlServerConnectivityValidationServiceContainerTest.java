package com.danhaywood.sqlcomparer.webapp.validation;

import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers(disabledWithoutDocker = true)
class SqlServerConnectivityValidationServiceContainerTest {

    @Container
    static final MSSQLServerContainer<?> SQL_SERVER = new MSSQLServerContainer<>(
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
            .acceptLicense()
            .withEnv("MSSQL_PID", "Developer")
            .withPassword("Str0ng_password!123");

    @Test
    void validatesWhenConfiguredDatabasesExist() throws Exception {
        createDatabaseIfMissing("left_validation_db");
        createDatabaseIfMissing("right_validation_db");

        final SqlServerConnectivityValidationService service = service(
                hostAndPort(), SQL_SERVER.getUsername(), SQL_SERVER.getPassword(), "left_validation_db", "right_validation_db");

        assertThatCode(service::validateConfiguredTargets).doesNotThrowAnyException();
    }

    @Test
    void failsWhenConfiguredDatabaseIsMissing() throws Exception {
        createDatabaseIfMissing("left_existing_db");

        final SqlServerConnectivityValidationService service = service(
                hostAndPort(), SQL_SERVER.getUsername(), SQL_SERVER.getPassword(), "left_existing_db", "missing_db");

        assertThatThrownBy(service::validateConfiguredTargets)
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Configured database does not exist: missing_db");
    }

    @Test
    void failsWithConnectivityMessageForUnreachableServer() {
        final SqlServerConnectivityValidationService service = service("localhost:1", "sa", "bad-password", "left_db", "right_db");

        assertThatThrownBy(service::validateConfiguredTargets)
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Unable to reach SQL Server");
    }

    @Test
    void failsWithAuthenticationMessageForInvalidCredentials() throws Exception {
        createDatabaseIfMissing("left_validation_db");
        createDatabaseIfMissing("right_validation_db");

        final SqlServerConnectivityValidationService service = service(
                hostAndPort(), SQL_SERVER.getUsername(), "wrong-password", "left_validation_db", "right_validation_db");

        assertThatThrownBy(service::validateConfiguredTargets)
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Authentication failed");
    }

    private static SqlServerConnectivityValidationService service(
            final String server,
            final String username,
            final String password,
            final String leftDatabase,
            final String rightDatabase) {
        final WebappComparisonProperties properties = properties(server, username, password, leftDatabase, rightDatabase);
        final WebappDataSources dataSources = new WebappDataSources(
                WebappDataSourceConfiguration.sqlServerDataSource(properties, "master"),
                WebappDataSourceConfiguration.sqlServerDataSource(properties, leftDatabase),
                WebappDataSourceConfiguration.sqlServerDataSource(properties, rightDatabase));
        return new SqlServerConnectivityValidationService(properties, dataSources);
    }

    private static WebappComparisonProperties properties(
            final String server,
            final String username,
            final String password,
            final String leftDatabase,
            final String rightDatabase) {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        final WebappComparisonProperties.Connection connection = new WebappComparisonProperties.Connection();
        connection.setServer(server);
        connection.setUsername(username);
        connection.setPassword(password);
        connection.setLeftDatabase(leftDatabase);
        connection.setRightDatabase(rightDatabase);
        properties.setConnection(connection);
        return properties;
    }

    private static void createDatabaseIfMissing(final String databaseName) throws SQLException {
        final String sql = "IF DB_ID('%s') IS NULL CREATE DATABASE [%s]".formatted(databaseName, databaseName);
        try (Connection connection = adminConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static Connection adminConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:sqlserver://%s;databaseName=master;encrypt=false;trustServerCertificate=true"
                        .formatted(hostAndPort()),
                SQL_SERVER.getUsername(),
                SQL_SERVER.getPassword());
    }

    private static String hostAndPort() {
        return "%s:%d".formatted(SQL_SERVER.getHost(), SQL_SERVER.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT));
    }
}
