package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.spi.CommandAuditTouchedTableResolver;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        final CommandAuditTouchedTableResolver touchedTableResolver = mock(CommandAuditTouchedTableResolver.class);

        final CliComparisonExecutor executor = new CliComparisonExecutorSqlServer(comparisonService, renderer, touchedTableResolver) {
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

    @Test
    void infersTablesFromCommandTimeRangeUsingInclusiveSelection() throws Exception {
        final MultiTableComparisonService comparisonService = mock(MultiTableComparisonService.class);
        when(comparisonService.compare(any(Connection.class), any(Connection.class), any(MultiTableComparisonRequest.class)))
                .thenReturn(new MultiTableComparisonResult(List.of()));

        final CliComparisonReportRenderer renderer = mock(CliComparisonReportRenderer.class);
        when(renderer.render(any(), any())).thenReturn(CliExecutionOutput.text("ok"));
        final CommandAuditTouchedTableResolver touchedTableResolver = mock(CommandAuditTouchedTableResolver.class);
        when(touchedTableResolver.resolveTouchedQualifiedTableNames(any(Connection.class), any()))
                .thenReturn(new java.util.TreeSet<>(List.of("dbo.Product", "dbo.Supplier")));

        final CliComparisonExecutorSqlServer executor = new CliComparisonExecutorSqlServer(comparisonService, renderer, touchedTableResolver) {
            @Override
            protected Connection openConnection(final String jdbcUrl, final String username, final String password) {
                return mock(Connection.class);
            }

            @Override
            protected List<String> selectInteractionIdsInRange(final Connection leftConnection, final LocalDateTime commandsFrom, final LocalDateTime commandsTo) {
                assertThat(commandsFrom).isEqualTo(LocalDateTime.parse("2026-05-01T10:00:00"));
                assertThat(commandsTo).isEqualTo(LocalDateTime.parse("2026-05-01T11:00:00"));
                return List.of("11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222");
            }
        };

        final CliArguments arguments = new CliArguments(
                "server", "sa", "secret", "left", "right", List.of(),
                CliOutputFormat.TEXT, null,
                LocalDateTime.parse("2026-05-01T10:00:00"),
                LocalDateTime.parse("2026-05-01T11:00:00"));

        executor.execute(arguments, new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));

        final var requestCaptor = org.mockito.ArgumentCaptor.forClass(MultiTableComparisonRequest.class);
        verify(comparisonService).compare(any(Connection.class), any(Connection.class), requestCaptor.capture());
        assertThat(requestCaptor.getValue().tables()).extracting(TableRef::displayName)
                .containsExactly("dbo.Product", "dbo.Supplier");
    }

    @Test
    void failsWhenCommandTimeRangeSelectsNoCommands() {
        final MultiTableComparisonService comparisonService = mock(MultiTableComparisonService.class);
        final CliComparisonReportRenderer renderer = mock(CliComparisonReportRenderer.class);
        final CommandAuditTouchedTableResolver touchedTableResolver = mock(CommandAuditTouchedTableResolver.class);

        final CliComparisonExecutorSqlServer executor = new CliComparisonExecutorSqlServer(comparisonService, renderer, touchedTableResolver) {
            @Override
            protected Connection openConnection(final String jdbcUrl, final String username, final String password) {
                return mock(Connection.class);
            }

            @Override
            protected List<String> selectInteractionIdsInRange(final Connection leftConnection, final LocalDateTime commandsFrom, final LocalDateTime commandsTo) {
                return List.of();
            }
        };

        final CliArguments arguments = new CliArguments(
                "server", "sa", "secret", "left", "right", List.of(),
                CliOutputFormat.TEXT, null,
                LocalDateTime.parse("2026-05-01T10:00:00"),
                LocalDateTime.parse("2026-05-01T11:00:00"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> executor.execute(arguments))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No commands found");
    }
}
