package com.danhaywood.sqlcomparer.webapp.comparison;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebappComparisonExecutionServiceTest {

    @Test
    void delegatesToApiComparisonServiceUsingDataSourceManagedConnections() throws Exception {
        final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(List.of(new TableRef("dbo", "Supplier")));
        final MultiTableComparisonResult expected = new MultiTableComparisonResult(List.of());
        final Connection leftConnection = mock(Connection.class);
        final Connection rightConnection = mock(Connection.class);
        final DataSource leftDataSource = mock(DataSource.class);
        final DataSource rightDataSource = mock(DataSource.class);
        when(leftDataSource.getConnection()).thenReturn(leftConnection);
        when(rightDataSource.getConnection()).thenReturn(rightConnection);
        final RecordingComparisonService delegate = new RecordingComparisonService(expected);
        final WebappDataSources dataSources = new WebappDataSources(mock(DataSource.class), leftDataSource, rightDataSource);

        final WebappComparisonExecutionService service = new WebappComparisonExecutionService(delegate, dataSources);

        assertThat(service.compare(request)).isSameAs(expected);
        assertThat(delegate.leftConnection).isSameAs(leftConnection);
        assertThat(delegate.rightConnection).isSameAs(rightConnection);
        assertThat(delegate.request).isSameAs(request);
        verify(leftConnection).close();
        verify(rightConnection).close();
    }

    private static final class RecordingComparisonService implements MultiTableComparisonService {

        private final MultiTableComparisonResult result;
        private Connection leftConnection;
        private Connection rightConnection;
        private MultiTableComparisonRequest request;

        private RecordingComparisonService(final MultiTableComparisonResult result) {
            this.result = result;
        }

        @Override
        public MultiTableComparisonResult compare(
                final Connection leftConnection,
                final Connection rightConnection,
                final MultiTableComparisonRequest request) {
            this.leftConnection = leftConnection;
            this.rightConnection = rightConnection;
            this.request = request;
            return result;
        }
    }
}
