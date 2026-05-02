package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.service.MultiTableComparisonReportFormatter;
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
            case YAML -> CliExecutionOutput.yaml(formatter.renderYaml(result));
            case EXCEL -> CliExecutionOutput.excel(formatter.renderExcel(result));
        };
    }
}
