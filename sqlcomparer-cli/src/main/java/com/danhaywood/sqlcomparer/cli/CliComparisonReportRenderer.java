package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.TextMultiTableComparisonReportRenderer;
import org.springframework.stereotype.Service;

@Service
public final class CliComparisonReportRenderer {

    private final TextMultiTableComparisonReportRenderer textRenderer;
    private final JsonMultiTableComparisonReportRenderer jsonRenderer;
    private final ExcelMultiTableComparisonReportRenderer excelRenderer;

    public CliComparisonReportRenderer(
            final TextMultiTableComparisonReportRenderer textRenderer,
            final JsonMultiTableComparisonReportRenderer jsonRenderer,
            final ExcelMultiTableComparisonReportRenderer excelRenderer) {
        this.textRenderer = textRenderer;
        this.jsonRenderer = jsonRenderer;
        this.excelRenderer = excelRenderer;
    }

    public CliExecutionOutput render(final MultiTableComparisonResult result, final CliOutputFormat outputFormat) {
        return switch (outputFormat) {
            case TEXT -> CliExecutionOutput.text(textRenderer.render(result));
            case JSON -> CliExecutionOutput.json(jsonRenderer.render(result));
            case EXCEL -> CliExecutionOutput.excel(excelRenderer.render(result));
        };
    }
}
