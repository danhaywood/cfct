package com.danhaywood.cfct.webapp.comparison;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.MultiTableComparisonReportFormatter;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;

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
        final MultiTableComparisonResult expectedRaw = new MultiTableComparisonResult(List.of());
        final Connection leftConnection = mock(Connection.class);
        final Connection rightConnection = mock(Connection.class);
        final DataSource leftDataSource = mock(DataSource.class);
        final DataSource rightDataSource = mock(DataSource.class);
        when(leftDataSource.getConnection()).thenReturn(leftConnection);
        when(rightDataSource.getConnection()).thenReturn(rightConnection);

        final WebappDataSourceConfiguration dataSourceConfiguration = mock(WebappDataSourceConfiguration.class);
        when(dataSourceConfiguration.dataSourcesFor(mockContext())).thenReturn(new com.danhaywood.cfct.webapp.config.WebappDataSources(mock(DataSource.class), leftDataSource, rightDataSource));

        final AuthenticatedConnectionContextHolder contextHolder = mock(AuthenticatedConnectionContextHolder.class);
        when(contextHolder.required()).thenReturn(mockContext());

        final RecordingComparisonService delegate = new RecordingComparisonService(expectedRaw);
        final MultiTableComparisonReportFormatter formatter = mock(MultiTableComparisonReportFormatter.class);
        when(formatter.renderJson(expectedRaw)).thenReturn("{}");
        when(formatter.renderYaml(expectedRaw)).thenReturn("a: b\n");
        when(formatter.renderExcel(expectedRaw)).thenReturn(new byte[]{1, 2, 3});

        final WebappComparisonExecutionService service = new WebappComparisonExecutionService(
                delegate,
                formatter,
                dataSourceConfiguration,
                contextHolder);

        final WebappComparisonExecutionService.ComparisonExecutionOutcome outcome = service.compare(request);

        assertThat(outcome.rawResult()).isSameAs(expectedRaw);
        assertThat(outcome.viewResult().tableResults()).isEmpty();
        assertThat(outcome.json()).isEqualTo("{}");
        assertThat(outcome.yaml()).isEqualTo("a: b\n");
        assertThat(outcome.excel()).containsExactly(1, 2, 3);
        assertThat(delegate.leftConnection).isSameAs(leftConnection);
        assertThat(delegate.rightConnection).isSameAs(rightConnection);
        assertThat(delegate.request.tables()).isEqualTo(request.tables());
        assertThat(delegate.request.options().businessKeyIndexSuffix())
                .isEqualTo(request.options().businessKeyIndexSuffix());
        assertThat(delegate.request.options().ignoredColumnNames())
                .isEqualTo(request.options().ignoredColumnNames());
        verify(leftConnection).close();
        verify(rightConnection).close();
    }

    private static AuthenticatedConnectionContext mockContext() {
        return new AuthenticatedConnectionContext("server", "sa", "secret", "left_db", "right_db");
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
