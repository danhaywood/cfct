package com.danhaywood.sqlcomparer.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class ComparisonViewModelMapper {

    private ComparisonViewModelMapper() {
    }

    public static MultiTableComparisonViewResult toViewResult(final MultiTableComparisonResult result) {
        return new MultiTableComparisonViewResult(result.tableResults().stream()
                .map(ComparisonViewModelMapper::toViewResult)
                .toList());
    }

    private static TableComparisonViewResult toViewResult(final TableComparisonResult result) {
        final List<ComparisonRowView> rows = new ArrayList<>();

        rows.addAll(result.matchingRowsValues().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ComparisonRowView(
                        entry.getKey(),
                        ComparisonRowStatus.MATCH,
                        entry.getValue(),
                        entry.getValue(),
                        List.of()))
                .toList());

        rows.addAll(result.differingRows().stream()
                .sorted(Comparator.comparing(RowDifference::key))
                .map(difference -> new ComparisonRowView(
                        difference.key(),
                        ComparisonRowStatus.DIFFERENT,
                        difference.leftValues(),
                        difference.rightValues(),
                        difference.columnDifferences().stream().map(ColumnDifference::column).toList()))
                .toList());

        rows.addAll(sideOnlyRows(result.rowsOnlyInLeftValues(), ComparisonRowStatus.ONLY_IN_LEFT));
        rows.addAll(sideOnlyRows(result.rowsOnlyInRightValues(), ComparisonRowStatus.ONLY_IN_RIGHT));

        rows.sort(Comparator.comparing(ComparisonRowView::key));

        return new TableComparisonViewResult(
                result.table(),
                List.copyOf(result.comparedColumns()),
                rows);
    }

    private static List<ComparisonRowView> sideOnlyRows(
            final Map<RowKey, Map<ColumnRef, String>> values,
            final ComparisonRowStatus status) {
        return values.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ComparisonRowView(
                        entry.getKey(),
                        status,
                        status == ComparisonRowStatus.ONLY_IN_LEFT ? entry.getValue() : Map.of(),
                        status == ComparisonRowStatus.ONLY_IN_RIGHT ? entry.getValue() : Map.of(),
                        List.of()))
                .toList();
    }
}
