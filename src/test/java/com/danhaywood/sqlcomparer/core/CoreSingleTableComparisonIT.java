package com.danhaywood.sqlcomparer.core;

import com.danhaywood.sqlcomparer.harness.DatabaseSide;
import com.danhaywood.sqlcomparer.harness.SqlServerTestHarness;
import com.danhaywood.sqlcomparer.report.TextTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.sqlserver.SqlServerTableMetadataReader;
import com.danhaywood.sqlcomparer.sqlserver.SqlServerTableRowReader;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class CoreSingleTableComparisonIT {

    private static SqlServerTestHarness harness;

    private final TableComparer comparer = new TableComparer(
            new SqlServerTableMetadataReader(),
            new SqlServerTableRowReader());
    private final TextTableComparisonReportRenderer renderer = new TextTableComparisonReportRenderer();

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

    @Test
    void comparesPurchaseOrderByBusinessKeyAndIgnoresTechnicalColumns() throws Exception {
        initializePurchaseOrderFixture();

        final TableComparisonResult result = comparePurchaseOrders();

        assertThat(result.businessKey().indexName()).isEqualTo("PurchaseOrder_BK");
        assertThat(result.businessKey().columns()).containsExactly(new ColumnRef("reference"));
        assertThat(result.ignoredColumns()).containsExactly(new ColumnRef("id"), new ColumnRef("version"));
        assertThat(result.rowsOnlyInLeft()).containsExactly(new RowKey(java.util.List.of("PO-003")));
        assertThat(result.rowsOnlyInRight()).containsExactly(new RowKey(java.util.List.of("PO-004")));
        assertThat(result.differingRows()).extracting(RowDifference::key)
                .containsExactly(new RowKey(java.util.List.of("PO-002")), new RowKey(java.util.List.of("PO-005")));
        assertThat(result.differingRows().get(0).columnDifferences())
                .containsExactly(new ColumnDifference(new ColumnRef("status"), "DRAFT", "APPROVED"));
        assertThat(result.differingRows().get(1).columnDifferences())
                .containsExactly(
                        new ColumnDifference(new ColumnRef("net_amount"), "100.00", "100.01"),
                        new ColumnDifference(new ColumnRef("gross_amount"), "100.00", "100.01"));
    }

    @Test
    void approvesPurchaseOrderComparisonReport() throws Exception {
        initializePurchaseOrderFixture();

        Approvals.verify(renderer.render(comparePurchaseOrders()));
    }

    @Test
    void failsClearlyWhenBusinessKeyIndexIsMissing() {
        initializePurchaseOrderFixture();

        assertThatThrownBy(() -> readMetadata("PurchaseOrderWithoutBusinessKey"))
                .isInstanceOf(MetadataException.class)
                .hasMessageContaining("dbo.PurchaseOrderWithoutBusinessKey")
                .hasMessageContaining("_BK");
    }

    @Test
    void failsClearlyWhenBusinessKeyIndexIsAmbiguous() {
        harness.executeScript(DatabaseSide.LEFT, """
                DROP TABLE IF EXISTS dbo.AmbiguousBusinessKey;
                CREATE TABLE dbo.AmbiguousBusinessKey (
                    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                    reference NVARCHAR(40) NOT NULL,
                    external_reference NVARCHAR(40) NOT NULL,
                    payload NVARCHAR(40) NOT NULL
                );
                CREATE UNIQUE INDEX AmbiguousBusinessKey_BK ON dbo.AmbiguousBusinessKey(reference);
                CREATE UNIQUE INDEX AmbiguousBusinessKeyExternal_BK ON dbo.AmbiguousBusinessKey(external_reference);
                """);

        assertThatThrownBy(() -> readMetadata("AmbiguousBusinessKey"))
                .isInstanceOf(MetadataException.class)
                .hasMessageContaining("dbo.AmbiguousBusinessKey")
                .hasMessageContaining("multiple unique indexes")
                .hasMessageContaining("AmbiguousBusinessKey_BK")
                .hasMessageContaining("AmbiguousBusinessKeyExternal_BK");
    }

    private TableComparisonResult comparePurchaseOrders() throws Exception {
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            return comparer.compare(left, right, TableComparisonRequest.forTable("dbo", "PurchaseOrder"));
        }
    }

    private TableMetadata readMetadata(final String tableName) throws Exception {
        try (Connection left = harness.openConnection(DatabaseSide.LEFT)) {
            return new SqlServerTableMetadataReader().read(left, TableComparisonRequest.forTable("dbo", tableName));
        }
    }

    private static void initializePurchaseOrderFixture() {
        initializePurchaseOrderFixture(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/left-data.sql");
        initializePurchaseOrderFixture(DatabaseSide.RIGHT, "/sql/fixtures/purchase-order/right-data.sql");
    }

    private static void initializePurchaseOrderFixture(final DatabaseSide side, final String dataResourcePath) {
        harness.initializeFromResource(side, "/sql/fixtures/purchase-order/schema.sql");
        harness.initializeFromResource(side, dataResourcePath);
    }
}
