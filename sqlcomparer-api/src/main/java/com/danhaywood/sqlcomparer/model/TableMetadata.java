package com.danhaywood.sqlcomparer.model;

import java.util.List;

public record TableMetadata(
        TableRef table,
        BusinessKey businessKey,
        List<ColumnMetadata> columns,
        List<ColumnRef> keyColumns,
        List<ColumnRef> ignoredColumns,
        List<ColumnRef> comparedColumns
) {

    public TableMetadata {
        columns = List.copyOf(columns);
        keyColumns = List.copyOf(keyColumns);
        ignoredColumns = List.copyOf(ignoredColumns);
        comparedColumns = List.copyOf(comparedColumns);
    }
}
