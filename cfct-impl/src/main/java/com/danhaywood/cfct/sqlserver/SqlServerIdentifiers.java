package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.TableRef;

final class SqlServerIdentifiers {

    private SqlServerIdentifiers() {
    }

    static String quoteTable(final TableRef table) {
        return quote(table.schemaName()) + "." + quote(table.tableName());
    }

    static String quoteColumn(final ColumnRef column) {
        return quote(column.name());
    }

    private static String quote(final String identifier) {
        return "[" + identifier.replace("]", "]]" ) + "]";
    }
}
