package com.danhaywood.cfct.report;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnDifference;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonMultiTableComparisonReportRendererTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonMultiTableComparisonReportRenderer renderer = new JsonMultiTableComparisonReportRenderer(objectMapper);

    @Test
    void rendersDetailedDeterministicJsonReport() throws Exception {
        final String json = renderer.render(new MultiTableComparisonResult(List.of(tableResult())));

        final JsonNode root = objectMapper.readTree(json);
        final JsonNode table = root.path("differingTables").path(0);

        assertThat(root.path("hasDifferences").asBoolean()).isTrue();
        assertThat(root.path("tables").isMissingNode()).isTrue();
        assertThat(root.path("comparedTables").path(0).path("table").path("schema").asText()).isEqualTo("dbo");
        assertThat(root.path("comparedTables").path(0).path("table").path("name").asText()).isEqualTo("Supplier");
        assertThat(table.path("table").path("schema").asText()).isEqualTo("dbo");
        assertThat(table.path("table").path("name").asText()).isEqualTo("Supplier");
        assertThat(table.path("summary").path("comparedColumnCount").asInt()).isEqualTo(2);
        assertThat(table.path("summary").path("ignoredColumnCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("rowsOnlyInLeftCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("rowsOnlyInRightCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("differingRowCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("hasDifferences").asBoolean()).isTrue();

        final JsonNode leftOnly = table.path("rowsOnlyInLeft").path(0);
        assertThat(leftOnly.path("key").path(0).asText()).isEqualTo("SUP-LEFT");
        assertThat(leftOnly.path("leftValues").path("reference").asText()).isEqualTo("SUP-LEFT");
        assertThat(leftOnly.path("leftValues").path("name").asText()).isEqualTo("Left-only supplier");
        assertThat(leftOnly.path("leftValues").path("status").asText()).isEqualTo("ACTIVE");
        assertThat(leftOnly.path("rightValues").isEmpty()).isTrue();

        final JsonNode rightOnly = table.path("rowsOnlyInRight").path(0);
        assertThat(rightOnly.path("key").path(0).asText()).isEqualTo("SUP-RIGHT");
        assertThat(rightOnly.path("leftValues").isEmpty()).isTrue();
        assertThat(rightOnly.path("rightValues").path("reference").asText()).isEqualTo("SUP-RIGHT");
        assertThat(rightOnly.path("rightValues").path("name").asText()).isEqualTo("Right-only supplier");
        assertThat(rightOnly.path("rightValues").path("status").asText()).isEqualTo("ACTIVE");

        final JsonNode differing = table.path("differingRows").path(0);
        assertThat(differing.path("key").path(0).asText()).isEqualTo("SUP-DIFF");
        assertThat(differing.path("leftValues").path("reference").asText()).isEqualTo("SUP-DIFF");
        assertThat(differing.path("leftValues").path("name").asText()).isEqualTo("Shared supplier");
        assertThat(differing.path("leftValues").path("status").asText()).isEqualTo("ACTIVE");
        assertThat(differing.path("rightValues").path("reference").asText()).isEqualTo("SUP-DIFF");
        assertThat(differing.path("rightValues").path("name").asText()).isEqualTo("Shared supplier");
        assertThat(differing.path("rightValues").path("status").asText()).isEqualTo("INACTIVE");
        assertThat(differing.path("differences").path(0).path("column").asText()).isEqualTo("status");
        assertThat(differing.path("differences").path(0).path("left").asText()).isEqualTo("ACTIVE");
        assertThat(differing.path("differences").path(0).path("right").asText()).isEqualTo("INACTIVE");
    }

    @Test
    void omitsCleanTablesFromDifferingTablesButIncludesThemInComparedTables() throws Exception {
        final String json = renderer.render(new MultiTableComparisonResult(List.of(tableResult(), cleanTableResult())));

        final JsonNode root = objectMapper.readTree(json);

        assertThat(root.path("hasDifferences").asBoolean()).isTrue();
        assertThat(root.path("differingTables")).hasSize(1);
        assertThat(root.path("differingTables").path(0).path("table").path("name").asText()).isEqualTo("Supplier");
        assertThat(root.path("comparedTables")).hasSize(2);
        assertThat(root.path("comparedTables").path(0).path("table").path("name").asText()).isEqualTo("Supplier");
        assertThat(root.path("comparedTables").path(1).path("table").path("name").asText()).isEqualTo("Product");
    }

    @Test
    void rendersEmptyComparisonReport() throws Exception {
        final String json = renderer.render(new MultiTableComparisonResult(List.of()));

        final JsonNode root = objectMapper.readTree(json);

        assertThat(root.path("hasDifferences").asBoolean()).isFalse();
        assertThat(root.path("differingTables")).isEmpty();
        assertThat(root.path("comparedTables")).isEmpty();
    }

    private TableComparisonResult tableResult() {
        final ColumnRef name = new ColumnRef("name");
        final ColumnRef status = new ColumnRef("status");
        final RowKey leftOnly = new RowKey(List.of("SUP-LEFT"));
        final RowKey rightOnly = new RowKey(List.of("SUP-RIGHT"));
        final RowKey differing = new RowKey(List.of("SUP-DIFF"));
        final Map<ColumnRef, String> leftOnlyValues = Map.of(name, "Left-only supplier", status, "ACTIVE");
        final Map<ColumnRef, String> rightOnlyValues = Map.of(name, "Right-only supplier", status, "ACTIVE");
        final Map<ColumnRef, String> differingLeftValues = Map.of(name, "Shared supplier", status, "ACTIVE");
        final Map<ColumnRef, String> differingRightValues = Map.of(name, "Shared supplier", status, "INACTIVE");

        return new TableComparisonResult(
                new TableRef("dbo", "Supplier"),
                new BusinessKey("Supplier_PK", List.of(new ColumnRef("reference"))),
                List.of(name, status),
                List.of(new ColumnRef("id")),
                List.of(leftOnly),
                List.of(rightOnly),
                List.of(new RowDifference(
                        differing,
                        differingLeftValues,
                        differingRightValues,
                        List.of(new ColumnDifference(status, "ACTIVE", "INACTIVE")))),
                Map.of(leftOnly, leftOnlyValues),
                Map.of(rightOnly, rightOnlyValues));
    }

    private TableComparisonResult cleanTableResult() {
        final ColumnRef name = new ColumnRef("name");
        return new TableComparisonResult(
                new TableRef("dbo", "Product"),
                new BusinessKey("Product_PK", List.of(new ColumnRef("sku"))),
                List.of(name),
                List.of(new ColumnRef("id")),
                List.of(),
                List.of(),
                List.of(),
                Map.of(),
                Map.of());
    }
}
