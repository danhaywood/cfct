package com.danhaywood.cfct.request;

import com.danhaywood.cfct.model.TableRef;

public record TableComparisonRequest(TableRef table, ComparisonOptions options) {

    public TableComparisonRequest {
        if (table == null) {
            throw new IllegalArgumentException("table is required");
        }
        if (options == null) {
            options = ComparisonOptions.defaults();
        }
    }

    public static TableComparisonRequest forTable(final String schemaName, final String tableName) {
        return new TableComparisonRequest(new TableRef(schemaName, tableName), ComparisonOptions.defaults());
    }
}
