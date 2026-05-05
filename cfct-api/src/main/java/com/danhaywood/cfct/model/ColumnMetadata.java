package com.danhaywood.cfct.model;

public record ColumnMetadata(
        ColumnRef column,
        boolean identity,
        String sqlTypeName,
        String ignoredExtendedPropertyValue,
        String normalizeMaskExtendedPropertyValue) {

    public ColumnMetadata(final ColumnRef column, final boolean identity, final String sqlTypeName) {
        this(column, identity, sqlTypeName, null, null);
    }

    public ColumnMetadata(
            final ColumnRef column,
            final boolean identity,
            final String sqlTypeName,
            final String ignoredExtendedPropertyValue) {
        this(column, identity, sqlTypeName, ignoredExtendedPropertyValue, null);
    }

    public boolean uniqueIdentifierType() {
        return sqlTypeName != null && "uniqueidentifier".equalsIgnoreCase(sqlTypeName);
    }
}
