package com.danhaywood.sqlcomparer.model;

import java.util.List;

public record MultiTableComparisonViewResult(List<TableComparisonViewResult> tableResults) {

    public MultiTableComparisonViewResult {
        tableResults = List.copyOf(tableResults);
    }

    public boolean hasDifferences() {
        return tableResults.stream()
                .flatMap(table -> table.rows().stream())
                .anyMatch(row -> row.status() != ComparisonRowStatus.MATCH);
    }
}
