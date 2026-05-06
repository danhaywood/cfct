package com.danhaywood.cfct.webapp.e2e;

import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class PlaywrightSqlServerFixture {

    private final MSSQLServerContainer<?> sqlServer;

    PlaywrightSqlServerFixture(final MSSQLServerContainer<?> sqlServer) {
        this.sqlServer = sqlServer;
    }

    String eligibleTable() {
        return "Supplier";
    }

    String secondEligibleTable() {
        return "Product";
    }

    String ineligibleTable() {
        return "PurchaseOrderWithoutBusinessKey";
    }

    String equalTable() {
        return "CustomerAddress";
    }

    String commandInteractionId() {
        return "11111111-1111-1111-1111-111111111111";
    }

    String secondCommandInteractionId() {
        return "22222222-2222-2222-2222-222222222222";
    }

    String server() {
        return "%s:%d".formatted(sqlServer.getHost(), sqlServer.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT));
    }

    String jdbcUrl() {
        return "jdbc:sqlserver://%s;encrypt=false;trustServerCertificate=true".formatted(server());
    }

    String username() {
        return sqlServer.getUsername();
    }

    String password() {
        return sqlServer.getPassword();
    }

    void createDatabaseIfMissing(final String databaseName) {
        final String sql = "IF DB_ID('%s') IS NULL CREATE DATABASE [%s]".formatted(databaseName, databaseName);
        executeAdminSql(sql);
    }

    void prepareManualSelectionTables(final String databaseName) {
        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + eligibleTable() + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + eligibleTable() + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, name NVARCHAR(80) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + eligibleTable() + "_PK ON dbo." + eligibleTable() + "(reference);");

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + secondEligibleTable() + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + secondEligibleTable() + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, name NVARCHAR(80) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + secondEligibleTable() + "_PK ON dbo." + secondEligibleTable() + "(reference);");

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + ineligibleTable() + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + ineligibleTable() + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, [version] DATETIME2(3) NOT NULL);");

        executeSql(databaseName, "DROP TABLE IF EXISTS dbo." + equalTable() + ";");
        executeSql(databaseName, "CREATE TABLE dbo." + equalTable() + " (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, reference NVARCHAR(40) NOT NULL, customerReference NVARCHAR(40) NOT NULL, line1 NVARCHAR(120) NOT NULL, city NVARCHAR(80) NOT NULL, postcode NVARCHAR(20) NOT NULL, [version] DATETIME2(3) NOT NULL);");
        executeSql(databaseName, "CREATE UNIQUE INDEX " + equalTable() + "_PK ON dbo." + equalTable() + "(reference);");

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
            executeSql(databaseName, "INSERT INTO dbo." + eligibleTable() + " (reference, name, [version]) VALUES ('SUP-001','Supplier One', SYSDATETIME()), ('SUP-002','Supplier Two L', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + secondEligibleTable() + " (reference, name, [version]) VALUES ('PRD-001','Product One', SYSDATETIME()), ('PRD-LEFT','Product Left Only', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + equalTable() + " (reference, customerReference, line1, city, postcode, [version]) VALUES ('ADDR-001', 'CUS-001', '10 High Street', 'Bristol', 'BS1 1AA', SYSDATETIME()), ('ADDR-002', 'CUS-002', '22 River Road', 'Bath', 'BA1 2BB', SYSDATETIME());");

            executeSql(databaseName, "INSERT INTO util.LogicalTypeTableMapping (logicalTypeName, qualifiedName) VALUES ('supplier.Supplier', 'dbo.Supplier'), ('product.Product', 'dbo.Product'), ('customer.CustomerAddress', 'dbo.CustomerAddress');");
            executeSql(databaseName, "INSERT INTO causewayExtCommandLog.CommandLogEntry (interactionId, executeIn, logicalMemberIdentifier, [timestamp], target, replayState) VALUES ('" + commandInteractionId() + "', 'FOREGROUND', 'supplier.Supplier#registerProduct', DATEADD(SECOND, -1, SYSDATETIME()), 'supplier.Supplier:301', 'OK');");
            executeSql(databaseName, "INSERT INTO causewayExtCommandLog.CommandLogEntry (interactionId, executeIn, logicalMemberIdentifier, [timestamp], target, replayState) VALUES ('" + secondCommandInteractionId() + "', 'FOREGROUND', 'product.Product#changeStatus', SYSDATETIME(), 'product.Product:701', 'PENDING');");
            executeSql(databaseName, "INSERT INTO causewayExtAuditTrail.AuditTrailEntry (interactionId, sequence, target, propertyId) VALUES ('" + commandInteractionId() + "', 1, 'supplier.Supplier:301', 'name');");
            executeSql(databaseName, "INSERT INTO causewayExtAuditTrail.AuditTrailEntry (interactionId, sequence, target, propertyId) VALUES ('" + secondCommandInteractionId() + "', 1, 'product.Product:701', 'name');");
        } else {
            executeSql(databaseName, "INSERT INTO dbo." + eligibleTable() + " (reference, name, [version]) VALUES ('SUP-001','Supplier One', SYSDATETIME()), ('SUP-002','Supplier Two R', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + secondEligibleTable() + " (reference, name, [version]) VALUES ('PRD-001','Product One Changed', SYSDATETIME()), ('PRD-RIGHT','Product Right Only', SYSDATETIME());");
            executeSql(databaseName, "INSERT INTO dbo." + equalTable() + " (reference, customerReference, line1, city, postcode, [version]) VALUES ('ADDR-001', 'CUS-001', '10 High Street', 'Bristol', 'BS1 1AA', SYSDATETIME()), ('ADDR-002', 'CUS-002', '22 River Road', 'Bath', 'BA1 2BB', SYSDATETIME());");
        }
    }

    private void executeAdminSql(final String sql) {
        executeSql("master", sql);
    }

    private void executeSql(final String databaseName, final String sql) {
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
