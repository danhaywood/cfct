package com.danhaywood.sqlcomparer.core;

import com.danhaywood.sqlcomparer.comparison.MultiTableComparer;
import com.danhaywood.sqlcomparer.comparison.TableComparer;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.RowDifference;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;
import com.danhaywood.sqlcomparer.harness.DatabaseSide;
import com.danhaywood.sqlcomparer.harness.SqlServerTestHarness;
import com.danhaywood.sqlcomparer.report.TextMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.TextTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.sqlserver.SqlServerTableMetadataReader;
import com.danhaywood.sqlcomparer.sqlserver.SqlServerTableRowReader;
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

    private final MultiTableComparer comparer = new MultiTableComparer(new TableComparer(
            new SqlServerTableMetadataReader(),
            new SqlServerTableRowReader()));
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
