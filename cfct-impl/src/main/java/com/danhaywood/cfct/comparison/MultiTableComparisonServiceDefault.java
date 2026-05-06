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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        final int totalTables = request.tables().size();
        final int maxWorkers = Math.min(totalTables, request.options().maxParallelComparisons());
        final ExecutorService executor = Executors.newFixedThreadPool(maxWorkers);
        try {
            final ExecutorCompletionService<TableOutcome> completionService = new ExecutorCompletionService<>(executor);
            for (final TableRef table : request.tables()) {
                request.options().progressListener().onProgress(new ComparisonProgressEvent(
                        table,
                        ComparisonProgressPhase.TABLE_STARTED,
                        0,
                        totalTables,
                        "Comparing " + table.displayName()));
                completionService.submit(() -> compareTable(leftConnection, rightConnection, request, table));
            }

            final Map<TableRef, TableComparisonResult> resultsByTable = new LinkedHashMap<>();
            int completedTables = 0;
            RuntimeException firstFailure = null;
            for (int i = 0; i < totalTables; i++) {
                final TableOutcome outcome = awaitOutcome(completionService);
                completedTables++;
                if (outcome.failure() == null) {
                    resultsByTable.put(outcome.table(), outcome.result());
                    request.options().progressListener().onProgress(new ComparisonProgressEvent(
                            outcome.table(),
                            ComparisonProgressPhase.TABLE_COMPLETED,
                            completedTables,
                            totalTables,
                            "Compared " + outcome.table().displayName()));
                } else {
                    request.options().progressListener().onProgress(new ComparisonProgressEvent(
                            outcome.table(),
                            ComparisonProgressPhase.TABLE_FAILED,
                            completedTables,
                            totalTables,
                            outcome.failure().getMessage()));
                    if (firstFailure == null) {
                        firstFailure = outcome.failure();
                    }
                }
            }

            if (firstFailure != null) {
                throw firstFailure;
            }

            final var orderedResults = new ArrayList<TableComparisonResult>(totalTables);
            for (final TableRef table : request.tables()) {
                orderedResults.add(resultsByTable.get(table));
            }
            return new MultiTableComparisonResult(orderedResults);
        } finally {
            executor.shutdownNow();
        }
    }

    private TableOutcome compareTable(
            final Connection leftConnection,
            final Connection rightConnection,
            final MultiTableComparisonRequest request,
            final TableRef table) {
        try {
            return new TableOutcome(
                    table,
                    tableComparer.compare(leftConnection, rightConnection, new TableComparisonRequest(table, request.options())),
                    null);
        } catch (RuntimeException ex) {
            return new TableOutcome(table, null, ex);
        }
    }

    private TableOutcome awaitOutcome(final ExecutorCompletionService<TableOutcome> completionService) {
        try {
            final Future<TableOutcome> future = completionService.take();
            return future.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while comparing selected tables.", ex);
        } catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Failed to compare selected tables.", cause);
        }
    }

    private record TableOutcome(
            TableRef table,
            TableComparisonResult result,
            RuntimeException failure) {
    }
}
