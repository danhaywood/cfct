package com.danhaywood.sqlcomparer.core;

import java.util.List;

public record MultiTableComparisonResult(List<TableComparisonResult> tableResults) {

    public MultiTableComparisonResult {
        tableResults = List.copyOf(tableResults);
    }

    public boolean hasDifferences() {
        return tableResults.stream().anyMatch(TableComparisonResult::hasDifferences);
    }
}
