package com.danhaywood.sqlcomparer.core;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;

@Service
public final class MultiTableComparer {

    private final TableComparer tableComparer;

    public MultiTableComparer(final TableComparer tableComparer) {
        this.tableComparer = tableComparer;
    }

    public MultiTableComparisonResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final MultiTableComparisonRequest request) {
        final var tableResults = new ArrayList<TableComparisonResult>();
        for (final TableRef table : request.tables()) {
            tableResults.add(tableComparer.compare(
                    leftConnection,
                    rightConnection,
                    new TableComparisonRequest(table, request.options())));
        }
        return new MultiTableComparisonResult(tableResults);
    }
}
