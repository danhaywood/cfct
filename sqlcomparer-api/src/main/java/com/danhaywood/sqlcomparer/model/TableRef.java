package com.danhaywood.cfct.model;

public record TableRef(String schemaName, String tableName) {

    public TableRef {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("schemaName is required");
        }
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("tableName is required");
        }
    }

    public String displayName() {
        return schemaName + "." + tableName;
    }
}
