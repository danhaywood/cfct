package com.danhaywood.sqlcomparer.model;

public record ColumnRef(String name) {

    public ColumnRef {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }
}
