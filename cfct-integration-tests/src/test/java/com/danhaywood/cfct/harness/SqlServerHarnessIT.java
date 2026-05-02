package com.danhaywood.cfct.harness;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class SqlServerHarnessIT {

    private static SqlServerTestHarness harness;

    @BeforeAll
    static void startHarness() {
        harness = new SqlServerTestHarness().start();
    }

    @AfterAll
    static void stopHarness() {
        if (harness != null) {
            harness.close();
        }
    }

    @ParameterizedTest
    @EnumSource(DatabaseSide.class)
    void connectsToEachLogicalDatabase(final DatabaseSide side) {
        assertThat(harness.queryForString(side, "SELECT DB_NAME()"))
                .isEqualTo(side.databaseName());
    }

    @Test
    void initializesRealisticPurchaseOrderFixture() {
        initializeFixture("purchase-order");

        assertThat(harness.queryForInt(DatabaseSide.LEFT, "SELECT COUNT(*) FROM dbo.PurchaseOrder"))
                .isEqualTo(5);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, "SELECT COUNT(*) FROM dbo.PurchaseOrder"))
                .isEqualTo(5);
        assertThat(harness.queryForString(DatabaseSide.LEFT, "SELECT STRING_AGG(reference, ',') WITHIN GROUP (ORDER BY reference) FROM dbo.PurchaseOrder"))
                .isEqualTo("PO-001,PO-002,PO-003,PO-005,PO-006");
        assertThat(harness.queryForString(DatabaseSide.RIGHT, "SELECT STRING_AGG(reference, ',') WITHIN GROUP (ORDER BY reference) FROM dbo.PurchaseOrder"))
                .isEqualTo("PO-001,PO-002,PO-004,PO-005,PO-006");
    }

    @Test
    void purchaseOrderFixtureMarksBusinessKeyWithUniqueIndex() {
        initializeFixture("purchase-order");

        assertThat(harness.queryForInt(DatabaseSide.LEFT, purchaseOrderBusinessKeyIndexColumnCountSql()))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, purchaseOrderBusinessKeyIndexColumnCountSql()))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.LEFT, purchaseOrderBusinessKeyIndexKeyColumnCountSql()))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, purchaseOrderBusinessKeyIndexKeyColumnCountSql()))
                .isEqualTo(1);
    }

    @Test
    void customerAddressFixtureSeedsCfctIgnoredExtendedProperty() {
        initializeFixture("customer-address");

        assertThat(harness.queryForInt(DatabaseSide.LEFT, cfctIgnoredExtendedPropertyCountSql("CustomerAddress", "postcode")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, cfctIgnoredExtendedPropertyCountSql("CustomerAddress", "postcode")))
                .isEqualTo(1);
    }

    @Test
    void purchaseOrderWithoutBusinessKeyFixtureHasNoBusinessKeyIndex() {
        initializeFixture("purchase-order-without-business-key");

        assertThat(harness.queryForInt(DatabaseSide.LEFT, tableExistsSql("PurchaseOrderWithoutBusinessKey")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, tableExistsSql("PurchaseOrderWithoutBusinessKey")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.LEFT, columnExistsSql("PurchaseOrderWithoutBusinessKey", "id")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.LEFT, columnExistsSql("PurchaseOrderWithoutBusinessKey", "reference")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.LEFT, columnExistsSql("PurchaseOrderWithoutBusinessKey", "status")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.LEFT, columnExistsSql("PurchaseOrderWithoutBusinessKey", "version")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.LEFT, businessKeyLikeUniqueIndexCountSql("PurchaseOrderWithoutBusinessKey")))
                .isEqualTo(0);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, businessKeyLikeUniqueIndexCountSql("PurchaseOrderWithoutBusinessKey")))
                .isEqualTo(0);
    }

    @Test
    void purchaseOrderFixtureUsesDatetime2VersionNotSqlServerRowversion() {
        initializeFixture("purchase-order");

        assertThat(harness.queryForString(DatabaseSide.LEFT, purchaseOrderVersionDataTypeSql()))
                .isEqualTo("datetime2");
        assertThat(harness.queryForString(DatabaseSide.RIGHT, purchaseOrderVersionDataTypeSql()))
                .isEqualTo("datetime2");
        assertThat(harness.queryForInt(DatabaseSide.LEFT, purchaseOrderRowversionColumnCountSql()))
                .isEqualTo(0);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, purchaseOrderRowversionColumnCountSql()))
                .isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(DatabaseSide.class)
    void purchaseOrderFixtureIncludesCausewayCommandAndAuditTables(final DatabaseSide side) {
        initializeFixture("purchase-order");

        assertThat(harness.queryForInt(side, tableExistsInSchemaSql("causewayExtCommandLog", "CommandLogEntry")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side, tableExistsInSchemaSql("causewayExtAuditTrail", "AuditTrailEntry")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side, tableExistsInSchemaSql("util", "LogicalTypeTableMapping")))
                .isEqualTo(1);

        assertThat(harness.queryForInt(side,
                primaryKeyColumnExistsSql("causewayExtCommandLog", "CommandLogEntry", "interactionId", 1)))
                .isEqualTo(1);

        assertThat(harness.queryForInt(side,
                primaryKeyColumnExistsSql("causewayExtAuditTrail", "AuditTrailEntry", "interactionId", 1)))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side,
                primaryKeyColumnExistsSql("causewayExtAuditTrail", "AuditTrailEntry", "sequence", 2)))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side,
                primaryKeyColumnExistsSql("causewayExtAuditTrail", "AuditTrailEntry", "target", 3)))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side,
                primaryKeyColumnExistsSql("causewayExtAuditTrail", "AuditTrailEntry", "propertyId", 4)))
                .isEqualTo(1);

        assertThat(harness.queryForInt(side,
                foreignKeyExistsSql("causewayExtAuditTrail", "AuditTrailEntry", "FK_AuditTrailEntry_CommandLogEntry_InteractionId")))
                .isEqualTo(1);

        assertThat(harness.queryForInt(side,
                columnExistsInSchemaSql("util", "LogicalTypeTableMapping", "logicalTypeName", "nvarchar")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side,
                columnExistsInSchemaSql("util", "LogicalTypeTableMapping", "qualifiedName", "nvarchar")))
                .isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(DatabaseSide.class)
    void purchaseOrderFixtureSeedsRegisterProductCommandAuditFootprint(final DatabaseSide side) {
        initializeFixture("purchase-order");

        assertThat(harness.queryForInt(side, registerProductCommandCountSql()))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side, registerProductSupplierTargetCountSql()))
                .isEqualTo(1);

        final String expectedReplayState = side == DatabaseSide.LEFT ? "EXPORTED" : "PENDING";
        assertThat(harness.queryForString(side, registerProductReplayStateSql()))
                .isEqualTo(expectedReplayState);

        final int expectedAuditRowCount = side == DatabaseSide.LEFT ? 3 : 0;
        assertThat(harness.queryForInt(side, registerProductAuditProductTargetCountSql()))
                .isEqualTo(expectedAuditRowCount);
    }

    @ParameterizedTest
    @EnumSource(DatabaseSide.class)
    void purchaseOrderFixtureSeedsLogicalTypeTableMappings(final DatabaseSide side) {
        initializeFixture("purchase-order");

        assertThat(harness.queryForInt(side, logicalTypeMappingCountSql("supplier.Supplier")))
                .isEqualTo(1);
        assertThat(harness.queryForInt(side, logicalTypeMappingCountSql("product.Product")))
                .isEqualTo(2);
        assertThat(harness.queryForInt(side, logicalTypeMappingCountSql("customer.CustomerAddress")))
                .isEqualTo(1);

        assertThat(harness.queryForString(side, logicalTypeQualifiedNamesSql("supplier.Supplier")))
                .isEqualTo("dbo.Supplier");
        assertThat(harness.queryForString(side, logicalTypeQualifiedNamesSql("product.Product")))
                .isEqualTo("dbo.Product,dbo.ProductInventory");
        assertThat(harness.queryForString(side, logicalTypeQualifiedNamesSql("customer.CustomerAddress")))
                .isEqualTo("dbo.CustomerAddress");
    }

    @ParameterizedTest
    @EnumSource(DatabaseSide.class)
    void purchaseOrderFixtureSeedsAdditionalCommandAuditAndBusinessData(final DatabaseSide side) {
        initializeFixture("purchase-order");

        assertThat(harness.queryForInt(side, tableExistsSql("Supplier"))).isEqualTo(1);
        assertThat(harness.queryForInt(side, tableExistsSql("Product"))).isEqualTo(1);
        assertThat(harness.queryForInt(side, tableExistsSql("ProductInventory"))).isEqualTo(1);
        assertThat(harness.queryForInt(side, tableExistsSql("Customer"))).isEqualTo(1);
        assertThat(harness.queryForInt(side, tableExistsSql("PurchaseOrderLine"))).isEqualTo(1);

        assertThat(harness.queryForInt(side, tableRowCountSql("Supplier"))).isEqualTo(3);
        assertThat(harness.queryForInt(side, tableRowCountSql("Product"))).isEqualTo(3);
        assertThat(harness.queryForInt(side, tableRowCountSql("ProductInventory"))).isEqualTo(3);
        assertThat(harness.queryForInt(side, tableRowCountSql("Customer"))).isEqualTo(2);
        final int expectedPurchaseOrderLineCount = side == DatabaseSide.LEFT ? 3 : 2;
        assertThat(harness.queryForInt(side, tableRowCountSql("PurchaseOrderLine"))).isEqualTo(expectedPurchaseOrderLineCount);

        assertThat(harness.queryForInt(side, commandLogRowCountSql())).isEqualTo(5);
        final int expectedAuditTrailRowCount = side == DatabaseSide.LEFT ? 10 : 6;
        assertThat(harness.queryForInt(side, auditTrailRowCountSql())).isEqualTo(expectedAuditTrailRowCount);
    }

    private static void initializeFixture(final String fixtureName) {
        initializeFixture(DatabaseSide.LEFT, fixtureName, "/sql/fixtures/%s/left-data.sql".formatted(fixtureName));
        initializeFixture(DatabaseSide.RIGHT, fixtureName, "/sql/fixtures/%s/right-data.sql".formatted(fixtureName));
    }

    private static void initializeFixture(final DatabaseSide side, final String fixtureName, final String dataResourcePath) {
        harness.initializeFromResource(side, "/sql/fixtures/%s/schema.sql".formatted(fixtureName));
        harness.initializeFromResource(side, dataResourcePath);
    }

    private static String purchaseOrderBusinessKeyIndexColumnCountSql() {
        return """
                SELECT COUNT(*)
                FROM sys.indexes i
                JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE i.object_id = OBJECT_ID('dbo.PurchaseOrder')
                  AND i.name = 'PurchaseOrder_PK'
                  AND i.is_unique = 1
                  AND c.name = 'reference'
                  AND ic.key_ordinal = 1
                """;
    }

    private static String purchaseOrderBusinessKeyIndexKeyColumnCountSql() {
        return """
                SELECT COUNT(*)
                FROM sys.indexes i
                JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                WHERE i.object_id = OBJECT_ID('dbo.PurchaseOrder')
                  AND i.name = 'PurchaseOrder_PK'
                  AND ic.key_ordinal > 0
                """;
    }

    private static String tableExistsSql(final String tableName) {
        return tableExistsInSchemaSql("dbo", tableName);
    }

    private static String tableExistsInSchemaSql(final String schemaName, final String tableName) {
        return """
                SELECT COUNT(*)
                FROM sys.tables t
                JOIN sys.schemas s ON t.schema_id = s.schema_id
                WHERE s.name = '%s'
                  AND t.name = '%s'
                """.formatted(schemaName, tableName);
    }

    private static String columnExistsSql(final String tableName, final String columnName) {
        return """
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = 'dbo'
                  AND TABLE_NAME = '%s'
                  AND COLUMN_NAME = '%s'
                """.formatted(tableName, columnName);
    }

    private static String businessKeyLikeUniqueIndexCountSql(final String tableName) {
        return """
                SELECT COUNT(*)
                FROM sys.indexes i
                WHERE i.object_id = OBJECT_ID('dbo.%s')
                  AND i.is_unique = 1
                  AND i.name LIKE '%%[_]PK'
                """.formatted(tableName);
    }

    private static String purchaseOrderVersionDataTypeSql() {
        return """
                SELECT DATA_TYPE
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = 'dbo'
                  AND TABLE_NAME = 'PurchaseOrder'
                  AND COLUMN_NAME = 'version'
                """;
    }

    private static String primaryKeyColumnExistsSql(
            final String schemaName,
            final String tableName,
            final String columnName,
            final int keyOrdinal) {
        return """
                SELECT COUNT(*)
                FROM sys.key_constraints kc
                JOIN sys.tables t ON kc.parent_object_id = t.object_id
                JOIN sys.schemas s ON t.schema_id = s.schema_id
                JOIN sys.index_columns ic ON kc.parent_object_id = ic.object_id AND kc.unique_index_id = ic.index_id
                JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE kc.type = 'PK'
                  AND s.name = '%s'
                  AND t.name = '%s'
                  AND c.name = '%s'
                  AND ic.key_ordinal = %d
                """.formatted(schemaName, tableName, columnName, keyOrdinal);
    }

    private static String columnExistsInSchemaSql(
            final String schemaName,
            final String tableName,
            final String columnName,
            final String dataType) {
        return """
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = '%s'
                  AND TABLE_NAME = '%s'
                  AND COLUMN_NAME = '%s'
                  AND DATA_TYPE = '%s'
                """.formatted(schemaName, tableName, columnName, dataType);
    }

    private static String foreignKeyExistsSql(
            final String schemaName,
            final String tableName,
            final String foreignKeyName) {
        return """
                SELECT COUNT(*)
                FROM sys.foreign_keys fk
                JOIN sys.tables t ON fk.parent_object_id = t.object_id
                JOIN sys.schemas s ON t.schema_id = s.schema_id
                WHERE s.name = '%s'
                  AND t.name = '%s'
                  AND fk.name = '%s'
                """.formatted(schemaName, tableName, foreignKeyName);
    }

    private static String registerProductCommandCountSql() {
        return """
                SELECT COUNT(*)
                FROM causewayExtCommandLog.CommandLogEntry
                WHERE logicalMemberIdentifier = 'supplier.Supplier#registerProduct'
                """;
    }

    private static String registerProductSupplierTargetCountSql() {
        return """
                SELECT COUNT(*)
                FROM causewayExtCommandLog.CommandLogEntry
                WHERE logicalMemberIdentifier = 'supplier.Supplier#registerProduct'
                  AND target LIKE 'supplier.Supplier:%'
                """;
    }

    private static String registerProductAuditProductTargetCountSql() {
        return """
                SELECT COUNT(*)
                FROM causewayExtAuditTrail.AuditTrailEntry a
                JOIN causewayExtCommandLog.CommandLogEntry c ON c.interactionId = a.interactionId
                WHERE c.logicalMemberIdentifier = 'supplier.Supplier#registerProduct'
                  AND a.target LIKE 'product.Product:%'
                """;
    }

    private static String registerProductReplayStateSql() {
        return """
                SELECT TOP 1 replayState
                FROM causewayExtCommandLog.CommandLogEntry
                WHERE logicalMemberIdentifier = 'supplier.Supplier#registerProduct'
                """;
    }

    private static String logicalTypeMappingCountSql(final String logicalTypeName) {
        return """
                SELECT COUNT(*)
                FROM util.LogicalTypeTableMapping
                WHERE logicalTypeName = '%s'
                """.formatted(logicalTypeName);
    }

    private static String logicalTypeQualifiedNamesSql(final String logicalTypeName) {
        return """
                SELECT STRING_AGG(qualifiedName, ',') WITHIN GROUP (ORDER BY qualifiedName)
                FROM util.LogicalTypeTableMapping
                WHERE logicalTypeName = '%s'
                """.formatted(logicalTypeName);
    }

    private static String commandLogRowCountSql() {
        return """
                SELECT COUNT(*)
                FROM causewayExtCommandLog.CommandLogEntry
                """;
    }

    private static String auditTrailRowCountSql() {
        return """
                SELECT COUNT(*)
                FROM causewayExtAuditTrail.AuditTrailEntry
                """;
    }

    private static String tableRowCountSql(final String tableName) {
        return """
                SELECT COUNT(*)
                FROM dbo.%s
                """.formatted(tableName);
    }

    private static String cfctIgnoredExtendedPropertyCountSql(final String tableName, final String columnName) {
        return """
                SELECT COUNT(*)
                FROM sys.extended_properties ep
                JOIN sys.tables t ON ep.major_id = t.object_id
                JOIN sys.columns c ON c.object_id = t.object_id AND c.column_id = ep.minor_id
                JOIN sys.schemas s ON s.schema_id = t.schema_id
                WHERE ep.class = 1
                  AND ep.name = 'cfct.ignored'
                  AND s.name = 'dbo'
                  AND t.name = '%s'
                  AND c.name = '%s'
                """.formatted(tableName, columnName);
    }

    private static String purchaseOrderRowversionColumnCountSql() {
        return """
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = 'dbo'
                  AND TABLE_NAME = 'PurchaseOrder'
                  AND DATA_TYPE IN ('timestamp', 'rowversion')
                """;
    }
}
