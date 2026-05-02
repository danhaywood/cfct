package com.danhaywood.cfct.model;

import java.util.List;
import java.util.Map;

public record TableComparisonResult(
        TableRef table,
        BusinessKey businessKey,
        List<ColumnRef> comparedColumns,
        List<ColumnRef> ignoredColumns,
        List<RowKey> rowsOnlyInLeft,
        List<RowKey> rowsOnlyInRight,
        List<RowDifference> differingRows,
        Map<RowKey, Map<ColumnRef, String>> rowsOnlyInLeftValues,
        Map<RowKey, Map<ColumnRef, String>> rowsOnlyInRightValues,
        Map<RowKey, Map<ColumnRef, String>> matchingRowsValues
) {

    public TableComparisonResult(
            final TableRef table,
            final BusinessKey businessKey,
            final List<ColumnRef> comparedColumns,
            final List<ColumnRef> ignoredColumns,
            final List<RowKey> rowsOnlyInLeft,
            final List<RowKey> rowsOnlyInRight,
            final List<RowDifference> differingRows) {
        this(table, businessKey, comparedColumns, ignoredColumns, rowsOnlyInLeft, rowsOnlyInRight, differingRows, Map.of(), Map.of(), Map.of());
    }

    public TableComparisonResult(
            final TableRef table,
            final BusinessKey businessKey,
            final List<ColumnRef> comparedColumns,
            final List<ColumnRef> ignoredColumns,
            final List<RowKey> rowsOnlyInLeft,
            final List<RowKey> rowsOnlyInRight,
            final List<RowDifference> differingRows,
            final Map<RowKey, Map<ColumnRef, String>> rowsOnlyInLeftValues,
            final Map<RowKey, Map<ColumnRef, String>> rowsOnlyInRightValues) {
        this(table,
                businessKey,
                comparedColumns,
                ignoredColumns,
                rowsOnlyInLeft,
                rowsOnlyInRight,
                differingRows,
                rowsOnlyInLeftValues,
                rowsOnlyInRightValues,
                Map.of());
    }

    public TableComparisonResult {
        comparedColumns = List.copyOf(comparedColumns);
        ignoredColumns = List.copyOf(ignoredColumns);
        rowsOnlyInLeft = List.copyOf(rowsOnlyInLeft);
        rowsOnlyInRight = List.copyOf(rowsOnlyInRight);
        differingRows = List.copyOf(differingRows);
        rowsOnlyInLeftValues = copyRowValues(rowsOnlyInLeftValues);
        rowsOnlyInRightValues = copyRowValues(rowsOnlyInRightValues);
        matchingRowsValues = copyRowValues(matchingRowsValues);
    }

    public boolean hasDifferences() {
        return !rowsOnlyInLeft.isEmpty() || !rowsOnlyInRight.isEmpty() || !differingRows.isEmpty();
    }

    private static Map<RowKey, Map<ColumnRef, String>> copyRowValues(final Map<RowKey, Map<ColumnRef, String>> rowValues) {
        return rowValues.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> Map.copyOf(entry.getValue())));
    }
}
