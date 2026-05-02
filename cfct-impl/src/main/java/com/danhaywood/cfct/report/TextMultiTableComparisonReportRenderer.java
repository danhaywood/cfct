package com.danhaywood.cfct.report;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableComparisonResult;
import org.springframework.stereotype.Service;

@Service
public final class TextMultiTableComparisonReportRenderer {

    private final TextTableComparisonReportRenderer tableRenderer;

    public TextMultiTableComparisonReportRenderer(final TextTableComparisonReportRenderer tableRenderer) {
        this.tableRenderer = tableRenderer;
    }

    public String render(final MultiTableComparisonResult result) {
        final StringBuilder report = new StringBuilder();
        report.append("Multi-table comparison").append('\n');
        report.append("Tables: ").append(result.tableResults().size()).append('\n');
        report.append('\n');
        for (int i = 0; i < result.tableResults().size(); i++) {
            final TableComparisonResult tableResult = result.tableResults().get(i);
            report.append("== ").append(tableResult.table().displayName()).append(" ==").append('\n');
            report.append(tableRenderer.render(tableResult));
            if (i < result.tableResults().size() - 1) {
                report.append('\n');
            }
        }
        if (!result.hasDifferences()) {
            report.append("No differences found across requested tables.").append('\n');
        }
        return report.toString();
    }
}
