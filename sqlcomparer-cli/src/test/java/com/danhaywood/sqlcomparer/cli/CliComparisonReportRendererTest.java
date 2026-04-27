package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.BusinessKey;
import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.TextMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.TextTableComparisonReportRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CliComparisonReportRendererTest {

    private final CliComparisonReportRenderer renderer = new CliComparisonReportRenderer(
            new TextMultiTableComparisonReportRenderer(new TextTableComparisonReportRenderer()),
            new JsonMultiTableComparisonReportRenderer(new ObjectMapper()),
            new ExcelMultiTableComparisonReportRenderer());

    @Test
    void rendersTextOutput() {
        final CliExecutionOutput output = renderer.render(result(), CliOutputFormat.TEXT);

        assertThat(output.outputFormat()).isEqualTo(CliOutputFormat.TEXT);
        assertThat(output.mediaType()).contains("text/plain");
        assertThat(new String(output.bytes(), StandardCharsets.UTF_8))
                .contains("Multi-table comparison")
                .contains("dbo.Supplier");
    }

    @Test
    void rendersJsonOutput() {
        final CliExecutionOutput output = renderer.render(result(), CliOutputFormat.JSON);

        assertThat(output.outputFormat()).isEqualTo(CliOutputFormat.JSON);
        assertThat(output.mediaType()).isEqualTo("application/json");
        assertThat(new String(output.bytes(), StandardCharsets.UTF_8))
                .contains("\"tables\"")
                .contains("\"Supplier\"");
    }

    @Test
    void rendersExcelOutput() {
        final CliExecutionOutput output = renderer.render(result(), CliOutputFormat.EXCEL);

        assertThat(output.outputFormat()).isEqualTo(CliOutputFormat.EXCEL);
        assertThat(output.mediaType()).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(output.bytes()).startsWith(new byte[]{0x50, 0x4b});
    }

    private MultiTableComparisonResult result() {
        final ColumnRef reference = new ColumnRef("reference");
        final TableComparisonResult tableResult = new TableComparisonResult(
                new TableRef("dbo", "Supplier"),
                new BusinessKey("Supplier_BK", List.of(reference)),
                List.of(reference, new ColumnRef("name")),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                Map.of(),
                Map.of());
        return new MultiTableComparisonResult(List.of(tableResult));
    }
}
