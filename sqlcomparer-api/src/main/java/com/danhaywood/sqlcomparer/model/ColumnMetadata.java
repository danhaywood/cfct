package com.danhaywood.cfct.model;

public record ColumnMetadata(ColumnRef column, boolean identity, String sqlTypeName) {

    public boolean uniqueIdentifierType() {
        return sqlTypeName != null && "uniqueidentifier".equalsIgnoreCase(sqlTypeName);
    }
}
