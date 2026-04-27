package com.danhaywood.sqlcomparer.comparison;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.service.TableComparisonService;

import java.sql.Connection;
import java.util.ArrayList;

public final class MultiTableComparisonServiceDefault implements MultiTableComparisonService {

    private final TableComparisonService tableComparer;

    public MultiTableComparisonServiceDefault(final TableComparisonService tableComparer) {
        this.tableComparer = tableComparer;
    }

    @Override
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
