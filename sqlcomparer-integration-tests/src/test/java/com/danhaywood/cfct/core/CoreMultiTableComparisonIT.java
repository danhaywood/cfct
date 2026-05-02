package com.danhaywood.cfct.core;

import com.danhaywood.cfct.comparison.MultiTableComparisonServiceDefault;
import com.danhaywood.cfct.comparison.TableComparisonServiceDefault;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.harness.DatabaseSide;
import com.danhaywood.cfct.harness.SqlServerTestHarness;
import com.danhaywood.cfct.report.TextMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.TextTableComparisonReportRenderer;
import com.danhaywood.cfct.sqlserver.TableMetadataReaderSqlServer;
import com.danhaywood.cfct.sqlserver.TableRowReaderSqlServer;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class CoreMultiTableComparisonIT {

    private static SqlServerTestHarness harness;

    private final MultiTableComparisonServiceDefault comparer = new MultiTableComparisonServiceDefault(new TableComparisonServiceDefault(
            new TableMetadataReaderSqlServer(),
            new TableRowReaderSqlServer()));
    private final TextMultiTableComparisonReportRenderer renderer = new TextMultiTableComparisonReportRenderer(
            new TextTableComparisonReportRenderer());

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
    void rejectsEmptyTableSet() {
        assertThatThrownBy(() -> MultiTableComparisonRequest.forTables(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one table");
    }

    @Test
    void comparesSelectedTablesInRequestOrder() throws Exception {
        initializeGoodComparableFixtures();

        final MultiTableComparisonResult result = compareSupplierAndProduct();

        assertThat(result.tableResults()).extracting(TableComparisonResult::table)
                .containsExactly(new TableRef("dbo", "Supplier"), new TableRef("dbo", "Product"));
        assertThat(result.tableResults()).extracting(tableResult -> tableResult.table().tableName())
                .doesNotContain("PurchaseOrder");
        assertThat(result.tableResults().get(0).rowsOnlyInLeft())
                .containsExactly(new RowKey(List.of("SUP-003")));
        assertThat(result.tableResults().get(0).rowsOnlyInRight())
                .containsExactly(new RowKey(List.of("SUP-004")));
        assertThat(result.tableResults().get(0).differingRows()).extracting(RowDifference::key)
                .containsExactly(new RowKey(List.of("SUP-002")));
        assertThat(result.tableResults().get(1).rowsOnlyInLeft())
                .containsExactly(new RowKey(List.of("SKU-003")));
        assertThat(result.tableResults().get(1).rowsOnlyInRight())
                .containsExactly(new RowKey(List.of("SKU-004")));
        assertThat(result.tableResults().get(1).differingRows()).extracting(RowDifference::key)
                .containsExactly(new RowKey(List.of("SKU-002")));
    }

    @Test
    void approvesMultiTableComparisonReport() throws Exception {
        initializeGoodComparableFixtures();

        Approvals.verify(renderer.render(compareSupplierAndProduct()));
    }

    private MultiTableComparisonResult compareSupplierAndProduct() throws Exception {
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            return comparer.compare(left, right, MultiTableComparisonRequest.forTables(List.of(
                    new TableRef("dbo", "Supplier"),
                    new TableRef("dbo", "Product"))));
        }
    }

    private static void initializeGoodComparableFixtures() {
        initializeFixture("purchase-order");
        initializeFixture("supplier");
        initializeFixture("product");
    }

    private static void initializeFixture(final String fixtureName) {
        initializeFixture(DatabaseSide.LEFT, fixtureName, "/sql/fixtures/%s/left-data.sql".formatted(fixtureName));
        initializeFixture(DatabaseSide.RIGHT, fixtureName, "/sql/fixtures/%s/right-data.sql".formatted(fixtureName));
    }

    private static void initializeFixture(final DatabaseSide side, final String fixtureName, final String dataResourcePath) {
        harness.initializeFromResource(side, "/sql/fixtures/%s/schema.sql".formatted(fixtureName));
        harness.initializeFromResource(side, dataResourcePath);
    }
}
