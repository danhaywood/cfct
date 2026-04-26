package com.danhaywood.sqlcomparer.core;

import java.util.List;

public record RowDifference(RowKey key, List<ColumnDifference> columnDifferences) {

    public RowDifference {
        columnDifferences = List.copyOf(columnDifferences);
    }
}
