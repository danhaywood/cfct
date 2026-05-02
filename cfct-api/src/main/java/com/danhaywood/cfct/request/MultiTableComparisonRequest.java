package com.danhaywood.cfct.request;

import com.danhaywood.cfct.model.TableRef;

import java.util.List;

public record MultiTableComparisonRequest(List<TableRef> tables, ComparisonOptions options) {

    public MultiTableComparisonRequest {
        tables = List.copyOf(tables);
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("At least one table is required");
        }
        if (options == null) {
            options = ComparisonOptions.defaults();
        }
    }

    public static MultiTableComparisonRequest forTables(final List<TableRef> tables) {
        return new MultiTableComparisonRequest(tables, ComparisonOptions.defaults());
    }
}
