package com.danhaywood.cfct.core;

import com.danhaywood.cfct.comparison.TableComparisonServiceDefault;
import com.danhaywood.cfct.exception.MetadataException;
import com.danhaywood.cfct.model.ColumnDifference;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.harness.DatabaseSide;
import com.danhaywood.cfct.harness.SqlServerTestHarness;
import com.danhaywood.cfct.report.TextTableComparisonReportRenderer;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorForIdentityColumns;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorForTimestamps;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorForUuidColumns;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorUsingExtendedProperties;
import com.danhaywood.cfct.sqlserver.TableMetadataReaderSqlServer;
import com.danhaywood.cfct.sqlserver.TableRowReaderSqlServer;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

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
    void executesDiffQueryWhenDatabasesUseCompatibilityLevel100() throws Exception {
        initializeFixture("purchase-order");
        setCompatibilityLevel(DatabaseSide.LEFT, 100);
        setCompatibilityLevel(DatabaseSide.RIGHT, 100);

        try {
            final TableComparisonResult result = comparePurchaseOrders();

            assertThat(result.rowsOnlyInLeft()).containsExactly(new RowKey(java.util.List.of("PO-003")));
            assertThat(result.rowsOnlyInRight()).containsExactly(new RowKey(java.util.List.of("PO-004")));
            assertThat(result.differingRows()).extracting(RowDifference::key)
                    .containsExactly(
                            new RowKey(java.util.List.of("PO-002")),
                            new RowKey(java.util.List.of("PO-005")));
        } finally {
            setCompatibilityLevel(DatabaseSide.LEFT, 160);
            setCompatibilityLevel(DatabaseSide.RIGHT, 160);
        }
    }

    @Test
    void excludesMatchingRowsFromClientPayloadWhenUsingDatabaseSideDiff() throws Exception {
        initializeFixture("purchase-order");

        final TableComparisonResult result = comparePurchaseOrders();

        assertThat(result.matchingRowsValues()).isEmpty();
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
    void ignoresColumnWhenCfctIgnoredExtendedPropertyIsTruthy() throws Exception {
        initializeFixture("customer-address");

        final TableComparisonResult result;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            result = comparer.compare(left, right, new TableComparisonRequest(new TableRef("dbo", "CustomerAddress"), ComparisonOptions.defaults()));
        }

        assertThat(result.ignoredColumns()).contains(new ColumnRef("postcode"));
        assertThat(result.comparedColumns()).doesNotContain(new ColumnRef("postcode"));
        assertThat(result.differingRows()).isEmpty();
    }

    @Test
    void doesNotIgnoreCfctIgnoredColumnWhenExtendedPropertiesAdvisorDisabled() throws Exception {
        initializeFixture("customer-address");

        final TableComparisonServiceDefault withoutExtendedPropertiesAdvisor = new TableComparisonServiceDefault(
                new TableMetadataReaderSqlServer(defaultAdvisors(false)),
                new TableRowReaderSqlServer());

        final TableComparisonResult result;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            result = withoutExtendedPropertiesAdvisor.compare(left, right,
                    new TableComparisonRequest(new TableRef("dbo", "CustomerAddress"), ComparisonOptions.defaults()));
        }

        assertThat(result.ignoredColumns()).doesNotContain(new ColumnRef("postcode"));
        assertThat(result.comparedColumns()).contains(new ColumnRef("postcode"));
        assertThat(result.differingRows()).extracting(RowDifference::key)
                .containsExactly(new RowKey(java.util.List.of("ADDR-002")));
    }

    @Test
    void suppressesTimestampOnlyDifferencesAfterNormalizeMaskScrubbing() throws Exception {
        initializeFixture("purchase-order");

        final TableComparisonResult result;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            result = comparer.compare(left, right,
                    new TableComparisonRequest(new TableRef("dbo", "PurchaseOrderTimeline"), ComparisonOptions.defaults()));
        }

        assertThat(result.differingRows()).extracting(RowDifference::key)
                .containsExactly(new RowKey(java.util.List.of("POT-002")));
        assertThat(result.differingRows().get(0).columnDifferences())
                .containsExactly(new ColumnDifference(
                        new ColumnRef("audit_message"),
                        "yyyy-MM-ddThh:MM.ss.SSS - VT - [RENT, RENT_FIXED] - 2026-06-01 - 2026-06-01/2026-07-01",
                        "yyyy-MM-ddThh:MM.ss.SSS - VT - [RENT, RENT_VARIABLE] - 2026-06-01 - 2026-06-01/2026-07-01"));
    }

    @Test
    void failsClearlyWhenBusinessKeyIndexIsAmbiguous() {
        initializeSchema("ambiguous-business-key");

        assertThatThrownBy(() -> readMetadata("AmbiguousBusinessKey"))
                .isInstanceOf(MetadataException.class)
                .hasMessageContaining("dbo.AmbiguousBusinessKey")
                .hasMessageContaining("multiple unique indexes or unique constraints")
                .hasMessageContaining("AmbiguousBusinessKey_PK")
                .hasMessageContaining("AmbiguousBusinessKeyExternal_PK");
    }

    @Test
    void acceptsBusinessKeyUniqueConstraintBySuffix() throws Exception {
        initializeSchema("business-key-constraint-suffix");

        final TableMetadata metadata = readMetadata("BusinessKeyConstraintSuffix");

        assertThat(metadata.businessKey().indexName()).isEqualTo("BusinessKeyConstraintSuffix__reference__PK");
        assertThat(metadata.businessKey().columns()).containsExactly(new ColumnRef("reference"));
    }

    @Test
    void acceptsPrimaryKeyWhenUnrelatedUniqueConstraintExists() throws Exception {
        initializeSchema("application-user-primary-key");

        final TableMetadata metadata = readMetadata("ApplicationUser");

        assertThat(metadata.businessKey().indexName()).isEqualTo("ApplicationUser_PK");
        assertThat(metadata.businessKey().columns()).containsExactly(new ColumnRef("id"));
    }

    @Test
    void prefersPrimaryKeyWhenMultipleBusinessKeyObjectsMatchSuffix() throws Exception {
        initializeSchema("application-user-primary-key-preference");

        final TableMetadata metadata = readMetadata("ApplicationUserPrimaryKeyPreference");

        assertThat(metadata.businessKey().indexName()).isEqualTo("ApplicationUserPrimaryKeyPreference_PK");
        assertThat(metadata.businessKey().columns()).containsExactly(new ColumnRef("id"));
    }

    @Test
    void failsClearlyWhenBusinessKeyObjectsAreAmbiguousAcrossIndexAndConstraint() {
        initializeSchema("ambiguous-business-key-mixed");

        assertThatThrownBy(() -> readMetadata("AmbiguousBusinessKeyMixed"))
                .isInstanceOf(MetadataException.class)
                .hasMessageContaining("dbo.AmbiguousBusinessKeyMixed")
                .hasMessageContaining("multiple unique indexes or unique constraints")
                .hasMessageContaining("AmbiguousBusinessKeyMixed__external_reference__PK")
                .hasMessageContaining("AmbiguousBusinessKeyMixed__reference__PK");
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

    private static void setCompatibilityLevel(final DatabaseSide side, final int level) {
        harness.executeScript(side, "ALTER DATABASE [" + side.databaseName() + "] SET COMPATIBILITY_LEVEL = " + level);
    }

    private static List<IgnoreColumnAdvisor> defaultAdvisors(final boolean extendedPropertiesEnabled) {
        return List.of(
                new IgnoreColumnAdvisorForIdentityColumns(true),
                new IgnoreColumnAdvisorForUuidColumns(true),
                new IgnoreColumnAdvisorForTimestamps(true),
                new IgnoreColumnAdvisorUsingExtendedProperties(extendedPropertiesEnabled));
    }
}
