package com.danhaywood.sqlcomparer.harness;

import org.approvaltests.Approvals;
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
    void initializesDatabasesIndependentlyAndPreservesIsolation() {
        initializeDatabases();

        assertThat(harness.queryForString(DatabaseSide.LEFT, "SELECT TOP 1 payload FROM dbo.sample_items ORDER BY payload"))
                .isEqualTo("left payload");
        assertThat(harness.queryForString(DatabaseSide.RIGHT, "SELECT TOP 1 payload FROM dbo.sample_items ORDER BY payload"))
                .isEqualTo("right payload");
        assertThat(harness.queryForInt(DatabaseSide.LEFT, "SELECT COUNT(*) FROM dbo.sample_items"))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, "SELECT COUNT(*) FROM dbo.sample_items"))
                .isEqualTo(1);
    }

    @Test
    void approvesStableHarnessDescription() {
        initializeDatabases();

        Approvals.verify(harness.describeDatabases());
    }

    @Test
    void initializesRealisticPurchaseOrderFixture() {
        initializePurchaseOrderFixture();

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
        initializePurchaseOrderFixture();

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
    void purchaseOrderFixtureIncludesTableWithoutBusinessKey() {
        initializePurchaseOrderFixture();

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
        initializePurchaseOrderFixture();

        assertThat(harness.queryForString(DatabaseSide.LEFT, purchaseOrderVersionDataTypeSql()))
                .isEqualTo("datetime2");
        assertThat(harness.queryForString(DatabaseSide.RIGHT, purchaseOrderVersionDataTypeSql()))
                .isEqualTo("datetime2");
        assertThat(harness.queryForInt(DatabaseSide.LEFT, purchaseOrderRowversionColumnCountSql()))
                .isEqualTo(0);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, purchaseOrderRowversionColumnCountSql()))
                .isEqualTo(0);
    }

    private static void initializeDatabases() {
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/left-init.sql");
        harness.initializeFromResource(DatabaseSide.RIGHT, "/sql/right-init.sql");
    }

    private static void initializePurchaseOrderFixture() {
        initializePurchaseOrderFixture(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/left-data.sql");
        initializePurchaseOrderFixture(DatabaseSide.RIGHT, "/sql/fixtures/purchase-order/right-data.sql");
    }

    private static void initializePurchaseOrderFixture(final DatabaseSide side, final String dataResourcePath) {
        harness.initializeFromResource(side, "/sql/fixtures/purchase-order/schema.sql");
        harness.initializeFromResource(side, dataResourcePath);
    }

    private static String purchaseOrderBusinessKeyIndexColumnCountSql() {
        return """
                SELECT COUNT(*)
                FROM sys.indexes i
                JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE i.object_id = OBJECT_ID('dbo.PurchaseOrder')
                  AND i.name = 'PurchaseOrder_BK'
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
                  AND i.name = 'PurchaseOrder_BK'
                  AND ic.key_ordinal > 0
                """;
    }

    private static String tableExistsSql(final String tableName) {
        return """
                SELECT COUNT(*)
                FROM sys.tables t
                JOIN sys.schemas s ON t.schema_id = s.schema_id
                WHERE s.name = 'dbo'
                  AND t.name = '%s'
                """.formatted(tableName);
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
                  AND i.name LIKE '%%[_]BK'
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
