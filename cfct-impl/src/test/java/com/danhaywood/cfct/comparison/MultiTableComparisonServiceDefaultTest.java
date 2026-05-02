package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.TableComparisonService;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MultiTableComparisonServiceDefaultTest {

    @Test
    void emitsProgressEventsInTableOrder() {
        final TableComparisonService tableComparisonService = mock(TableComparisonService.class);
        when(tableComparisonService.compare(
                any(Connection.class),
                any(Connection.class),
                any(com.danhaywood.cfct.request.TableComparisonRequest.class))).thenAnswer(invocation -> {
            final var request = invocation.getArgument(2, com.danhaywood.cfct.request.TableComparisonRequest.class);
            return new TableComparisonResult(request.table(), new BusinessKey("pk", List.of(new ColumnRef("id"))), List.of(), List.of(), List.of(), List.of(), List.of());
        });

        final List<ComparisonProgressEvent> events = new ArrayList<>();
        final ComparisonOptions options = new ComparisonOptions("_PK", java.util.Set.of("version"), events::add);
        final MultiTableComparisonRequest request = new MultiTableComparisonRequest(
                List.of(new TableRef("dbo", "Supplier"), new TableRef("dbo", "Product")),
                options);

        final MultiTableComparisonResult result = new MultiTableComparisonServiceDefault(tableComparisonService)
                .compare(mock(Connection.class), mock(Connection.class), request);

        assertThat(result.tableResults()).hasSize(2);
        assertThat(events).extracting(ComparisonProgressEvent::phase)
                .containsExactly(
                        ComparisonProgressPhase.TABLE_STARTED,
                        ComparisonProgressPhase.TABLE_COMPLETED,
                        ComparisonProgressPhase.TABLE_STARTED,
                        ComparisonProgressPhase.TABLE_COMPLETED);
        assertThat(events.get(0).table().displayName()).isEqualTo("dbo.Supplier");
        assertThat(events.get(1).completedTables()).isEqualTo(1);
        assertThat(events.get(3).completedTables()).isEqualTo(2);
    }
}
