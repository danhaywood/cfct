package com.danhaywood.sqlcomparer.comparison;

import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.ComparisonRowStatus;
import com.danhaywood.sqlcomparer.model.ComparisonRowView;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonViewResult;
import com.danhaywood.sqlcomparer.model.RowDifference;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableComparisonViewResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonViewService;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class MultiTableComparisonViewServiceDefault implements MultiTableComparisonViewService {

    private final MultiTableComparisonService comparisonService;

    public MultiTableComparisonViewServiceDefault(final MultiTableComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @Override
    public MultiTableComparisonViewResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final MultiTableComparisonRequest request) {
        final MultiTableComparisonResult result = comparisonService.compare(leftConnection, rightConnection, request);
        return new MultiTableComparisonViewResult(result.tableResults().stream()
                .map(this::toViewResult)
                .toList());
    }

    private TableComparisonViewResult toViewResult(final TableComparisonResult result) {
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
                        difference.columnDifferences().stream().map(diff -> diff.column()).toList()))
                .toList());

        rows.addAll(sideOnlyRows(result.rowsOnlyInLeftValues(), ComparisonRowStatus.ONLY_IN_LEFT));
        rows.addAll(sideOnlyRows(result.rowsOnlyInRightValues(), ComparisonRowStatus.ONLY_IN_RIGHT));

        rows.sort(Comparator.comparing(ComparisonRowView::key));

        return new TableComparisonViewResult(
                result.table(),
                List.copyOf(result.comparedColumns()),
                rows);
    }

    private List<ComparisonRowView> sideOnlyRows(
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
