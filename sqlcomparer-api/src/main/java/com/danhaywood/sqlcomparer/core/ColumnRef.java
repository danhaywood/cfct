package com.danhaywood.sqlcomparer.core;

public record ColumnRef(String name) {

    public ColumnRef {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }
}
