package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.config.WebappDatasourceProperties;
import com.danhaywood.cfct.webapp.selection.CommandCatalogEntry;
import com.danhaywood.cfct.webapp.selection.CommandDrivenTableSelectionService;
import com.danhaywood.cfct.webapp.selection.DatabaseSide;
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
import static org.mockito.ArgumentMatchers.eq;
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
    void refreshUnionsCompletedBackgroundFootprintsFromBothDatabasesAndReportsPendingWork() {
        final List<CommandCatalogEntry> left = List.of(
                foreground("newest-ok", "OK", "2026-06-12T07:00:00"),
                background("left-completed", "newest-ok", "UNDEFINED", "2026-06-12T07:01:00", "2026-06-12T07:02:00"),
                background("left-pending", "newest-ok", "UNDEFINED", "2026-06-12T07:03:00", null),
                background("left-failed", "newest-ok", "FAILED", "2026-06-12T07:04:00", "2026-06-12T07:05:00"));
        final List<CommandCatalogEntry> right = List.of(
                foreground("newest-ok", "OK", "2026-06-12T07:00:00"),
                background("different-right-id", "newest-ok", "UNDEFINED", "2026-06-12T07:01:30", "2026-06-12T07:02:30"));
        final Fixture fixture = fixture(left, right, Set.of(SUPPLIER), Set.of(APPLICATION_USER));
        stubComparison(fixture);

        final AutomationComparisonService.AutomationRefreshResult result = fixture.service().refresh();

        assertThat(result.conflict()).isFalse();
        assertThat(result.latestResult().json())
                .contains("\"command\" : {")
                .contains("\"interactionId\" : \"newest-ok\"")
                .contains("\"pending\" : 1")
                .contains("\"completed\" : 2")
                .contains("\"failed\" : 1")
                .contains("\"appA\" : {")
                .contains("\"appB\" : {")
                .contains("\"left-completed\"")
                .contains("\"different-right-id\"");
        assertThat(result.latestResult().tableCount()).isEqualTo(2);
        assertThat(result.latestResult().backgroundCommands().pending()).isEqualTo(1);
        assertThat(result.latestResult().backgroundCommands().completed()).isEqualTo(2);
        assertThat(result.latestResult().backgroundCommands().failed()).isEqualTo(1);
        verify(fixture.commandDrivenTableSelectionService).resolveTouchedBusinessTables(
                List.of("newest-ok", "left-completed"), fixture.tableCatalog, EXPECTED_CONTEXT, DatabaseSide.LEFT);
        verify(fixture.commandDrivenTableSelectionService).resolveTouchedBusinessTables(
                List.of("newest-ok", "different-right-id"), fixture.tableCatalog, EXPECTED_CONTEXT, DatabaseSide.RIGHT);
        verify(fixture.executionService).compare(
                org.mockito.ArgumentMatchers.argThat(request -> Set.copyOf(request.tables()).equals(Set.of(SUPPLIER, APPLICATION_USER))),
                isNull(),
                eq(EXPECTED_CONTEXT));
    }

    @Test
    void discoversNestedDescendantsWithoutIncludingUnrelatedBackgroundCommands() {
        final List<CommandCatalogEntry> catalog = List.of(
                background("child", "root", "UNDEFINED", "2026-06-12T07:01:00", "done"),
                background("grandchild", "child", "UNDEFINED", "2026-06-12T07:02:00", "done"),
                background("unrelated", "other", "UNDEFINED", "2026-06-12T07:03:00", "done"));

        assertThat(AutomationComparisonService.backgroundDescendants(catalog, "ROOT"))
                .extracting(CommandCatalogEntry::interactionId)
                .containsExactly("child", "grandchild");
    }

    @Test
    void selectsNewestCommonSuccessfulForeground() {
        final List<CommandCatalogEntry> left = List.of(
                foreground("left-only-newer", "OK", "2026-06-12T08:00:00"),
                foreground("common", "OK", "2026-06-12T07:00:00"));
        final List<CommandCatalogEntry> right = List.of(
                foreground("left-only-newer", "PENDING", "2026-06-12T08:00:00"),
                foreground("common", "OK", "2026-06-12T07:00:00"));
        final Fixture fixture = fixture(left, right, Set.of(), Set.of());

        final AutomationComparisonService.AutomationRefreshResult result = fixture.service().refresh();

        assertThat(result.latestResult().command().interactionId()).isEqualTo("common");
    }

    @Test
    void refreshReturnsMachineReadableOutcomeWhenNoCommonSuccessfulForegroundExists() {
        final Fixture fixture = fixture(
                List.of(foreground("left", "OK", "2026-06-12T08:00:00")),
                List.of(foreground("right", "OK", "2026-06-12T08:00:00")),
                Set.of(),
                Set.of());

        final AutomationComparisonService.AutomationRefreshResult result = fixture.service().refresh();

        assertThat(result.latestResult().json()).contains("\"status\" : \"no_completed_foreground\"");
        assertThat(result.latestResult().command()).isNull();
        verify(fixture.tableCatalogService, never()).discoverTableCatalog(any(), any());
    }

    @Test
    void refreshFailureDoesNotReturnCachedResult() {
        final Fixture fixture = fixture();
        when(fixture.executionService.compare(any(), isNull(), any()))
                .thenReturn(outcome("{\"ok\":true}\n"))
                .thenThrow(new IllegalStateException("database unavailable"));
        final AutomationComparisonService service = fixture.service();

        assertThat(service.refresh().latestResult().json()).contains("\"ok\" : true");
        assertThatThrownBy(service::refresh)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("database unavailable");
    }

    @Test
    void refreshReturnsEmptyJsonWhenNoEligibleTouchedTablesResolve() {
        final Fixture fixture = fixture(List.of(foreground("newest-ok", "OK", "2026-06-12T07:00:00")),
                List.of(foreground("newest-ok", "OK", "2026-06-12T07:00:00")), Set.of(), Set.of());

        final AutomationComparisonService.AutomationRefreshResult result = fixture.service().refresh();

        assertThat(result.latestResult().json())
                .contains("\"hasDifferences\" : false")
                .contains("\"command\" : {");
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
            return outcome("{}\n");
        });
        final AutomationComparisonService service = fixture.service();
        final Thread refreshThread = new Thread(service::refresh);
        refreshThread.start();
        assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();

        assertThat(service.refresh().conflict()).isTrue();
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
        return fixture(
                List.of(foreground("newest-ok", "OK", "2026-06-12T07:00:00")),
                List.of(foreground("newest-ok", "OK", "2026-06-12T07:00:00")),
                Set.of(SUPPLIER),
                Set.of(APPLICATION_USER));
    }

    private static Fixture fixture(
            final List<CommandCatalogEntry> leftCommands,
            final List<CommandCatalogEntry> rightCommands,
            final Set<TableRef> leftTouchedTables,
            final Set<TableRef> rightTouchedTables) {
        final WebappComparisonExecutionService executionService = mock(WebappComparisonExecutionService.class);
        final SqlServerCommandCatalogService commandCatalogService = mock(SqlServerCommandCatalogService.class);
        final SqlServerTableCatalogService tableCatalogService = mock(SqlServerTableCatalogService.class);
        final CommandDrivenTableSelectionService commandDrivenTableSelectionService = mock(CommandDrivenTableSelectionService.class);
        final List<TableCatalogEntry> tableCatalog = List.of(TableCatalogEntry.eligible(SUPPLIER), TableCatalogEntry.eligible(APPLICATION_USER));
        when(commandCatalogService.discoverCommandCatalog(EXPECTED_CONTEXT, DatabaseSide.LEFT)).thenReturn(leftCommands);
        when(commandCatalogService.discoverCommandCatalog(EXPECTED_CONTEXT, DatabaseSide.RIGHT)).thenReturn(rightCommands);
        when(tableCatalogService.discoverTableCatalog(EXPECTED_CONTEXT, DatabaseSide.LEFT)).thenReturn(tableCatalog);
        when(tableCatalogService.discoverTableCatalog(EXPECTED_CONTEXT, DatabaseSide.RIGHT)).thenReturn(tableCatalog);
        when(commandDrivenTableSelectionService.resolveTouchedBusinessTables(any(), eq(tableCatalog), eq(EXPECTED_CONTEXT), eq(DatabaseSide.LEFT)))
                .thenReturn(leftTouchedTables);
        when(commandDrivenTableSelectionService.resolveTouchedBusinessTables(any(), eq(tableCatalog), eq(EXPECTED_CONTEXT), eq(DatabaseSide.RIGHT)))
                .thenReturn(rightTouchedTables);
        return new Fixture(executionService, commandCatalogService, tableCatalogService, commandDrivenTableSelectionService, tableCatalog);
    }

    private static void stubComparison(final Fixture fixture) {
        when(fixture.executionService.compare(any(), isNull(), any())).thenReturn(outcome(
                "{\"hasDifferences\":false,\"differingTables\":[],\"comparedTables\":[]}\n"));
    }

    private static WebappComparisonExecutionService.ComparisonExecutionOutcome outcome(final String json) {
        return new WebappComparisonExecutionService.ComparisonExecutionOutcome(
                new MultiTableComparisonResult(List.of()),
                new com.danhaywood.cfct.model.MultiTableComparisonViewResult(List.of()),
                json,
                "{}\n",
                new byte[]{1});
    }

    private static CommandCatalogEntry foreground(
            final String interactionId,
            final String replayState,
            final String timestamp) {
        return new CommandCatalogEntry(interactionId, null, "member", "target", replayState, "FOREGROUND", timestamp, timestamp, false);
    }

    private static CommandCatalogEntry background(
            final String interactionId,
            final String parentInteractionId,
            final String replayState,
            final String timestamp,
            final String completedAt) {
        return new CommandCatalogEntry(interactionId, parentInteractionId, "member", "target", replayState, "BACKGROUND", timestamp, completedAt, false);
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
