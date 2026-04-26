package com.danhaywood.sqlcomparer.core;

import java.util.List;

public record TableComparisonResult(
        TableRef table,
        BusinessKey businessKey,
        List<ColumnRef> comparedColumns,
        List<ColumnRef> ignoredColumns,
        List<RowKey> rowsOnlyInLeft,
        List<RowKey> rowsOnlyInRight,
        List<RowDifference> differingRows
) {

    public TableComparisonResult {
        comparedColumns = List.copyOf(comparedColumns);
        ignoredColumns = List.copyOf(ignoredColumns);
        rowsOnlyInLeft = List.copyOf(rowsOnlyInLeft);
        rowsOnlyInRight = List.copyOf(rowsOnlyInRight);
        differingRows = List.copyOf(differingRows);
    }

    public boolean hasDifferences() {
        return !rowsOnlyInLeft.isEmpty() || !rowsOnlyInRight.isEmpty() || !differingRows.isEmpty();
    }
}
