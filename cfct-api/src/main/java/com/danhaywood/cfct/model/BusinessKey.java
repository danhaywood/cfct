package com.danhaywood.cfct.model;

import java.util.List;

public record BusinessKey(String indexName, List<ColumnRef> columns) {

    public BusinessKey {
        if (indexName == null || indexName.isBlank()) {
            throw new IllegalArgumentException("indexName is required");
        }
        columns = List.copyOf(columns);
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("columns are required");
        }
    }

    public String display() {
        return indexName + "(" + columns.stream().map(ColumnRef::name).collect(java.util.stream.Collectors.joining(", ")) + ")";
    }
}
