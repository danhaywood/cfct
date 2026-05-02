package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.service.TableComparisonService;

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
        final int totalTables = request.tables().size();
        int completedTables = 0;
        for (final TableRef table : request.tables()) {
            request.options().progressListener().onProgress(new ComparisonProgressEvent(
                    table,
                    ComparisonProgressPhase.TABLE_STARTED,
                    completedTables,
                    totalTables,
                    "Comparing " + table.displayName()));
            try {
                tableResults.add(tableComparer.compare(
                        leftConnection,
                        rightConnection,
                        new TableComparisonRequest(table, request.options())));
                completedTables++;
                request.options().progressListener().onProgress(new ComparisonProgressEvent(
                        table,
                        ComparisonProgressPhase.TABLE_COMPLETED,
                        completedTables,
                        totalTables,
                        "Compared " + table.displayName()));
            } catch (RuntimeException ex) {
                request.options().progressListener().onProgress(new ComparisonProgressEvent(
                        table,
                        ComparisonProgressPhase.TABLE_FAILED,
                        completedTables,
                        totalTables,
                        ex.getMessage()));
                throw ex;
            }
        }
        return new MultiTableComparisonResult(tableResults);
    }
}
