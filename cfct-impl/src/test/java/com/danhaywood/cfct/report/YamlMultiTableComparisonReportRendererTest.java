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
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class YamlMultiTableComparisonReportRendererTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final YAMLMapper yamlMapper = new YAMLMapper();
    private final YamlMultiTableComparisonReportRenderer renderer = new YamlMultiTableComparisonReportRenderer(objectMapper);

    @Test
    void rendersDetailedDeterministicYamlReport() throws Exception {
        final String yaml = renderer.render(new MultiTableComparisonResult(List.of(tableResult())));

        final JsonNode root = yamlMapper.readTree(yaml);
        final JsonNode table = root.path("tables").path(0);

        assertThat(root.path("hasDifferences").asBoolean()).isTrue();
        assertThat(table.path("table").path("schema").asText()).isEqualTo("dbo");
        assertThat(table.path("table").path("name").asText()).isEqualTo("Supplier");
        assertThat(table.path("summary").path("comparedColumnCount").asInt()).isEqualTo(2);
        assertThat(table.path("summary").path("ignoredColumnCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("rowsOnlyInLeftCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("rowsOnlyInRightCount").asInt()).isEqualTo(1);
        assertThat(table.path("summary").path("differingRowCount").asInt()).isEqualTo(1);

        final JsonNode differing = table.path("differingRows").path(0);
        assertThat(differing.path("key").path(0).asText()).isEqualTo("SUP-DIFF");
        assertThat(differing.path("leftValues").path("status").asText()).isEqualTo("ACTIVE");
        assertThat(differing.path("rightValues").path("status").asText()).isEqualTo("INACTIVE");
        assertThat(differing.path("differences").path(0).path("column").asText()).isEqualTo("status");
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
}
