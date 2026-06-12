package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.config.WebappDatasourceProperties;
import com.danhaywood.cfct.webapp.selection.CommandCatalogEntry;
import com.danhaywood.cfct.webapp.selection.CommandDrivenTableSelectionService;
import com.danhaywood.cfct.webapp.selection.SqlServerCommandCatalogService;
import com.danhaywood.cfct.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.cfct.webapp.selection.TableCatalogEntry;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutomationComparisonServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-06-12T07:00:00Z"), ZoneOffset.UTC);
    private static final AuthenticatedConnectionContext EXPECTED_CONTEXT = new AuthenticatedConnectionContext(
            "jdbc:sqlserver://server;encrypt=false",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "sa",
            "secret",
            "left_db",
            "right_db");
    private static final TableRef SUPPLIER = new TableRef("dbo", "Supplier");
    private static final TableRef APPLICATION_USER = new TableRef("isisExtSecman", "ApplicationUser");

    @Test
    void refreshDerivesTablesFromNewestSuccessfulCommandAndReturnsCurrentFormatterJson() {
        final Fixture fixture = fixture();
        when(fixture.executionService.compare(any(), isNull(), any()))
                .thenReturn(new WebappComparisonExecutionService.ComparisonExecutionOutcome(
                        new MultiTableComparisonResult(List.of()),
                        new com.danhaywood.cfct.model.MultiTableComparisonViewResult(List.of()),
                        "{\"hasDifferences\":false,\"differingTables\":[],\"comparedTables\":[]}\n",
                        "tables: []\n",
                        new byte[]{1}));
        final AutomationComparisonService service = fixture.service();

        final AutomationComparisonService.AutomationRefreshResult result = service.refresh();

        assertThat(result.conflict()).isFalse();
        assertThat(result.latestResult().json()).contains("\"hasDifferences\" : false");
        assertThat(result.latestResult().json()).contains("\"differingTables\" : [ ]");
        assertThat(result.latestResult().json()).contains("\"comparedTables\" : [ ]");
        assertThat(result.latestResult().json()).contains("\"command\" : {");
        assertThat(result.latestResult().json()).contains("\"interactionId\" : \"newest-ok\"");
        assertThat(result.latestResult().json()).contains("\"timestamp\" : \"2026-06-12T07:00:00\"");
        assertThat(result.latestResult().completedAt()).isEqualTo(Instant.parse("2026-06-12T07:00:00Z"));
        assertThat(result.latestResult().tableCount()).isEqualTo(2);
        assertThat(result.latestResult().command()).isEqualTo(new AutomationComparisonService.CommandMetadata("newest-ok", "2026-06-12T07:00:00"));
        verify(fixture.commandCatalogService).discoverCommandCatalog(EXPECTED_CONTEXT);
        verify(fixture.tableCatalogService).discoverTableCatalog(EXPECTED_CONTEXT);
        verify(fixture.commandDrivenTableSelectionService).resolveTouchedBusinessTables(
                List.of("newest-ok"),
                fixture.tableCatalog,
                EXPECTED_CONTEXT);
        verify(fixture.executionService).compare(
                org.mockito.ArgumentMatchers.argThat(request -> Set.copyOf(request.tables()).equals(Set.of(SUPPLIER, APPLICATION_USER))),
                isNull(),
                org.mockito.ArgumentMatchers.eq(EXPECTED_CONTEXT));
    }

    @Test
    void newestSuccessfulCommandIgnoresNewerFailedCommands() {
        final Fixture fixture = fixture(List.of(
                command("newer-failed", "FAILED", "2026-06-12T08:00:00"),
                command("newest-ok", "OK", "2026-06-12T07:00:00"),
                command("older-ok", "OK", "2026-06-12T06:00:00")));
        when(fixture.executionService.compare(any(), isNull(), any()))
                .thenReturn(new WebappComparisonExecutionService.ComparisonExecutionOutcome(
                        new MultiTableComparisonResult(List.of()),
                        new com.danhaywood.cfct.model.MultiTableComparisonViewResult(List.of()),
                        "{}\n",
                        "{}\n",
                        new byte[]{1}));

        fixture.service().refresh();

        verify(fixture.commandDrivenTableSelectionService).resolveTouchedBusinessTables(
                List.of("newest-ok"),
                fixture.tableCatalog,
                EXPECTED_CONTEXT);
    }

    @Test
    void refreshFailureDoesNotReturnCachedResult() {
        final Fixture fixture = fixture();
        when(fixture.executionService.compare(any(), isNull(), any()))
                .thenReturn(new WebappComparisonExecutionService.ComparisonExecutionOutcome(
                        new MultiTableComparisonResult(List.of()),
                        new com.danhaywood.cfct.model.MultiTableComparisonViewResult(List.of()),
                        "{\"ok\":true}\n",
                        "ok: true\n",
                        new byte[]{1}))
                .thenThrow(new IllegalStateException("database unavailable"));
        final AutomationComparisonService service = fixture.service();

        final String firstJson = service.refresh().latestResult().json();
        assertThat(firstJson).contains("\"ok\" : true");
        assertThat(firstJson).contains("\"command\" : {");
        assertThatThrownBy(service::refresh)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("database unavailable");
    }

    @Test
    void refreshFailsWhenNoSuccessfulCommandExists() {
        final Fixture fixture = fixture(List.of(command("failed", "FAILED", "2026-06-12T08:00:00")));

        assertThatThrownBy(fixture.service()::refresh)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No successful command is available for automation refresh.");
    }

    @Test
    void refreshReturnsEmptyJsonWhenNoEligibleTouchedTablesResolve() {
        final Fixture fixture = fixture(Set.of());

        final AutomationComparisonService.AutomationRefreshResult result = fixture.service().refresh();

        assertThat(result.conflict()).isFalse();
        assertThat(result.latestResult().json()).contains("\"hasDifferences\" : false");
        assertThat(result.latestResult().json()).contains("\"differingTables\" : [ ]");
        assertThat(result.latestResult().json()).contains("\"comparedTables\" : [ ]");
        assertThat(result.latestResult().json()).contains("\"command\" : {");
        assertThat(result.latestResult().json()).contains("\"interactionId\" : \"newest-ok\"");
        assertThat(result.latestResult().json()).contains("\"timestamp\" : \"2026-06-12T07:00:00\"");
        assertThat(result.latestResult().command()).isEqualTo(new AutomationComparisonService.CommandMetadata("newest-ok", "2026-06-12T07:00:00"));
        assertThat(result.latestResult().tableCount()).isZero();
        verify(fixture.executionService, never()).compare(any(), isNull(), any());
    }

    @Test
    void refreshReturnsConflictWhenAnotherRefreshIsRunning() throws Exception {
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);
        final Fixture fixture = fixture();
        when(fixture.executionService.compare(any(), isNull(), any())).thenAnswer(invocation -> {
            entered.countDown();
            release.await(5, TimeUnit.SECONDS);
            return new WebappComparisonExecutionService.ComparisonExecutionOutcome(
                    new MultiTableComparisonResult(List.of()),
                    new com.danhaywood.cfct.model.MultiTableComparisonViewResult(List.of()),
                    "{}\n",
                    "{}\n",
                    new byte[]{1});
        });
        final AutomationComparisonService service = fixture.service();
        final Thread refreshThread = new Thread(service::refresh);
        refreshThread.start();
        assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();

        final AutomationComparisonService.AutomationRefreshResult conflict = service.refresh();

        assertThat(conflict.conflict()).isTrue();
        release.countDown();
        refreshThread.join(5_000);
    }

    @Test
    void disabledAutomationRejectsRefresh() {
        final WebappComparisonProperties properties = properties();
        properties.getAutomation().setEnabled(false);
        final AutomationComparisonService service = new AutomationComparisonService(
                properties,
                datasourceProperties(),
                mock(WebappComparisonExecutionService.class),
                mock(SqlServerCommandCatalogService.class),
                mock(SqlServerTableCatalogService.class),
                mock(CommandDrivenTableSelectionService.class),
                FIXED_CLOCK);

        assertThatThrownBy(service::refresh)
                .isInstanceOf(AutomationComparisonService.AutomationDisabledException.class);
    }

    private static Fixture fixture() {
        return fixture(List.of(
                command("newest-ok", "OK", "2026-06-12T07:00:00"),
                command("older-ok", "OK", "2026-06-12T06:00:00")));
    }

    private static Fixture fixture(final List<CommandCatalogEntry> commandCatalog) {
        return fixture(commandCatalog, Set.of(SUPPLIER, APPLICATION_USER));
    }

    private static Fixture fixture(final Set<TableRef> touchedTables) {
        return fixture(List.of(command("newest-ok", "OK", "2026-06-12T07:00:00")), touchedTables);
    }

    private static Fixture fixture(final List<CommandCatalogEntry> commandCatalog, final Set<TableRef> touchedTables) {
        final WebappComparisonExecutionService executionService = mock(WebappComparisonExecutionService.class);
        final SqlServerCommandCatalogService commandCatalogService = mock(SqlServerCommandCatalogService.class);
        final SqlServerTableCatalogService tableCatalogService = mock(SqlServerTableCatalogService.class);
        final CommandDrivenTableSelectionService commandDrivenTableSelectionService = mock(CommandDrivenTableSelectionService.class);
        final List<TableCatalogEntry> tableCatalog = List.of(TableCatalogEntry.eligible(SUPPLIER), TableCatalogEntry.eligible(APPLICATION_USER));
        when(commandCatalogService.discoverCommandCatalog(EXPECTED_CONTEXT)).thenReturn(commandCatalog);
        when(tableCatalogService.discoverTableCatalog(EXPECTED_CONTEXT)).thenReturn(tableCatalog);
        when(commandDrivenTableSelectionService.resolveTouchedBusinessTables(List.of("newest-ok"), tableCatalog, EXPECTED_CONTEXT))
                .thenReturn(touchedTables);
        return new Fixture(executionService, commandCatalogService, tableCatalogService, commandDrivenTableSelectionService, tableCatalog);
    }

    private static CommandCatalogEntry command(final String interactionId, final String replayState, final String timestamp) {
        return new CommandCatalogEntry(interactionId, "member", "target", replayState, "FOREGROUND", timestamp, timestamp, false);
    }

    private static WebappComparisonProperties properties() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.getAutomation().setEnabled(true);
        properties.getAutomation().setUsername("robot");
        properties.getAutomation().setPassword("secret");
        properties.getAutomation().setLeftDatabase("left_db");
        properties.getAutomation().setRightDatabase("right_db");
        return properties;
    }

    private static WebappDatasourceProperties datasourceProperties() {
        final WebappDatasourceProperties properties = new WebappDatasourceProperties();
        properties.setUrl("jdbc:sqlserver://server;encrypt=false");
        properties.setUsername("sa");
        properties.setPassword("secret");
        return properties;
    }

    private record Fixture(
            WebappComparisonExecutionService executionService,
            SqlServerCommandCatalogService commandCatalogService,
            SqlServerTableCatalogService tableCatalogService,
            CommandDrivenTableSelectionService commandDrivenTableSelectionService,
            List<TableCatalogEntry> tableCatalog) {
        private AutomationComparisonService service() {
            return new AutomationComparisonService(
                    properties(),
                    datasourceProperties(),
                    executionService,
                    commandCatalogService,
                    tableCatalogService,
                    commandDrivenTableSelectionService,
                    FIXED_CLOCK);
        }
    }
}
