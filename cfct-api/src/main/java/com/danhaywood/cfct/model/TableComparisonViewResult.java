package com.danhaywood.cfct.model;

import java.util.List;

public record TableComparisonViewResult(
        TableRef table,
        List<ColumnRef> comparedColumns,
        List<ComparisonRowView> rows
) {

    public TableComparisonViewResult {
        comparedColumns = List.copyOf(comparedColumns);
        rows = List.copyOf(rows);
    }

    public String tableDisplayName() {
        return table.displayName();
    }
}
