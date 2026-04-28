package com.danhaywood.sqlcomparer.model;

import java.util.List;
import java.util.Map;

public record ComparisonRowView(
        RowKey key,
        ComparisonRowStatus status,
        Map<ColumnRef, String> leftValues,
        Map<ColumnRef, String> rightValues,
        List<ColumnRef> differingColumns
) {

    public ComparisonRowView {
        leftValues = Map.copyOf(leftValues);
        rightValues = Map.copyOf(rightValues);
        differingColumns = List.copyOf(differingColumns);
    }
}
