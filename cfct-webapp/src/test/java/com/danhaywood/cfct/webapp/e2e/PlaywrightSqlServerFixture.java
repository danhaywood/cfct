package com.danhaywood.cfct.webapp.e2e;

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
    static final String EQUAL_TABLE = "CustomerAddress";
    static final String COMMAND_INTERACTION_ID = "11111111-1111-1111-1111-111111111111";
    static final String SECOND_COMMAND_INTERACTION_ID = "22222222-2222-2222-2222-222222222222";

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

    static String jdbcUrl() {
        return "jdbc:sqlserver://%s;encrypt=false;trustServerCertificate=true".formatted(server());
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

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + EQUAL_TABLE + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + EQUAL_TABLE + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, customerReference NVARCHAR(40) NOT NULL, line1 NVARCHAR(120) NOT NULL, city NVARCHAR(80) NOT NULL, postcode NVARCHAR(20) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + EQUAL_TABLE + "_PK ON dbo." + EQUAL_TABLE + "(reference);");

        executeSql(databaseName, "IF SCHEMA_ID('causewayExtCommandLog') IS NULL EXEC('CREATE SCHEMA causewayExtCommandLog');");
        executeSql(databaseName, "IF SCHEMA_ID('causewayExtAuditTrail') IS NULL EXEC('CREATE SCHEMA causewayExtAuditTrail');");
        executeSql(databaseName, "IF SCHEMA_ID('util') IS NULL EXEC('CREATE SCHEMA util');");

        executeSql(databaseName, "DROP TABLE IF EXISTS causewayExtAuditTrail.AuditTrailEntry;");
        executeSql(databaseName, "DROP TABLE IF EXISTS causewayExtCommandLog.CommandLogEntry;");
        executeSql(databaseName, "DROP TABLE IF EXISTS util.LogicalTypeTableMapping;");

        executeSql(databaseName, "CREATE TABLE causewayExtCommandLog.CommandLogEntry (interactionId UNIQUEIDENTIFIER NOT NULL PRIMARY KEY, executeIn VARCHAR(10) NOT NULL, logicalMemberIdentifier VARCHAR(255) NOT NULL, [timestamp] DATETIME2 NOT NULL, target VARCHAR(1500) NOT NULL, replayState VARCHAR(20) NOT NULL);");
        executeSql(databaseName, "CREATE TABLE causewayExtAuditTrail.AuditTrailEntry (interactionId UNIQUEIDENTIFIER NOT NULL, sequence INT NOT NULL, target VARCHAR(1500) NOT NULL, propertyId VARCHAR(100) NOT NULL, CONSTRAINT PK_AuditTrailEntry PRIMARY KEY (interactionId, sequence, target, propertyId));");
        executeSql(databaseName, "CREATE TABLE util.LogicalTypeTableMapping (logicalTypeName NVARCHAR(255) NULL, qualifiedName NVARCHAR(255) NOT NULL);");

        if (databaseName.contains("left")) {
            executeSql(databaseName, "INSERT INTO dbo." + ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('SUP-001','Supplier One', SYSDATETIME()), ('SUP-002','Supplier Two L', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + SECOND_ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('PRD-001','Product One', SYSDATETIME()), ('PRD-LEFT','Product Left Only', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + EQUAL_TABLE + " (reference, customerReference, line1, city, postcode, [version]) VALUES ('ADDR-001', 'CUS-001', '10 High Street', 'Bristol', 'BS1 1AA', SYSDATETIME()), ('ADDR-002', 'CUS-002', '22 River Road', 'Bath', 'BA1 2BB', SYSDATETIME());");

            executeSql(databaseName, "INSERT INTO util.LogicalTypeTableMapping (logicalTypeName, qualifiedName) VALUES ('supplier.Supplier', 'dbo.Supplier'), ('product.Product', 'dbo.Product'), ('customer.CustomerAddress', 'dbo.CustomerAddress');");
            executeSql(databaseName, "INSERT INTO causewayExtCommandLog.CommandLogEntry (interactionId, executeIn, logicalMemberIdentifier, [timestamp], target, replayState) VALUES ('" + COMMAND_INTERACTION_ID + "', 'FOREGROUND', 'supplier.Supplier#registerProduct', DATEADD(SECOND, -1, SYSDATETIME()), 'supplier.Supplier:301', 'EXPORTED');");
            executeSql(databaseName, "INSERT INTO causewayExtCommandLog.CommandLogEntry (interactionId, executeIn, logicalMemberIdentifier, [timestamp], target, replayState) VALUES ('" + SECOND_COMMAND_INTERACTION_ID + "', 'FOREGROUND', 'product.Product#changeStatus', SYSDATETIME(), 'product.Product:701', 'EXPORTED');");
            executeSql(databaseName, "INSERT INTO causewayExtAuditTrail.AuditTrailEntry (interactionId, sequence, target, propertyId) VALUES ('" + COMMAND_INTERACTION_ID + "', 1, 'supplier.Supplier:301', 'name');");
            executeSql(databaseName, "INSERT INTO causewayExtAuditTrail.AuditTrailEntry (interactionId, sequence, target, propertyId) VALUES ('" + SECOND_COMMAND_INTERACTION_ID + "', 1, 'product.Product:701', 'name');");
        } else {
            executeSql(databaseName, "INSERT INTO dbo." + ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('SUP-001','Supplier One', SYSDATETIME()), ('SUP-002','Supplier Two R', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + SECOND_ELIGIBLE_TABLE + " (reference, name, [version]) VALUES ('PRD-001','Product One Changed', SYSDATETIME()), ('PRD-RIGHT','Product Right Only', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + EQUAL_TABLE + " (reference, customerReference, line1, city, postcode, [version]) VALUES ('ADDR-001', 'CUS-001', '10 High Street', 'Bristol', 'BS1 1AA', SYSDATETIME()), ('ADDR-002', 'CUS-002', '22 River Road', 'Bath', 'BA1 2BB', SYSDATETIME());");
        }
    }

    private static void executeAdminSql(final String sql) {
        executeSql("master", sql);
    }

    private static void executeSql(final String databaseName, final String sql) {
        try (Connection connection = DriverManager.getConnection(
                "%s;databaseName=%s".formatted(jdbcUrl(), databaseName),
                username(),
                password());
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute SQL against Playwright SQL Server fixture.", ex);
        }
    }
}
