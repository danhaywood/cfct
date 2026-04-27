package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonReportFormatter;
import org.springframework.stereotype.Service;

@Service
public final class CliComparisonReportRenderer {

    private final MultiTableComparisonReportFormatter formatter;

    public CliComparisonReportRenderer(final MultiTableComparisonReportFormatter formatter) {
        this.formatter = formatter;
    }

    public CliExecutionOutput render(final MultiTableComparisonResult result, final CliOutputFormat outputFormat) {
        return switch (outputFormat) {
            case TEXT -> CliExecutionOutput.text(formatter.renderText(result));
            case JSON -> CliExecutionOutput.json(formatter.renderJson(result));
            case EXCEL -> CliExecutionOutput.excel(formatter.renderExcel(result));
        };
    }
}
