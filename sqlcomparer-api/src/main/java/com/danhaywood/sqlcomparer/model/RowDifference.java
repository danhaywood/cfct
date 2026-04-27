package com.danhaywood.sqlcomparer.model;

import java.util.List;
import java.util.Map;

public record RowDifference(
        RowKey key,
        Map<ColumnRef, String> leftValues,
        Map<ColumnRef, String> rightValues,
        List<ColumnDifference> columnDifferences
) {

    public RowDifference(final RowKey key, final List<ColumnDifference> columnDifferences) {
        this(key, Map.of(), Map.of(), columnDifferences);
    }

    public RowDifference {
        leftValues = Map.copyOf(leftValues);
        rightValues = Map.copyOf(rightValues);
        columnDifferences = List.copyOf(columnDifferences);
    }
}
