package com.danhaywood.sqlcomparer.core;

import com.danhaywood.sqlcomparer.comparison.TableComparisonServiceDefault;
import com.danhaywood.sqlcomparer.exception.MetadataException;
import com.danhaywood.sqlcomparer.model.ColumnDifference;
import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.RowDifference;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableMetadata;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.ComparisonOptions;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;
import com.danhaywood.sqlcomparer.harness.DatabaseSide;
import com.danhaywood.sqlcomparer.harness.SqlServerTestHarness;
import com.danhaywood.sqlcomparer.report.TextTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.sqlserver.TableMetadataReaderSqlServer;
import com.danhaywood.sqlcomparer.sqlserver.TableRowReaderSqlServer;
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

    private final TableComparisonServiceDefault comparer = new TableComparisonServiceDefault(
            new TableMetadataReaderSqlServer(),
            new TableRowReaderSqlServer());
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
    void comparesPurchaseOrderByBusinessKeyAndIgnoresIdentityByDefault() throws Exception {
        initializeFixture("purchase-order");

        final TableComparisonResult result = comparePurchaseOrders();

        assertThat(result.businessKey().indexName()).isEqualTo("PurchaseOrder_PK");
        assertThat(result.businessKey().columns()).containsExactly(new ColumnRef("reference"));
        assertThat(result.ignoredColumns()).containsExactly(new ColumnRef("id"), new ColumnRef("version"));
        assertThat(result.rowsOnlyInLeft()).containsExactly(new RowKey(java.util.List.of("PO-003")));
        assertThat(result.rowsOnlyInRight()).containsExactly(new RowKey(java.util.List.of("PO-004")));
        assertThat(result.differingRows()).extracting(RowDifference::key)
                .containsExactly(
                        new RowKey(java.util.List.of("PO-002")),
                        new RowKey(java.util.List.of("PO-005")));
        assertThat(result.differingRows().get(0).columnDifferences())
                .containsExactly(new ColumnDifference(new ColumnRef("status"), "DRAFT", "APPROVED"));
        assertThat(result.differingRows().get(1).columnDifferences())
                .containsExactly(
                        new ColumnDifference(new ColumnRef("net_amount"), "100.00", "100.01"),
                        new ColumnDifference(new ColumnRef("gross_amount"), "100.00", "100.01"));
    }

    @Test
    void explicitlyIgnoringIdStillProducesSameDifferencesAsDefault() throws Exception {
        initializeFixture("purchase-order");

        final TableComparisonResult result = comparePurchaseOrdersWithOptions(new ComparisonOptions("_PK", java.util.Set.of("id", "version")));

        assertThat(result.ignoredColumns()).containsExactly(new ColumnRef("id"), new ColumnRef("version"));
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
        initializeFixture("purchase-order");

        Approvals.verify(renderer.render(comparePurchaseOrders()));
    }

    @Test
    void failsClearlyWhenBusinessKeyIndexIsMissing() {
        initializeFixture("purchase-order-without-business-key");

        assertThatThrownBy(() -> readMetadata("PurchaseOrderWithoutBusinessKey"))
                .isInstanceOf(MetadataException.class)
                .hasMessageContaining("dbo.PurchaseOrderWithoutBusinessKey")
                .hasMessageContaining("_PK");
    }

    @Test
    void excludesGuidUuidAndUniqueIdentifierColumnsFromComparison() throws Exception {
        initializeFixture("guid-noise");

        final TableComparisonResult result;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            result = comparer.compare(left, right, new TableComparisonRequest(new TableRef("dbo", "GuidNoise"), ComparisonOptions.defaults()));
        }

        assertThat(result.businessKey().columns()).containsExactly(new ColumnRef("reference"));
        assertThat(result.comparedColumns()).containsExactly(new ColumnRef("name"));
        assertThat(result.ignoredColumns()).containsExactly(new ColumnRef("id"), new ColumnRef("Guid"), new ColumnRef("uuid"), new ColumnRef("version"));
        assertThat(result.differingRows()).extracting(RowDifference::key)
                .containsExactly(new RowKey(java.util.List.of("GN-002")));
        assertThat(result.differingRows().get(0).columnDifferences())
                .containsExactly(new ColumnDifference(new ColumnRef("name"), "Bravo-LEFT", "Bravo-RIGHT"));
    }

    @Test
    void failsClearlyWhenBusinessKeyIndexIsAmbiguous() {
        initializeSchema("ambiguous-business-key");

        assertThatThrownBy(() -> readMetadata("AmbiguousBusinessKey"))
                .isInstanceOf(MetadataException.class)
                .hasMessageContaining("dbo.AmbiguousBusinessKey")
                .hasMessageContaining("multiple unique indexes")
                .hasMessageContaining("AmbiguousBusinessKey_PK")
                .hasMessageContaining("AmbiguousBusinessKeyExternal_PK");
    }

    private TableComparisonResult comparePurchaseOrders() throws Exception {
        return comparePurchaseOrdersWithOptions(ComparisonOptions.defaults());
    }

    private TableComparisonResult comparePurchaseOrdersWithOptions(final ComparisonOptions options) throws Exception {
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            return comparer.compare(left, right, new TableComparisonRequest(new TableRef("dbo", "PurchaseOrder"), options));
        }
    }

    private TableMetadata readMetadata(final String tableName) throws Exception {
        try (Connection left = harness.openConnection(DatabaseSide.LEFT)) {
            return new TableMetadataReaderSqlServer().read(left, TableComparisonRequest.forTable("dbo", tableName));
        }
    }

    private static void initializeFixture(final String fixtureName) {
        initializeFixture(DatabaseSide.LEFT, fixtureName, "/sql/fixtures/%s/left-data.sql".formatted(fixtureName));
        initializeFixture(DatabaseSide.RIGHT, fixtureName, "/sql/fixtures/%s/right-data.sql".formatted(fixtureName));
    }

    private static void initializeFixture(final DatabaseSide side, final String fixtureName, final String dataResourcePath) {
        initializeSchema(side, fixtureName);
        harness.initializeFromResource(side, dataResourcePath);
    }

    private static void initializeSchema(final String fixtureName) {
        initializeSchema(DatabaseSide.LEFT, fixtureName);
        initializeSchema(DatabaseSide.RIGHT, fixtureName);
    }

    private static void initializeSchema(final DatabaseSide side, final String fixtureName) {
        harness.initializeFromResource(side, "/sql/fixtures/%s/schema.sql".formatted(fixtureName));
    }
}
