package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.MultiTableComparisonService;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CliComparisonExecutorSqlServerTest {

    @Test
    void printsProgressEventsToStderr() throws Exception {
        final MultiTableComparisonService comparisonService = mock(MultiTableComparisonService.class);
        when(comparisonService.compare(any(Connection.class), any(Connection.class), any(MultiTableComparisonRequest.class)))
                .thenAnswer(invocation -> {
                    final MultiTableComparisonRequest request = invocation.getArgument(2);
                    request.options().progressListener().onProgress(new ComparisonProgressEvent(
                            request.tables().get(0), ComparisonProgressPhase.TABLE_STARTED, 0, 1, "start"));
                    request.options().progressListener().onProgress(new ComparisonProgressEvent(
                            request.tables().get(0), ComparisonProgressPhase.TABLE_COMPLETED, 1, 1, "done"));
                    return new MultiTableComparisonResult(List.of());
                });

        final CliComparisonReportRenderer renderer = mock(CliComparisonReportRenderer.class);
        when(renderer.render(any(), any())).thenReturn(CliExecutionOutput.text("ok"));

        final CliComparisonExecutor executor = new CliComparisonExecutorSqlServer(comparisonService, renderer) {
            @Override
            protected Connection openConnection(final String jdbcUrl, final String username, final String password) {
                return mock(Connection.class);
            }
        };
        final ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
        final CliArguments arguments = new CliArguments(
                "server", "sa", "secret", "left", "right", List.of(new TableRef("dbo", "Supplier")),
                CliOutputFormat.TEXT, null);

        executor.execute(arguments, new PrintStream(errBytes, true, StandardCharsets.UTF_8));

        final String stderr = errBytes.toString(StandardCharsets.UTF_8);
        assertThat(stderr).contains("[progress] 1/1 comparing dbo.Supplier");
        assertThat(stderr).contains("[progress] 1/1 compared dbo.Supplier");
    }
}
