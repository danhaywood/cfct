package com.danhaywood.sqlcomparer.webapp.e2e;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

final class PlaywrightSqlServerFixture {

    static final String ELIGIBLE_TABLE = "Supplier";
    static final String SECOND_ELIGIBLE_TABLE = "Product";
    static final String INELIGIBLE_TABLE = "PurchaseOrderWithoutBusinessKey";
    static final String COMMAND_INTERACTION_ID = "11111111-1111-1111-1111-111111111111";

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
        executeSql(databaseName, "CREATE TABLE dbo." + ELIGIBLE_TABLE + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, name NVARCHAR(80) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + ELIGIBLE_TABLE + "_PK ON dbo." + ELIGIBLE_TABLE + "(reference);");

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + SECOND_ELIGIBLE_TABLE + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + SECOND_ELIGIBLE_TABLE + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, name NVARCHAR(80) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + SECOND_ELIGIBLE_TABLE + "_PK ON dbo." + SECOND_ELIGIBLE_TABLE + "(reference);");

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + INELIGIBLE_TABLE + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + INELIGIBLE_TABLE + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, [version] DATETIME2(3) NOT NULL);");

        executeSql(databaseName, "IF SCHEMA_ID('causewayExtCommandLog') IS NULL EXEC('CREATE SCHEMA causewayExtCommandLog');");
        executeSql(databaseName, "DROP TABLE IF EXISTS causewayExtCommandLog.CommandLogEntry;");
        executeSql(databaseName, "CREATE TABLE causewayExtCommandLog.CommandLogEntry (interactionId UNIQUEIDENTIFIER NOT NULL PRIMARY KEY, executeIn VARCHAR(10) NOT NULL, logicalMemberIdentifier VARCHAR(255) NOT NULL, [timestamp] DATETIME2 NOT NULL, target VARCHAR(1500) NOT NULL, replayState VARCHAR(20) NOT NULL);");

        if (databaseName.contains("left")) {
            executeSql(databaseName, "INSERT INTO dbo." + ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('SUP-001','Supplier One', SYSDATETIME()), ('SUP-002','Supplier Two L', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + SECOND_ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('PRD-001','Product One', SYSDATETIME()), ('PRD-LEFT','Product Left Only', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO causewayExtCommandLog.CommandLogEntry (interactionId, executeIn, logicalMemberIdentifier, [timestamp], target, replayState) VALUES ('" + COMMAND_INTERACTION_ID + "', 'FOREGROUND', 'supplier.Supplier#registerProduct', SYSDATETIME(), 'supplier.Supplier:301', 'EXPORTED');");
        } else {
            executeSql(databaseName, "INSERT INTO dbo." + ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('SUP-001','Supplier One', SYSDATETIME()), ('SUP-002','Supplier Two R', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + SECOND_ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('PRD-001','Product One Changed', SYSDATETIME()), ('PRD-RIGHT','Product Right Only', SYSDATETIME());");
        }
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
