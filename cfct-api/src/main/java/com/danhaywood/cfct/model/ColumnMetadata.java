package com.danhaywood.cfct.model;

public record ColumnMetadata(
        ColumnRef column,
        boolean identity,
        String sqlTypeName,
        String ignoredExtendedPropertyValue) {

    public ColumnMetadata(final ColumnRef column, final boolean identity, final String sqlTypeName) {
        this(column, identity, sqlTypeName, null);
    }

    public boolean uniqueIdentifierType() {
        return sqlTypeName != null && "uniqueidentifier".equalsIgnoreCase(sqlTypeName);
    }
}
