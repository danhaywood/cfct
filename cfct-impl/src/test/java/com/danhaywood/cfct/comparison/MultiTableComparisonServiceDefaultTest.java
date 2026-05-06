package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.TableComparisonService;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MultiTableComparisonServiceDefaultTest {

    @Test
    void emitsRequestOrderedStartsAndMonotonicCompletionCounts() {
        final TableComparisonService tableComparisonService = mock(TableComparisonService.class);
        when(tableComparisonService.compare(any(Connection.class), any(Connection.class), any(TableComparisonRequest.class)))
                .thenAnswer(invocation -> tableResult(invocation.getArgument(2, TableComparisonRequest.class).table()));

        final List<ComparisonProgressEvent> events = new ArrayList<>();
        final ComparisonOptions options = new ComparisonOptions("_PK", Set.of("version"), events::add, 2);
        final MultiTableComparisonRequest request = new MultiTableComparisonRequest(
                List.of(new TableRef("dbo", "Supplier"), new TableRef("dbo", "Product")),
                options);

        final MultiTableComparisonResult result = new MultiTableComparisonServiceDefault(tableComparisonService)
                .compare(mock(Connection.class), mock(Connection.class), request);

        assertThat(result.tableResults()).extracting(r -> r.table().displayName())
                .containsExactly("dbo.Supplier", "dbo.Product");

        assertThat(events).filteredOn(event -> event.phase() == ComparisonProgressPhase.TABLE_STARTED)
                .extracting(event -> event.table().displayName())
                .containsExactly("dbo.Supplier", "dbo.Product");

        assertThat(events).filteredOn(event -> event.phase() == ComparisonProgressPhase.TABLE_COMPLETED)
                .extracting(ComparisonProgressEvent::completedTables)
                .containsExactly(1, 2);
    }

    @Test
    void allowsCompletionEventsToArriveOutOfRequestOrderWhileResultOrderStaysDeterministic() throws Exception {
        final CountDownLatch supplierStarted = new CountDownLatch(1);
        final CountDownLatch releaseSupplier = new CountDownLatch(1);

        final TableComparisonService tableComparisonService = mock(TableComparisonService.class);
        when(tableComparisonService.compare(any(Connection.class), any(Connection.class), any(TableComparisonRequest.class)))
                .thenAnswer(invocation -> {
                    final TableComparisonRequest request = invocation.getArgument(2, TableComparisonRequest.class);
                    if ("Supplier".equals(request.table().tableName())) {
                        supplierStarted.countDown();
                        if (!releaseSupplier.await(2, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Timed out waiting to release supplier comparison.");
                        }
                        TimeUnit.MILLISECONDS.sleep(150);
                    } else {
                        supplierStarted.await(2, TimeUnit.SECONDS);
                        releaseSupplier.countDown();
                    }
                    return tableResult(request.table());
                });

        final List<ComparisonProgressEvent> events = new ArrayList<>();
        final ComparisonOptions options = new ComparisonOptions("_PK", Set.of("version"), events::add, 2);
        final MultiTableComparisonRequest request = new MultiTableComparisonRequest(
                List.of(new TableRef("dbo", "Supplier"), new TableRef("dbo", "Product")),
                options);

        final MultiTableComparisonResult result = new MultiTableComparisonServiceDefault(tableComparisonService)
                .compare(mock(Connection.class), mock(Connection.class), request);

        assertThat(result.tableResults()).extracting(r -> r.table().displayName())
                .containsExactly("dbo.Supplier", "dbo.Product");

        final List<String> completedOrder = events.stream()
                .filter(event -> event.phase() == ComparisonProgressPhase.TABLE_COMPLETED)
                .map(event -> event.table().displayName())
                .toList();
        assertThat(completedOrder).containsExactly("dbo.Product", "dbo.Supplier");

        final List<Integer> completedCounts = events.stream()
                .filter(event -> event.phase() == ComparisonProgressPhase.TABLE_COMPLETED)
                .map(ComparisonProgressEvent::completedTables)
                .toList();
        assertThat(completedCounts).containsExactly(1, 2);
    }

    private TableComparisonResult tableResult(final TableRef tableRef) {
        return new TableComparisonResult(
                tableRef,
                new BusinessKey("pk", List.of(new ColumnRef("id"))),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }
}
