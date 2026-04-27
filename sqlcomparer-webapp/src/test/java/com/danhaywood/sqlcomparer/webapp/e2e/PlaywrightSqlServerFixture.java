package com.danhaywood.sqlcomparer.webapp.e2e;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

final class PlaywrightSqlServerFixture {

    static final String ELIGIBLE_TABLE = "PlaywrightEligible";
    static final String INELIGIBLE_TABLE = "PlaywrightIneligible";

    private static final MSSQLServerContainer<?> SQL_SERVER = new MSSQLServerContainer<>(
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
            .acceptLicense()
            .withEnv("MSSQL_PID", "Developer")
            .withPassword("Str0ng_password!123");

    static {
        SQL_SERVER.start();
    }

    private PlaywrightSqlServerFixture() {
    }

    static String server() {
        return "%s:%d".formatted(SQL_SERVER.getHost(), SQL_SERVER.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT));
    }

    static String username() {
        return SQL_SERVER.getUsername();
    }

    static String password() {
        return SQL_SERVER.getPassword();
    }

    static void createDatabaseIfMissing(final String databaseName) {
        final String sql = "IF DB_ID('%s') IS NULL CREATE DATABASE [%s]".formatted(databaseName, databaseName);
        executeAdminSql(sql);
    }

    static void prepareManualSelectionTables(final String databaseName) {
        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + ELIGIBLE_TABLE + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + ELIGIBLE_TABLE + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + ELIGIBLE_TABLE + "_PK ON dbo." + ELIGIBLE_TABLE + "(reference);");

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + INELIGIBLE_TABLE + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + INELIGIBLE_TABLE + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, [version] DATETIME2(3) NOT NULL);");
    }

    private static void executeAdminSql(final String sql) {
        executeSql("master", sql);
    }

    private static void executeSql(final String databaseName, final String sql) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true".formatted(server(), databaseName),
                username(),
                password());
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute SQL against Playwright SQL Server fixture.", ex);
        }
    }
}
