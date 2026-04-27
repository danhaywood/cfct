package com.danhaywood.sqlcomparer.webapp.comparison;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import com.danhaywood.sqlcomparer.model.TableRef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebappComparisonExecutionServiceTest {

    @Test
    void delegatesToApiComparisonService() {
        final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(List.of(new TableRef("dbo", "Supplier")));
        final MultiTableComparisonResult expected = new MultiTableComparisonResult(List.of());
        final Connection leftConnection = mock(Connection.class);
        final Connection rightConnection = mock(Connection.class);
        final MultiTableComparisonService delegate = mock(MultiTableComparisonService.class);
        when(delegate.compare(leftConnection, rightConnection, request)).thenReturn(expected);

        final WebappComparisonExecutionService service = new WebappComparisonExecutionService(delegate);

        assertThat(service.compare(leftConnection, rightConnection, request)).isSameAs(expected);
    }
}
