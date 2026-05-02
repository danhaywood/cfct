package com.danhaywood.cfct.report;

import com.danhaywood.cfct.model.ColumnDifference;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public final class TextTableComparisonReportRenderer {

    public String render(final TableComparisonResult result) {
        final StringBuilder report = new StringBuilder();
        report.append("Table: ").append(result.table().displayName()).append('\n');
        report.append("Business key: ").append(result.businessKey().display()).append('\n');
        report.append('\n');
        appendColumnList(report, "Compared columns", result.comparedColumns());
        report.append('\n');
        appendColumnList(report, "Ignored columns", result.ignoredColumns());
        report.append('\n');
        appendRows(report, "Rows only in left", result.rowsOnlyInLeft());
        report.append('\n');
        appendRows(report, "Rows only in right", result.rowsOnlyInRight());
        report.append('\n');
        appendDifferences(report, result.differingRows());
        report.append('\n');
        if (!result.hasDifferences()) {
            report.append("No differences found.").append('\n');
        }
        return report.toString();
    }

    private void appendColumnList(final StringBuilder report, final String heading, final List<ColumnRef> columns) {
        report.append(heading).append(":").append('\n');
        if (columns.isEmpty()) {
            report.append("  (none)").append('\n');
            return;
        }
        for (final ColumnRef column : columns) {
            report.append("  ").append(column.name()).append('\n');
        }
    }

    private void appendRows(final StringBuilder report, final String heading, final List<RowKey> rows) {
        report.append(heading).append(":").append('\n');
        if (rows.isEmpty()) {
            report.append("  (none)").append('\n');
            return;
        }
        for (final RowKey row : rows) {
            report.append("  ").append(row.display()).append('\n');
        }
    }

    private void appendDifferences(final StringBuilder report, final List<RowDifference> differingRows) {
        report.append("Rows with differences:").append('\n');
        if (differingRows.isEmpty()) {
            report.append("  (none)").append('\n');
            return;
        }
        for (final RowDifference rowDifference : differingRows) {
            report.append("  ").append(rowDifference.key().display()).append('\n');
            for (final ColumnDifference columnDifference : rowDifference.columnDifferences()) {
                report.append("    ")
                        .append(columnDifference.column().name())
                        .append(": left=")
                        .append(columnDifference.leftValue())
                        .append(", right=")
                        .append(columnDifference.rightValue())
                        .append('\n');
            }
        }
    }
}
