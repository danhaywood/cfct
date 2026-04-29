package com.danhaywood.sqlcomparer.report;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonReportFormatter;

public final class MultiTableComparisonReportFormatterDefault implements MultiTableComparisonReportFormatter {

    private final TextMultiTableComparisonReportRenderer textRenderer;
    private final JsonMultiTableComparisonReportRenderer jsonRenderer;
    private final YamlMultiTableComparisonReportRenderer yamlRenderer;
    private final ExcelMultiTableComparisonReportRenderer excelRenderer;

    public MultiTableComparisonReportFormatterDefault(
            final TextMultiTableComparisonReportRenderer textRenderer,
            final JsonMultiTableComparisonReportRenderer jsonRenderer,
            final YamlMultiTableComparisonReportRenderer yamlRenderer,
            final ExcelMultiTableComparisonReportRenderer excelRenderer) {
        this.textRenderer = textRenderer;
        this.jsonRenderer = jsonRenderer;
        this.yamlRenderer = yamlRenderer;
        this.excelRenderer = excelRenderer;
    }

    @Override
    public String renderText(final MultiTableComparisonResult result) {
        return textRenderer.render(result);
    }

    @Override
    public String renderJson(final MultiTableComparisonResult result) {
        return jsonRenderer.render(result);
    }

    @Override
    public String renderYaml(final MultiTableComparisonResult result) {
        return yamlRenderer.render(result);
    }

    @Override
    public byte[] renderExcel(final MultiTableComparisonResult result) {
        return excelRenderer.render(result);
    }
}
