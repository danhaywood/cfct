package com.danhaywood.cfct.webapp.validation;

import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
        createRequiredTargetObjects("right_validation_db");

        final SqlServerConnectivityValidationService service = service();

        assertThatCode(() -> service.validate(context(
                hostAndPort(),
                SQL_SERVER.getUsername(),
                SQL_SERVER.getPassword(),
                "left_validation_db",
                "right_validation_db"))).doesNotThrowAnyException();
    }

    @Test
    void failsWhenConfiguredDatabaseIsMissing() throws Exception {
        createDatabaseIfMissing("left_existing_db");

        final SqlServerConnectivityValidationService service = service();

        assertThatThrownBy(() -> service.validate(context(
                hostAndPort(),
                SQL_SERVER.getUsername(),
                SQL_SERVER.getPassword(),
                "left_existing_db",
                "missing_db")))
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Configured database does not exist: missing_db");
    }

    @Test
    void failsWithConnectivityMessageForUnreachableServer() {
        final SqlServerConnectivityValidationService service = service();

        assertThatThrownBy(() -> service.validate(context("localhost:1", "sa", "bad-password", "left_db", "right_db")))
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Unable to reach SQL Server");
    }

    @Test
    void failsWithAuthenticationMessageForInvalidCredentials() throws Exception {
        createDatabaseIfMissing("left_validation_db");
        createDatabaseIfMissing("right_validation_db");
        createRequiredTargetObjects("right_validation_db");

        final SqlServerConnectivityValidationService service = service();

        assertThatThrownBy(() -> service.validate(context(
                hostAndPort(),
                SQL_SERVER.getUsername(),
                "wrong-password",
                "left_validation_db",
                "right_validation_db")))
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Authentication failed");
    }

    @Test
    void failsWhenRequiredTargetSystemObjectsAreMissing() throws Exception {
        createDatabaseIfMissing("left_required_db");
        createDatabaseIfMissing("right_required_db");

        final SqlServerConnectivityValidationService service = service();

        assertThatThrownBy(() -> service.validate(context(
                hostAndPort(),
                SQL_SERVER.getUsername(),
                SQL_SERVER.getPassword(),
                "left_required_db",
                "right_required_db")))
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Required target system objects are missing")
                .hasMessageContaining("causewayExtCommandLog.CommandLogEntry")
                .hasMessageContaining("causewayExtAuditTrail.AuditTrailEntry")
                .hasMessageContaining("util.LogicalTypeTableMapping");
    }

    @Test
    void validatesWhenRequiredTargetSystemObjectsExistAsViews() throws Exception {
        createDatabaseIfMissing("left_required_view_db");
        createDatabaseIfMissing("right_required_view_db");
        createRequiredTargetObjectsAsViews("right_required_view_db");

        final SqlServerConnectivityValidationService service = service();

        assertThatCode(() -> service.validate(context(
                hostAndPort(),
                SQL_SERVER.getUsername(),
                SQL_SERVER.getPassword(),
                "left_required_view_db",
                "right_required_view_db"))).doesNotThrowAnyException();
    }

    private static SqlServerConnectivityValidationService service() {
        return new SqlServerConnectivityValidationService(new WebappDataSourceConfiguration());
    }

    private static AuthenticatedConnectionContext context(
            final String server,
            final String username,
            final String password,
            final String leftDatabase,
            final String rightDatabase) {
        return new AuthenticatedConnectionContext(
                "jdbc:sqlserver://" + server + ";encrypt=false;trustServerCertificate=true",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                username,
                password,
                leftDatabase,
                rightDatabase);
    }

    private static void createDatabaseIfMissing(final String databaseName) throws SQLException {
        final String sql = "IF DB_ID('%s') IS NULL CREATE DATABASE [%s]".formatted(databaseName, databaseName);
        try (Connection connection = adminConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void createRequiredTargetObjects(final String databaseName) throws SQLException {
        final List<String> ddl = List.of(
                "IF SCHEMA_ID('causewayExtCommandLog') IS NULL EXEC('CREATE SCHEMA causewayExtCommandLog')",
                "IF SCHEMA_ID('causewayExtAuditTrail') IS NULL EXEC('CREATE SCHEMA causewayExtAuditTrail')",
                "IF SCHEMA_ID('util') IS NULL EXEC('CREATE SCHEMA util')",
                "IF OBJECT_ID('causewayExtCommandLog.CommandLogEntry', 'U') IS NULL CREATE TABLE causewayExtCommandLog.CommandLogEntry (id INT NOT NULL)",
                "IF OBJECT_ID('causewayExtAuditTrail.AuditTrailEntry', 'U') IS NULL CREATE TABLE causewayExtAuditTrail.AuditTrailEntry (id INT NOT NULL)",
                "IF OBJECT_ID('util.LogicalTypeTableMapping', 'U') IS NULL CREATE TABLE util.LogicalTypeTableMapping (id INT NOT NULL)"
        );
        executeInDatabase(databaseName, ddl);
    }

    private static void createRequiredTargetObjectsAsViews(final String databaseName) throws SQLException {
        final List<String> ddl = List.of(
                "IF SCHEMA_ID('causewayExtCommandLog') IS NULL EXEC('CREATE SCHEMA causewayExtCommandLog')",
                "IF SCHEMA_ID('causewayExtAuditTrail') IS NULL EXEC('CREATE SCHEMA causewayExtAuditTrail')",
                "IF SCHEMA_ID('util') IS NULL EXEC('CREATE SCHEMA util')",
                "IF OBJECT_ID('causewayExtCommandLog.CommandLogEntry', 'V') IS NOT NULL DROP VIEW causewayExtCommandLog.CommandLogEntry",
                "IF OBJECT_ID('causewayExtAuditTrail.AuditTrailEntry', 'V') IS NOT NULL DROP VIEW causewayExtAuditTrail.AuditTrailEntry",
                "IF OBJECT_ID('util.LogicalTypeTableMapping', 'V') IS NOT NULL DROP VIEW util.LogicalTypeTableMapping",
                "IF OBJECT_ID('causewayExtCommandLog.CommandLogEntry', 'U') IS NOT NULL DROP TABLE causewayExtCommandLog.CommandLogEntry",
                "IF OBJECT_ID('causewayExtAuditTrail.AuditTrailEntry', 'U') IS NOT NULL DROP TABLE causewayExtAuditTrail.AuditTrailEntry",
                "IF OBJECT_ID('util.LogicalTypeTableMapping', 'U') IS NOT NULL DROP TABLE util.LogicalTypeTableMapping",
                "CREATE VIEW causewayExtCommandLog.CommandLogEntry AS SELECT CAST(1 AS INT) AS id",
                "CREATE VIEW causewayExtAuditTrail.AuditTrailEntry AS SELECT CAST(1 AS INT) AS id",
                "CREATE VIEW util.LogicalTypeTableMapping AS SELECT CAST(1 AS INT) AS id"
        );
        executeInDatabase(databaseName, ddl);
    }

    private static void executeInDatabase(final String databaseName, final List<String> statements) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true"
                        .formatted(hostAndPort(), databaseName),
                SQL_SERVER.getUsername(),
                SQL_SERVER.getPassword());
             Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
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
