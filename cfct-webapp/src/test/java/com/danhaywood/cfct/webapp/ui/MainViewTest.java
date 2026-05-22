package com.danhaywood.cfct.webapp.ui;

import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.ComparisonRowStatus;
import com.danhaywood.cfct.model.ComparisonRowView;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.MultiTableComparisonViewResult;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonViewResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.auth.ConnectionLoginRequest;
import com.danhaywood.cfct.webapp.auth.WebappAuthenticationService;
import com.danhaywood.cfct.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.selection.CommandCatalogEntry;
import com.danhaywood.cfct.webapp.selection.CommandDrivenTableSelectionService;
import com.danhaywood.cfct.webapp.selection.SqlServerCommandCatalogService;
import com.danhaywood.cfct.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.cfct.webapp.selection.TableCatalogEntry;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MainViewTest {

    @Test
    void opensLoginModalWhenUnauthenticated() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                unauthenticatedHolder(),
                authenticationServiceWithDefaults());

        final BeforeEnterEvent event = mock(BeforeEnterEvent.class);
        view.beforeEnter(event);

        final Component accountMenu = findByTestId(view, "account-menu").orElseThrow();
        assertThat(accountMenu.isVisible()).isFalse();
        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        assertThat(compareButton.isEnabled()).isFalse();
    }

    @Test
    void rendersAppLayoutShellAndFooterWithoutConnectionStatusLabels() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(
                holder,
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(view.getElement().getAttribute("data-testid")).isEqualTo("main-app-layout");
        assertThat(findByTestId(view, "hamburger-menu")).isPresent();
        assertThat(findByTestId(view, "navbar-branding")).isPresent();
        assertThat(findByTestId(view, "navbar-branding-logo")).isPresent();
        final Span navbarBrand = (Span) findByTestId(view, "navbar-branding-name").orElseThrow();
        assertThat(navbarBrand.getText()).isEqualTo("CFCT");
        assertThat(findByTestId(view, "account-menu")).isPresent();
        final Component accountLabel = findByTestId(view, "account-menu-label").orElseThrow();
        assertThat(accountLabel.getElement().getText()).isEqualTo("sa");
        assertThat(findByTestId(view, "logout-button")).isEmpty();
        assertThat(findByTestId(view, "connection-status-state")).isEmpty();
        assertThat(findByTestId(view, "connection-status-summary")).isEmpty();

        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        assertThat(progress.getText()).isBlank();
    }

    @Test
    void compareActionBarUsesAlignmentClass() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Component actionBar = findByTestId(view, "navigation-compare-action-bar").orElseThrow();
        assertThat(actionBar.getElement().getAttribute("class")).contains("navigation-compare-action-bar");
        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        assertThat(compareButton.getElement().getAttribute("data-default-action")).isEqualTo("compare");
        assertThat(compareButton.getThemeNames()).contains("primary");
        assertThat(findByTestId(view, "navigation-drawer-resize-handle")).isPresent();
    }

    @Test
    void enterShortcutTriggersCompareWhenEnabledAndNotTyping() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeHandleEnterShortcut(view, "DIV", null);

        verify(comparisonExecutionService).compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class));
    }

    @Test
    void enterShortcutDoesNotTriggerCompareWhenDisabledOrTyping() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeHandleEnterShortcut(view, "DIV", null);
        invokeHandleEnterShortcut(view, "INPUT", "text");

        Mockito.verifyNoInteractions(comparisonExecutionService);
    }

    @Test
    void refreshShortcutF5AndAltRTriggerRefresh() {
        final SqlServerCommandCatalogService commandCatalogService = Mockito.mock(SqlServerCommandCatalogService.class);
        when(commandCatalogService.discoverCommandCatalog()).thenReturn(
                List.of(
                        new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "PENDING", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                        new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "OK", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)),
                List.of(
                        new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "PENDING", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                        new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "OK", "FOREGROUND", "2026-04-05T11:00:00.000", null, false),
                        new CommandCatalogEntry("44444444-4444-4444-4444-444444444444", "order.Order#submit", "order.Order:900", "OK", "FOREGROUND", "2026-04-05T12:00:00.000", null, false)),
                List.of(
                        new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "PENDING", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                        new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "OK", "FOREGROUND", "2026-04-05T11:00:00.000", null, false),
                        new CommandCatalogEntry("55555555-5555-5555-5555-555555555555", "customer.Customer#approve", "customer.Customer:501", "OK", "FOREGROUND", "2026-04-05T13:00:00.000", null, false)));

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogService,
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeHandleRefreshShortcut(view, "F5", false, "DIV", null);
        invokeHandleRefreshShortcut(view, "r", true, "DIV", null);

        verify(commandCatalogService, Mockito.times(3)).discoverCommandCatalog();
    }

    @Test
    void refreshShortcutIgnoredWhileTypingInInput() {
        final SqlServerCommandCatalogService commandCatalogService = Mockito.mock(SqlServerCommandCatalogService.class);
        when(commandCatalogService.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "OK", "FOREGROUND", "2026-04-05T10:00:00.000", null, false)));

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogService,
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeHandleRefreshShortcut(view, "F5", false, "INPUT", "text");
        invokeHandleRefreshShortcut(view, "r", true, "TEXTAREA", null);

        verify(commandCatalogService, Mockito.times(1)).discoverCommandCatalog();
    }

    @Test
    void rendersCommandGridAboveTableGridWithSpacerAndCompareBelowGrid() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(findByTestId(view, "command-selection-spacer")).isPresent();
        assertThat(findByTestId(view, "command-selection-grid")).isPresent();
        assertThat(findByTestId(view, "clear-selections-button")).isPresent();
        assertThat(findByTestId(view, "table-selection-grid")).isPresent();

        final List<String> testIdsInOrder = view.getChildren()
                .flatMap(this::streamWithDescendants)
                .map(component -> component.getElement().getAttribute("data-testid"))
                .filter(id -> id != null && !id.isBlank())
                .toList();

        assertThat(testIdsInOrder.indexOf("command-selection-grid"))
                .isLessThan(testIdsInOrder.indexOf("clear-selections-button"));
        assertThat(testIdsInOrder.indexOf("clear-selections-button"))
                .isLessThan(testIdsInOrder.indexOf("table-selection-grid"));
        assertThat(testIdsInOrder.indexOf("table-selection-grid"))
                .isLessThan(testIdsInOrder.indexOf("navigation-compare-action-bar"));
    }

    @Test
    void commandGridDisplaysReplayMemberTimestampInteractionColumnsInOrder() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();
        final List<String> columnKeys = commandGrid.getColumns().stream().map(Grid.Column::getKey).toList();
        assertThat(columnKeys).containsExactly(null, "replay-state", "member", "timestamp", "completed-at", "interaction");
    }

    @Test
    void commandGridDisplaysBackgroundReplayStateFromCompletedAt() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithBackgroundEntries(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();
        final List<?> items = commandGrid.getListDataView().getItems().toList();

        assertThat(items)
                .extracting(item -> ((CommandCatalogEntry) item).replayState())
                .containsExactly("UNDEFINED", "UNDEFINED", "OK");
        assertThat(items)
                .extracting(item -> invokeDisplayReplayState(view, (CommandCatalogEntry) item))
                .containsExactly("BGRND:PEND", "BGRND:DONE", "OK");
    }

    @Test
    void commandGridDefaultsToTimestampAscendingOrder() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithReverseTimestamps(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();
        final List<?> items = commandGrid.getListDataView().getItems().toList();
        assertThat(items)
                .extracting(item -> ((CommandCatalogEntry) item).timestamp())
                .containsExactly("2026-04-05T10:00:00.000", "2026-04-05T11:00:00.000");
    }

    @Test
    void baselinePickerRendersAboveCommandGridAndDefaultsToEmpty() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final DateTimePicker baseline = (DateTimePicker) findByTestId(view, "command-filter-baseline-timestamp").orElseThrow();
        assertThat(baseline.getValue()).isNull();

        final List<String> testIdsInOrder = view.getChildren()
                .flatMap(this::streamWithDescendants)
                .map(component -> component.getElement().getAttribute("data-testid"))
                .filter(id -> id != null && !id.isBlank())
                .toList();

        assertThat(testIdsInOrder.indexOf("command-filter-baseline-timestamp"))
                .isLessThan(testIdsInOrder.indexOf("command-selection-grid"));
    }

    @Test
    void refreshButtonRendersToLeftOfBaselineFields() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final List<String> testIdsInOrder = view.getChildren()
                .flatMap(this::streamWithDescendants)
                .map(component -> component.getElement().getAttribute("data-testid"))
                .filter(id -> id != null && !id.isBlank())
                .toList();

        assertThat(testIdsInOrder.indexOf("command-filter-refresh"))
                .isLessThan(testIdsInOrder.indexOf("command-filter-baseline-timestamp"));
        assertThat(testIdsInOrder.indexOf("command-filter-baseline-timestamp"))
                .isLessThan(testIdsInOrder.indexOf("command-filter-baseline-clear"));
    }

    @Test
    void refreshButtonReloadsCommandRowsFromCatalogService() {
        final SqlServerCommandCatalogService commandCatalogService = Mockito.mock(SqlServerCommandCatalogService.class);
        when(commandCatalogService.discoverCommandCatalog()).thenReturn(
                List.of(
                        new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "OK", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                        new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)),
                List.of(
                        new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "OK", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                        new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", null, false),
                        new CommandCatalogEntry("44444444-4444-4444-4444-444444444444", "order.Order#submit", "order.Order:900", "OK", "FOREGROUND", "2026-04-05T12:00:00.000", null, false)));

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogService,
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Button refreshButton = (Button) findByTestId(view, "command-filter-refresh").orElseThrow();
        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();

        assertThat(commandGrid.getListDataView().getItems().toList())
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");

        refreshButton.click();

        assertThat(commandGrid.getListDataView().getItems().toList())
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333", "44444444-4444-4444-4444-444444444444");
        assertThat(view.selectedCommandInteractionIdsForStageOne()).containsExactly("44444444-4444-4444-4444-444444444444");
        assertThat(focusedCommandInteractionId(view)).isEqualTo("44444444-4444-4444-4444-444444444444");
        verify(commandCatalogService, Mockito.times(2)).discoverCommandCatalog();
    }

    @Test
    void refreshAutoSelectsNewestOkCommandAndRefreshesProgressStatus() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                commandDrivenSelectionServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeToggleFocusedCommandSelection(view);
        assertThat(view.selectedCommandInteractionIdsForStageOne()).containsExactly("11111111-1111-1111-1111-111111111111");
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Supplier"));

        invokeShowComparisonProgress(view, "Comparison complete.", "comparison-progress-summary-success");
        invokeOnComparisonProgress(view, new ComparisonProgressEvent(
                new TableRef("dbo", "Supplier"),
                ComparisonProgressPhase.TABLE_COMPLETED,
                1,
                1,
                "Compared dbo.Supplier"));

        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        final Span counter = (Span) findByTestId(view, "compare-progress-counter").orElseThrow();
        final Button refreshButton = (Button) findByTestId(view, "command-filter-refresh").orElseThrow();

        assertThat(progress.getText()).isEqualTo("Compared dbo.Supplier (1/1)");
        assertThat(counter.getText()).isEqualTo("1 of 1");

        refreshButton.click();

        assertThat(view.selectedCommandInteractionIdsForStageOne()).containsExactly("11111111-1111-1111-1111-111111111111");
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Supplier"));
        assertThat(focusedCommandInteractionId(view)).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(progress.getText()).isBlank();
        assertThat(counter.getText()).isBlank();
    }

    @Test
    void refreshLeavesSelectionEmptyWhenNoOkCommandExists() {
        final SqlServerCommandCatalogService commandCatalogService = Mockito.mock(SqlServerCommandCatalogService.class);
        when(commandCatalogService.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "PENDING", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "FAILED", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)));

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogService,
                commandDrivenSelectionServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeToggleFocusedCommandSelection(view);
        assertThat(view.selectedCommandInteractionIdsForStageOne()).isNotEmpty();

        final Button refreshButton = (Button) findByTestId(view, "command-filter-refresh").orElseThrow();
        refreshButton.click();

        assertThat(view.selectedCommandInteractionIdsForStageOne()).isEmpty();
        assertThat(view.selectedTablesForStageTwo()).isEmpty();
        assertThat(focusedCommandInteractionId(view)).isNull();
    }

    @Test
    void baselinePickerFiltersCommandsAndClearsBackToFullList() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final DateTimePicker baseline = (DateTimePicker) findByTestId(view, "command-filter-baseline-timestamp").orElseThrow();
        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();

        baseline.setValue(LocalDateTime.parse("2026-04-05T10:00:00.000"));

        final List<?> filtered = commandGrid.getListDataView().getItems().toList();
        assertThat(filtered)
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");

        baseline.clear();

        final List<?> restored = commandGrid.getListDataView().getItems().toList();
        assertThat(restored)
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");
    }

    @Test
    void baselineClearButtonClearsPickerValueAndRestoresRows() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final DateTimePicker baseline = (DateTimePicker) findByTestId(view, "command-filter-baseline-timestamp").orElseThrow();
        final Button clearBaseline = (Button) findByTestId(view, "command-filter-baseline-clear").orElseThrow();
        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();

        baseline.setValue(LocalDateTime.parse("2026-04-05T10:00:00.000"));
        assertThat(commandGrid.getListDataView().getItems().toList())
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");

        clearBaseline.click();

        assertThat(baseline.getValue()).isNull();
        assertThat(commandGrid.getListDataView().getItems().toList())
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");
    }

    @Test
    void contextMenuBaselineActionUsesSelectedCommandTimestamp() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeSetCommandBaselineFilter(view, "2026-04-05T10:00:00.000");

        final DateTimePicker baseline = (DateTimePicker) findByTestId(view, "command-filter-baseline-timestamp").orElseThrow();
        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();

        assertThat(baseline.getValue()).isEqualTo(LocalDateTime.parse("2026-04-05T10:00:00.000"));
        assertThat(commandGrid.getListDataView().getItems().toList())
                .extracting(item -> ((CommandCatalogEntry) item).interactionId())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");
    }

    @Test
    void selectedOnlyCheckboxDefaultsToCheckedAndCanRevealAllRows() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Checkbox selectedOnly = (Checkbox) findByTestId(view, "selected-only-checkbox").orElseThrow();
        final Grid<?> tableGrid = (Grid<?>) findByTestId(view, "table-selection-grid").orElseThrow();

        assertThat(selectedOnly.getValue()).isTrue();
        assertThat(tableGrid.getDataProvider().size(new com.vaadin.flow.data.provider.Query<>())).isZero();

        selectedOnly.setValue(false);

        assertThat(tableGrid.getDataProvider().size(new com.vaadin.flow.data.provider.Query<>())).isEqualTo(3);
    }

    @Test
    void commandSelectionImmediatelyShowsProgrammaticBusinessRowsWhileSelectedOnlyChecked() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                commandDrivenSelectionServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Checkbox selectedOnly = (Checkbox) findByTestId(view, "selected-only-checkbox").orElseThrow();
        final Grid<?> tableGrid = (Grid<?>) findByTestId(view, "table-selection-grid").orElseThrow();

        assertThat(selectedOnly.getValue()).isTrue();
        assertThat(tableGrid.getDataProvider().size(new com.vaadin.flow.data.provider.Query<>())).isZero();

        invokeToggleFocusedCommandSelection(view);

        assertThat(tableGrid.getDataProvider().size(new com.vaadin.flow.data.provider.Query<>())).isEqualTo(1);
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Supplier"));
        assertThat(selectedOnly.getValue()).isTrue();
    }

    @Test
    void clearSelectionsRestoresSelectedOnlyDefaultChecked() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithPreselectedEntries(),
                commandDrivenSelectionServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Checkbox selectedOnly = (Checkbox) findByTestId(view, "selected-only-checkbox").orElseThrow();
        selectedOnly.setValue(false);
        assertThat(selectedOnly.getValue()).isFalse();

        invokeClearAllSelections(view);

        assertThat(selectedOnly.getValue()).isTrue();
    }

    @Test
    void exposesSelectedCommandInteractionIdsForStageOne() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithPreselectedEntries(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(view.selectedCommandInteractionIdsForStageOne())
                .containsExactly("11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222");
    }

    @Test
    void preselectedCommandsCanDriveInitialBusinessTableSelection() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithPreselectedEntries(),
                commandDrivenSelectionServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(view.selectedTablesForStageTwo())
                .containsExactly(new TableRef("dbo", "Supplier"), new TableRef("dbo", "Product"));

        final Button clearButton = (Button) findByTestId(view, "clear-selections-button").orElseThrow();
        assertThat(clearButton.isEnabled()).isTrue();
    }

    @Test
    void clearActionClearsCommandAndBusinessSelectionsAndDisablesItself() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithPreselectedEntries(),
                commandDrivenSelectionServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(view.selectedCommandInteractionIdsForStageOne()).isNotEmpty();
        assertThat(view.selectedTablesForStageTwo()).isNotEmpty();

        invokeClearAllSelections(view);

        assertThat(view.selectedCommandInteractionIdsForStageOne()).isEmpty();
        assertThat(view.selectedTablesForStageTwo()).isEmpty();

        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        final Button clearButton = (Button) findByTestId(view, "clear-selections-button").orElseThrow();
        assertThat(compareButton.isEnabled()).isFalse();
        assertThat(clearButton.isEnabled()).isFalse();
    }

    @Test
    void rendersFooterConnectionDetailsWithoutPassword() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());
        final Footer footer = (Footer) findByTestId(view, "connection-details-footer").orElseThrow();

        final String footerText = textOf(footer);

        assertThat(footerText).contains("left_db", "right_db");
        assertThat(footerText).doesNotContain("localhost:1433", "jdbc:sqlserver");
        assertThat(footerText).doesNotContain("super-secret-password", "SQL connectivity status");
    }

    @Test
    void comparisonStageUsesFullHeightFlexChainForResultContent() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        final Component mainContent = findByTestId(view, "main-content").orElseThrow();
        final Component comparisonStage = findByTestId(view, "comparison-stage-placeholder").orElseThrow();
        final Component resultsContainer = findByTestId(view, "comparison-results-container").orElseThrow();

        assertThat(mainContent.getElement().getStyle().get("padding-bottom")).isEqualTo("5rem");
        assertThat(comparisonStage.getElement().getStyle().get("flex")).isEqualTo("1 1 auto");
        assertThat(comparisonStage.getElement().getStyle().get("min-height")).isEqualTo("0");
        assertThat(resultsContainer.getElement().getStyle().get("flex")).isEqualTo("1 1 auto");
        assertThat(resultsContainer.getElement().getStyle().get("min-height")).isEqualTo("0");
    }

    @Test
    void executesCompareAndRendersResultTabs() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        assertThat(compareButton.isEnabled()).isTrue();

        invokeExecuteComparison(view);

        verify(comparisonExecutionService).compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class));
        assertThat(findByTestId(view, "comparison-result-actions")).isPresent();
        assertThat(findByTestId(view, "comparison-table-filter")).isPresent();
        assertThat(findByTestId(view, "download-format-select")).isPresent();
        assertThat(findByTestId(view, "download-action")).isPresent();
        assertThat(findByTestId(view, "comparison-stage-error")).isPresent();
        assertThat(findByTestId(view, "comparison-stage-state")).isEmpty();
        final Select<?> formatSelect = (Select<?>) findByTestId(view, "download-format-select").orElseThrow();
        assertThat(formatSelect.getValue().toString().toLowerCase()).contains("json");
        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        assertThat(progress.getText()).isNotBlank();
    }

    @Test
    void executeComparisonAppliesSuccessProgressStyle() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeShowComparisonProgress(view, "Comparison complete.", "comparison-progress-summary-success");

        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        assertThat(progress.getText()).isEqualTo("Comparison complete.");
        assertThat(progress.getElement().getAttribute("class")).contains("comparison-progress-summary-success");
    }

    @Test
    void executeComparisonAppliesFailureProgressStyleWhenComparisonThrows() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenThrow(new RuntimeException("boom"));

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        assertThat(progress.getText()).isEqualTo("Comparison failed.");
        assertThat(progress.getElement().getAttribute("class")).contains("comparison-progress-summary-failure");
    }

    @Test
    void completedProgressEventUpdatesCounterAndKeepsRowMarkedUntilReset() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeOnComparisonProgress(view, new ComparisonProgressEvent(
                new TableRef("dbo", "Supplier"),
                ComparisonProgressPhase.TABLE_COMPLETED,
                1,
                2,
                "Compared dbo.Supplier"));

        final Span counter = (Span) findByTestId(view, "compare-progress-counter").orElseThrow();
        assertThat(counter.isVisible()).isTrue();
        assertThat(counter.getText()).isEqualTo("1 of 2");

        final Grid<TableCatalogEntry> tableGrid = castGrid(findByTestId(view, "table-selection-grid").orElseThrow());
        final TableCatalogEntry supplier = tableGrid.getListDataView().getItems()
                .filter(entry -> entry.table().equals(new TableRef("dbo", "Supplier")))
                .findFirst()
                .orElseThrow();
        assertThat(tableGrid.getPartNameGenerator().apply(supplier)).contains("comparison-completed-row");
    }

    @Test
    void clearActionClearsAnyExistingComparisonProgressSummary() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeShowComparisonProgress(view, "Comparison complete.", "comparison-progress-summary-success");
        invokeOnComparisonProgress(view, new ComparisonProgressEvent(
                new TableRef("dbo", "Supplier"),
                ComparisonProgressPhase.TABLE_COMPLETED,
                1,
                1,
                "Compared dbo.Supplier"));
        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        final Span counter = (Span) findByTestId(view, "compare-progress-counter").orElseThrow();
        assertThat(progress.getText()).isEqualTo("Compared dbo.Supplier (1/1)");
        assertThat(counter.getText()).isEqualTo("1 of 1");

        invokeClearAllSelections(view);

        assertThat(progress.getText()).isBlank();
        assertThat(counter.getText()).isBlank();
        assertThat(counter.isVisible()).isFalse();
        final String cssClass = progress.getElement().getAttribute("class");
        assertThat(cssClass == null ? "" : cssClass)
                .doesNotContain("comparison-progress-summary-success", "comparison-progress-summary-failure", "comparison-progress-summary-neutral");
    }

    @Test
    void changingCommandOrTableFiltersClearsAnyExistingComparisonProgressSummary() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeShowComparisonProgress(view, "Comparison complete.", "comparison-progress-summary-success");
        invokeOnComparisonProgress(view, new ComparisonProgressEvent(
                new TableRef("dbo", "Supplier"),
                ComparisonProgressPhase.TABLE_COMPLETED,
                1,
                1,
                "Compared dbo.Supplier"));
        final Span progress = (Span) findByTestId(view, "comparison-progress-summary").orElseThrow();
        final Span counter = (Span) findByTestId(view, "compare-progress-counter").orElseThrow();
        assertThat(progress.getText()).isEqualTo("Compared dbo.Supplier (1/1)");
        assertThat(counter.getText()).isEqualTo("1 of 1");

        invokeToggleFocusedCommandSelection(view);
        assertThat(progress.getText()).isBlank();
        assertThat(counter.getText()).isBlank();

        invokeShowComparisonProgress(view, "Comparison complete.", "comparison-progress-summary-success");
        assertThat(progress.getText()).isEqualTo("Comparison complete.");

        final TextField tableFilter = (TextField) findByTestId(view, "table-filter-table").orElseThrow();
        tableFilter.setValue("Supplier");
        assertThat(progress.getText()).isBlank();

        invokeShowComparisonProgress(view, "Comparison complete.", "comparison-progress-summary-success");
        final DateTimePicker baselineFilter = (DateTimePicker) findByTestId(view, "command-filter-baseline-timestamp").orElseThrow();
        baselineFilter.setValue(LocalDateTime.parse("2026-04-05T10:00:00.000"));
        assertThat(progress.getText()).isBlank();
    }

    @Test
    void differencesOnlyFilterUsesDifferenceStatusForTabFiltering() {
        final WebappComparisonExecutionService.ComparisonExecutionOutcome outcome = sampleComparisonOutcome();
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(outcome);

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        final com.vaadin.flow.component.checkbox.Checkbox differencesOnly =
                (com.vaadin.flow.component.checkbox.Checkbox) findByTestId(view, "comparison-differences-only-filter").orElseThrow();
        final com.vaadin.flow.component.checkbox.Checkbox diffColumnsOnly =
                (com.vaadin.flow.component.checkbox.Checkbox) findByTestId(view, "comparison-diff-columns-only-filter").orElseThrow();
        assertThat(differencesOnly.getLabel()).isEqualTo("Diff tables only");
        assertThat(differencesOnly.getValue()).isFalse();
        assertThat(diffColumnsOnly.getLabel()).isEqualTo("Diff columns only");

        final TableComparisonViewResult supplier = outcome.viewResult().tableResults().get(0);
        final TableComparisonViewResult customerAddress = outcome.viewResult().tableResults().get(1);

        assertThat(invokeHasDifferences(view, supplier)).isTrue();
        assertThat(invokeHasDifferences(view, customerAddress)).isFalse();

        assertThat(invokeMatchesComparedTableFilter(view, supplier)).isTrue();
        assertThat(invokeMatchesComparedTableFilter(view, customerAddress)).isTrue();

        differencesOnly.setValue(true);
        assertThat(invokeMatchesComparedTableFilter(view, supplier)).isTrue();
        assertThat(invokeMatchesComparedTableFilter(view, customerAddress)).isFalse();
    }

    @Test
    void diffColumnsOnlyFilterDisablesForUnchangedSelectedTab() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        final com.vaadin.flow.component.checkbox.Checkbox diffColumnsOnly =
                (com.vaadin.flow.component.checkbox.Checkbox) findByTestId(view, "comparison-diff-columns-only-filter").orElseThrow();

        final TableComparisonViewResult supplier = sampleComparisonOutcome().viewResult().tableResults().get(0);
        final TableComparisonViewResult customerAddress = sampleComparisonOutcome().viewResult().tableResults().get(1);
        invokeUpdateDiffColumnsOnlyFilterState(view, supplier);
        assertThat(diffColumnsOnly.isEnabled()).isTrue();
        invokeUpdateDiffColumnsOnlyFilterState(view, customerAddress);
        assertThat(diffColumnsOnly.isEnabled()).isFalse();
    }

    @Test
    void reloadTableCatalogAfterLoginFromUnauthenticatedStateDoesNotThrow() {
        final AuthenticatedConnectionContextHolder holder = unauthenticatedHolder();
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                holder,
                authenticationServiceWithDefaults());

        holder.set(new AuthenticatedConnectionContext("jdbc:sqlserver://localhost:1433", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "super-secret-password", "left_db", "right_db"));

        assertThatCode(() -> invokeOnAuthenticationSuccess(view)).doesNotThrowAnyException();
        final Component commandGrid = findByTestId(view, "command-selection-grid").orElseThrow();
        assertThat(commandGrid.getElement().getAttribute("data-focused")).isEqualTo("true");
    }

    @Test
    void commandGridIncludesDeterministicFocusTargetHook() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Component commandGrid = findByTestId(view, "command-selection-grid").orElseThrow();
        assertThat(commandGrid.getElement().getAttribute("data-testid-focus-target")).isEqualTo("command-selection-grid");
        assertThat(commandGrid.getElement().getAttribute("tabindex")).isEqualTo("0");
    }

    @Test
    void toggleFocusedCommandSelectionUsesExistingStatePath() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(view.selectedCommandInteractionIdsForStageOne()).isEmpty();
        invokeToggleFocusedCommandSelection(view);
        assertThat(view.selectedCommandInteractionIdsForStageOne()).containsExactly("11111111-1111-1111-1111-111111111111");
        invokeToggleFocusedCommandSelection(view);
        assertThat(view.selectedCommandInteractionIdsForStageOne()).isEmpty();
    }

    @Test
    void shiftRangeSelectionSelectsInclusiveVisibleRowsFromAnchor() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeToggleFocusedCommandSelection(view);
        setFocusedCommandInteractionId(view, "33333333-3333-3333-3333-333333333333");
        invokeToggleFocusedCommandSelectionWithShift(view, true);

        assertThat(view.selectedCommandInteractionIdsForStageOne())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");
    }

    @Test
    void shiftSpaceWithoutAnchorSelectsFocusedRowAndCreatesAnchor() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        setFocusedCommandInteractionId(view, "33333333-3333-3333-3333-333333333333");
        invokeToggleFocusedCommandSelectionWithShift(view, true);
        assertThat(view.selectedCommandInteractionIdsForStageOne())
                .containsExactly("33333333-3333-3333-3333-333333333333");

        setFocusedCommandInteractionId(view, "11111111-1111-1111-1111-111111111111");
        invokeToggleFocusedCommandSelectionWithShift(view, true);
        assertThat(view.selectedCommandInteractionIdsForStageOne())
                .containsExactly("11111111-1111-1111-1111-111111111111", "33333333-3333-3333-3333-333333333333");
    }

    @Test
    void applyingCommandFiltersClearsRangeAnchorWhenAnchorIsNotVisible() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeToggleFocusedCommandSelection(view);
        assertThat(commandRangeAnchorInteractionId(view)).isEqualTo("11111111-1111-1111-1111-111111111111");

        setCommandMemberFilterValue(view, "product.Product");
        invokeApplyCommandFilters(view);

        assertThat(commandRangeAnchorInteractionId(view)).isNull();
    }

    @Test
    void shiftRangeSelectionTriggersSingleCommandDrivenResolution() {
        final CommandDrivenTableSelectionService commandDrivenSelectionService = mock(CommandDrivenTableSelectionService.class);
        when(commandDrivenSelectionService.resolveTouchedBusinessTables(Mockito.any(), Mockito.anyList()))
                .thenReturn(Set.of());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                commandDrivenSelectionService,
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeToggleFocusedCommandSelection(view);
        Mockito.clearInvocations(commandDrivenSelectionService);

        invokeApplyCommandRangeSelection(view, "33333333-3333-3333-3333-333333333333");

        verify(commandDrivenSelectionService, Mockito.times(1))
                .resolveTouchedBusinessTables(Mockito.any(), Mockito.anyList());
    }

    @Test
    void toggleFocusedBusinessTableSelectionUsesExistingStatePath() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(view.selectedTablesForStageTwo()).isEmpty();
        invokeToggleFocusedBusinessTableSelection(view);
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Supplier"));
        invokeToggleFocusedBusinessTableSelection(view);
        assertThat(view.selectedTablesForStageTwo()).isEmpty();
    }

    @Test
    void toggleFocusedBusinessTableSelectionSkipsIneligibleRows() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        setFocusedBusinessTable(view, new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"));
        invokeToggleFocusedBusinessTableSelection(view);
        assertThat(view.selectedTablesForStageTwo()).isEmpty();
    }

    @Test
    void togglingAcrossFocusedBusinessRowsKeepsNavigationSelectionStable() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        setFocusedBusinessTable(view, new TableRef("dbo", "Supplier"));
        invokeToggleFocusedBusinessTableSelection(view);
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Supplier"));

        setFocusedBusinessTable(view, new TableRef("dbo", "Product"));
        invokeToggleFocusedBusinessTableSelection(view);
        assertThat(view.selectedTablesForStageTwo()).containsExactlyInAnyOrder(
                new TableRef("dbo", "Supplier"),
                new TableRef("dbo", "Product"));

        setFocusedBusinessTable(view, new TableRef("dbo", "Supplier"));
        invokeToggleFocusedBusinessTableSelection(view);
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Product"));
    }

    @Test
    void hidesMatchRowsByDefaultAndOmitsMatchRowsToggle() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        assertThat(findByTestId(view, "comparison-show-match-filter")).isEmpty();

        final ColumnRef name = new ColumnRef("name");
        final ComparisonRowView matchRow = new ComparisonRowView(
                new RowKey(List.of("SUP-001")),
                ComparisonRowStatus.MATCH,
                Map.of(name, "Supplier A"),
                Map.of(name, "Supplier A"),
                List.of());
        final ComparisonRowView diffRow = new ComparisonRowView(
                new RowKey(List.of("SUP-002")),
                ComparisonRowStatus.DIFFERENT,
                Map.of(name, "Supplier A"),
                Map.of(name, "Supplier B"),
                List.of(name));
        final TableComparisonViewResult supplier = new TableComparisonViewResult(
                new TableRef("dbo", "Supplier"),
                List.of(name),
                List.of(matchRow, diffRow));

        final Grid<?> grid = extractGrid(invokeBuildResultGrid(view, supplier));
        final int visibleRows = grid.getDataProvider().size(new com.vaadin.flow.data.provider.Query<>());
        assertThat(visibleRows).isEqualTo(1);
    }

    @Test
    void resultGridColumnsAreSortable() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final Grid<?> grid = extractGrid(invokeBuildResultGrid(view, sampleComparisonOutcome().viewResult().tableResults().get(0)));
        assertThat(grid.getColumns().stream().allMatch(Grid.Column::isSortable)).isTrue();
    }

    @Test
    void diffColumnsOnlyFilterCanBeEnabledAndAppliedWithoutError() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        final ColumnRef name = new ColumnRef("name");
        final ColumnRef city = new ColumnRef("city");
        final ComparisonRowView row = new ComparisonRowView(
                new RowKey(List.of("SUP-001")),
                ComparisonRowStatus.DIFFERENT,
                Map.of(name, "Supplier A", city, "London"),
                Map.of(name, "Supplier A (R)", city, "London"),
                List.of(name));
        final TableComparisonViewResult supplier = new TableComparisonViewResult(
                new TableRef("dbo", "Supplier"),
                List.of(name, city),
                List.of(row));

        final com.vaadin.flow.component.checkbox.Checkbox diffColumnsOnly =
                (com.vaadin.flow.component.checkbox.Checkbox) findByTestId(view, "comparison-diff-columns-only-filter").orElseThrow();
        setSelectedComparisonTable(view, supplier.table());
        invokeUpdateDiffColumnsOnlyFilterState(view, supplier);
        diffColumnsOnly.setValue(true);

        final Grid<?> grid = extractGrid(invokeBuildResultGrid(view, supplier));
        assertThat(grid.getColumns().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void rendersSingleComparedColumnWhenLeftAndRightValuesAreEqual() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final ColumnRef name = new ColumnRef("name");
        final ComparisonRowView row = new ComparisonRowView(
                new RowKey(List.of("SUP-001")),
                ComparisonRowStatus.MATCH,
                Map.of(name, "Supplier A"),
                Map.of(name, "Supplier A"),
                List.of());
        final TableComparisonViewResult supplier = new TableComparisonViewResult(
                new TableRef("dbo", "Supplier"),
                List.of(name),
                List.of(row));

        final Component gridContainer = invokeBuildResultGrid(view, supplier);
        final Grid<?> grid = extractGrid(gridContainer);
        assertThat(grid.getColumns()).hasSize(3);
    }

    @Test
    void compareRequestUsesCurrentSelectedTables() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedProduct(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        invokeExecuteComparison(view);

        final ArgumentCaptor<MultiTableComparisonRequest> captor = ArgumentCaptor.forClass(MultiTableComparisonRequest.class);
        verify(comparisonExecutionService).compare(captor.capture(), Mockito.any(com.danhaywood.cfct.service.ComparisonProgressListener.class));
        assertThat(captor.getValue().tables()).containsExactly(new TableRef("dbo", "Product"));
    }

    @Test
    void buildsScrollableGridContainer() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final ColumnRef name = new ColumnRef("name");
        final ComparisonRowView row = new ComparisonRowView(
                new RowKey(List.of("SUP-001")),
                ComparisonRowStatus.MATCH,
                Map.of(name, "Supplier A"),
                Map.of(name, "Supplier A"),
                List.of());
        final TableComparisonViewResult supplier = new TableComparisonViewResult(
                new TableRef("dbo", "Supplier"),
                List.of(name),
                List.of(row));

        final Component gridContainer = invokeBuildResultGrid(view, supplier);
        assertThat(gridContainer.getElement().getAttribute("class")).contains("cmp-grid-scroll-container");
        assertThat(extractGrid(gridContainer).getElement().getAttribute("data-testid")).isEqualTo("comparison-grid-dbo-supplier");
    }

    @Test
    void omitsDirectionalPrefixesForOnlyInSideRows() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        assertThat(invokeCompactValue(view, ComparisonRowStatus.ONLY_IN_LEFT, "Left-only supplier", ""))
                .isEqualTo("Left-only supplier");
        assertThat(invokeCompactValue(view, ComparisonRowStatus.ONLY_IN_RIGHT, "", "Right-only supplier"))
                .isEqualTo("Right-only supplier");
    }

    @Test
    void appliesDiffCellClassForDifferingValues() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final ColumnRef status = new ColumnRef("status");
        final ComparisonRowView row = new ComparisonRowView(
                new RowKey(List.of("SUP-002")),
                ComparisonRowStatus.DIFFERENT,
                Map.of(status, "ACTIVE"),
                Map.of(status, "SUSPENDED"),
                List.of(status));

        final Component cell = invokeValueCell(view, row, status);
        final String cssClasses = cell.getElement().getOuterHTML();
        assertThat(cssClasses).contains("cmp-cell-diff");
    }

    @Test
    void appliesMissingSideCellClassesForSideOnlyRows() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                authenticationServiceWithDefaults());

        final ColumnRef name = new ColumnRef("name");
        final ComparisonRowView leftOnly = new ComparisonRowView(
                new RowKey(List.of("SUP-003")),
                ComparisonRowStatus.ONLY_IN_LEFT,
                Map.of(name, "Supplier Left"),
                Map.of(),
                List.of());
        final ComparisonRowView rightOnly = new ComparisonRowView(
                new RowKey(List.of("SUP-004")),
                ComparisonRowStatus.ONLY_IN_RIGHT,
                Map.of(),
                Map.of(name, "Supplier Right"),
                List.of());

        final Component leftCell = invokeValueCell(view, leftOnly, name);
        final Component rightCell = invokeValueCell(view, rightOnly, name);

        assertThat(leftCell.getElement().getAttribute("class")).contains("cmp-cell-left-only");
        assertThat(rightCell.getElement().getAttribute("class")).contains("cmp-cell-right-only");
    }

    private AuthenticatedConnectionContextHolder authenticatedHolder() {
        final AuthenticatedConnectionContextHolder holder = new AuthenticatedConnectionContextHolder();
        holder.set(new AuthenticatedConnectionContext("jdbc:sqlserver://localhost:1433", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "super-secret-password", "left_db", "right_db"));
        return holder;
    }

    private AuthenticatedConnectionContextHolder unauthenticatedHolder() {
        return new AuthenticatedConnectionContextHolder();
    }

    private Optional<Component> findByTestId(final Component root, final String testId) {
        if (testId.equals(root.getElement().getAttribute("data-testid"))) {
            return Optional.of(root);
        }
        return root.getChildren()
                .flatMap(this::streamWithDescendants)
                .filter(component -> testId.equals(component.getElement().getAttribute("data-testid")))
                .findFirst();
    }

    private List<Component> findByTestIdPrefix(final Component root, final String testIdPrefix) {
        return root.getChildren()
                .flatMap(this::streamWithDescendants)
                .filter(component -> {
                    final String testId = component.getElement().getAttribute("data-testid");
                    return testId != null && testId.startsWith(testIdPrefix);
                })
                .toList();
    }

    private Stream<Component> streamWithDescendants(final Component component) {
        return Stream.concat(Stream.of(component), component.getChildren().flatMap(this::streamWithDescendants));
    }

    private String textOf(final Component component) {
        final String ownText = component instanceof HasText hasText ? hasText.getText() : "";
        return Stream.concat(Stream.of(ownText), component.getChildren().map(this::textOf))
                .filter(text -> text != null && !text.isBlank())
                .reduce("", (left, right) -> left + " " + right);
    }

    private void invokeOnAuthenticationSuccess(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("onAuthenticationSuccess");
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Component invokeBuildResultGrid(final MainView view, final TableComparisonViewResult tableResult) {
        try {
            final var method = MainView.class.getDeclaredMethod("buildResultGrid", TableComparisonViewResult.class);
            method.setAccessible(true);
            return (Component) method.invoke(view, tableResult);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Grid<?> extractGrid(final Component gridContainer) {
        return (Grid<?>) gridContainer.getChildren().findFirst().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private Grid<TableCatalogEntry> castGrid(final Component component) {
        return (Grid<TableCatalogEntry>) component;
    }

    private String invokeCompactValue(final MainView view, final ComparisonRowStatus status, final String left, final String right) {
        try {
            final var method = MainView.class.getDeclaredMethod("compactValue", ComparisonRowStatus.class, String.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(view, status, left, right);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Component invokeValueCell(final MainView view, final ComparisonRowView row, final ColumnRef column) {
        try {
            final var method = MainView.class.getDeclaredMethod("valueCell", ComparisonRowView.class, ColumnRef.class);
            method.setAccessible(true);
            return (Component) method.invoke(view, row, column);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean invokeHasDifferences(final MainView view, final TableComparisonViewResult tableResult) {
        try {
            final var method = MainView.class.getDeclaredMethod("hasDifferences", TableComparisonViewResult.class);
            method.setAccessible(true);
            return (boolean) method.invoke(view, tableResult);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean invokeMatchesComparedTableFilter(final MainView view, final TableComparisonViewResult tableResult) {
        try {
            final var method = MainView.class.getDeclaredMethod("matchesComparedTableFilter", TableComparisonViewResult.class);
            method.setAccessible(true);
            return (boolean) method.invoke(view, tableResult);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeUpdateDiffColumnsOnlyFilterState(final MainView view, final TableComparisonViewResult tableResult) {
        try {
            final var method = MainView.class.getDeclaredMethod("updateDiffColumnsOnlyFilterState", TableComparisonViewResult.class);
            method.setAccessible(true);
            method.invoke(view, tableResult);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeHandleEnterShortcut(final MainView view, final String targetTagName, final String targetType) {
        try {
            final var method = MainView.class.getDeclaredMethod("handleEnterShortcut", String.class, String.class);
            method.setAccessible(true);
            method.invoke(view, targetTagName, targetType);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeHandleRefreshShortcut(
            final MainView view,
            final String key,
            final boolean altKey,
            final String targetTagName,
            final String targetType) {
        try {
            final var method = MainView.class.getDeclaredMethod("handleRefreshShortcut", String.class, boolean.class, String.class, String.class);
            method.setAccessible(true);
            method.invoke(view, key, altKey, targetTagName, targetType);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeToggleFocusedCommandSelection(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("toggleFocusedCommandSelection");
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeToggleFocusedCommandSelectionWithShift(final MainView view, final boolean shiftPressed) {
        try {
            final var method = MainView.class.getDeclaredMethod("toggleFocusedCommandSelection", boolean.class);
            method.setAccessible(true);
            method.invoke(view, shiftPressed);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeApplyCommandRangeSelection(final MainView view, final String interactionId) {
        try {
            final var method = MainView.class.getDeclaredMethod("applyCommandRangeSelection", String.class);
            method.setAccessible(true);
            method.invoke(view, interactionId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeToggleFocusedBusinessTableSelection(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("toggleFocusedBusinessTableSelection");
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setFocusedBusinessTable(final MainView view, final TableRef tableRef) {
        try {
            final var field = MainView.class.getDeclaredField("focusedBusinessTable");
            field.setAccessible(true);
            field.set(view, tableRef);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setSelectedComparisonTable(final MainView view, final TableRef tableRef) {
        try {
            final var field = MainView.class.getDeclaredField("selectedComparisonTable");
            field.setAccessible(true);
            field.set(view, tableRef);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setFocusedCommandInteractionId(final MainView view, final String interactionId) {
        try {
            final var field = MainView.class.getDeclaredField("focusedCommandInteractionId");
            field.setAccessible(true);
            field.set(view, interactionId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String focusedCommandInteractionId(final MainView view) {
        try {
            final var field = MainView.class.getDeclaredField("focusedCommandInteractionId");
            field.setAccessible(true);
            return (String) field.get(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String commandRangeAnchorInteractionId(final MainView view) {
        try {
            final var field = MainView.class.getDeclaredField("commandRangeAnchorInteractionId");
            field.setAccessible(true);
            return (String) field.get(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeExecuteComparison(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("executeComparison");
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeOnComparisonProgress(final MainView view, final ComparisonProgressEvent event) {
        try {
            final var method = MainView.class.getDeclaredMethod("onComparisonProgress", ComparisonProgressEvent.class);
            method.setAccessible(true);
            method.invoke(view, event);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeShowComparisonProgress(final MainView view, final String message, final String styleClass) {
        try {
            final var method = MainView.class.getDeclaredMethod("showComparisonProgress", String.class, String.class);
            method.setAccessible(true);
            method.invoke(view, message, styleClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeClearAllSelections(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("clearAllSelections");
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String invokeDisplayReplayState(final MainView view, final CommandCatalogEntry entry) {
        try {
            final var method = MainView.class.getDeclaredMethod("displayReplayState", CommandCatalogEntry.class);
            method.setAccessible(true);
            return (String) method.invoke(view, entry);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeSetCommandBaselineFilter(final MainView view, final String timestamp) {
        try {
            final var method = MainView.class.getDeclaredMethod("setCommandBaselineFilter", String.class);
            method.setAccessible(true);
            method.invoke(view, timestamp);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invokeApplyCommandFilters(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("applyCommandFilters");
            method.setAccessible(true);
            method.invoke(view);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setCommandMemberFilterValue(final MainView view, final String filterValue) {
        try {
            final var field = MainView.class.getDeclaredField("commandMemberFilterValue");
            field.setAccessible(true);
            field.set(view, filterValue);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private SqlServerTableCatalogService catalogServiceWithDefaults() {
        final SqlServerTableCatalogService service = Mockito.mock(SqlServerTableCatalogService.class);
        when(service.discoverTableCatalog()).thenReturn(List.of(
                TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                TableCatalogEntry.eligible(new TableRef("dbo", "Product")),
                TableCatalogEntry.ineligible(new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"), "No unique index ending with _PK.")));
        return service;
    }

    private SqlServerTableCatalogService catalogServiceWithPreselectedSupplier() {
        final SqlServerTableCatalogService service = Mockito.mock(SqlServerTableCatalogService.class);
        when(service.discoverTableCatalog()).thenReturn(List.of(
                new TableCatalogEntry(new TableRef("dbo", "Supplier"), true, null, true),
                TableCatalogEntry.eligible(new TableRef("dbo", "Product")),
                TableCatalogEntry.ineligible(new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"), "No unique index ending with _PK.")));
        return service;
    }

    private SqlServerTableCatalogService catalogServiceWithPreselectedProduct() {
        final SqlServerTableCatalogService service = Mockito.mock(SqlServerTableCatalogService.class);
        when(service.discoverTableCatalog()).thenReturn(List.of(
                TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                new TableCatalogEntry(new TableRef("dbo", "Product"), true, null, true),
                TableCatalogEntry.ineligible(new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"), "No unique index ending with _PK.")));
        return service;
    }

    private SqlServerCommandCatalogService commandCatalogServiceWithDefaults() {
        final SqlServerCommandCatalogService service = Mockito.mock(SqlServerCommandCatalogService.class);
        when(service.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "OK", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)));
        return service;
    }

    private SqlServerCommandCatalogService commandCatalogServiceWithReverseTimestamps() {
        final SqlServerCommandCatalogService service = Mockito.mock(SqlServerCommandCatalogService.class);
        when(service.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "FAILED", "FOREGROUND", "2026-04-05T11:00:00.000", null, false),
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "OK", "FOREGROUND", "2026-04-05T10:00:00.000", null, false)));
        return service;
    }

    private SqlServerCommandCatalogService commandCatalogServiceWithBackgroundEntries() {
        final SqlServerCommandCatalogService service = Mockito.mock(SqlServerCommandCatalogService.class);
        when(service.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "UNDEFINED", "BACKGROUND", "2026-04-05T10:00:00.000", null, false),
                new CommandCatalogEntry("22222222-2222-2222-2222-222222222222", "supplier.Supplier#updateName", "supplier.Supplier:302", "UNDEFINED", "BACKGROUND", "2026-04-05T10:30:00.000", "2026-04-05T10:31:00.000", false),
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "OK", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)));
        return service;
    }

    private SqlServerCommandCatalogService commandCatalogServiceWithPreselectedEntries() {
        final SqlServerCommandCatalogService service = Mockito.mock(SqlServerCommandCatalogService.class);
        when(service.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "EXPORTED", "FOREGROUND", "2026-04-05T10:00:00.000", null, true),
                new CommandCatalogEntry("22222222-2222-2222-2222-222222222222", "supplier.Supplier#updateName", "supplier.Supplier:302", "EXPORTED", "FOREGROUND", "2026-04-05T10:30:00.000", null, true),
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)));
        return service;
    }

    private CommandDrivenTableSelectionService commandDrivenSelectionServiceWithDefaults() {
        final CommandDrivenTableSelectionService service = mock(CommandDrivenTableSelectionService.class);
        when(service.resolveTouchedBusinessTables(Mockito.any(), Mockito.anyList()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    final List<String> interactionIds = (List<String>) invocation.getArgument(0);
                    if (interactionIds == null || interactionIds.isEmpty()) {
                        return Set.of();
                    }
                    if (interactionIds.contains("11111111-1111-1111-1111-111111111111")
                            && interactionIds.contains("22222222-2222-2222-2222-222222222222")) {
                        return Set.of(new TableRef("dbo", "Supplier"), new TableRef("dbo", "Product"));
                    }
                    if (interactionIds.contains("11111111-1111-1111-1111-111111111111")) {
                        return Set.of(new TableRef("dbo", "Supplier"));
                    }
                    if (interactionIds.contains("22222222-2222-2222-2222-222222222222")) {
                        return Set.of(new TableRef("dbo", "Product"));
                    }
                    return Set.of();
                });
        return service;
    }

    private WebappComparisonProperties propertiesWithDefaults() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.getConnection().setLeftDatabase("left_db");
        properties.getConnection().setRightDatabase("right_db");
        return properties;
    }

    private WebappAuthenticationService authenticationServiceWithDefaults() {
        final WebappAuthenticationService service = mock(WebappAuthenticationService.class);
        when(service.loginDefaults()).thenReturn(new ConnectionLoginRequest(
                "jdbc:sqlserver://localhost:1433;encrypt=false;trustServerCertificate=true",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "sa",
                "super-secret-password",
                "left_db",
                "right_db"));
        return service;
    }

    private WebappComparisonExecutionService.ComparisonExecutionOutcome sampleComparisonOutcome() {
        final ColumnRef name = new ColumnRef("name");
        final ComparisonRowView supplierDifferent = new ComparisonRowView(
                new RowKey(List.of("SUP-001")),
                ComparisonRowStatus.DIFFERENT,
                Map.of(name, "Supplier A"),
                Map.of(name, "Supplier A (R)"),
                List.of(name));
        final TableComparisonViewResult supplier = new TableComparisonViewResult(
                new TableRef("dbo", "Supplier"),
                List.of(name),
                List.of(supplierDifferent));

        final ComparisonRowView customerAddressMatch = new ComparisonRowView(
                new RowKey(List.of("ADDR-001")),
                ComparisonRowStatus.MATCH,
                Map.of(name, "10 High Street"),
                Map.of(name, "10 High Street"),
                List.of());
        final TableComparisonViewResult customerAddress = new TableComparisonViewResult(
                new TableRef("dbo", "CustomerAddress"),
                List.of(name),
                List.of(customerAddressMatch));

        final MultiTableComparisonViewResult viewResult = new MultiTableComparisonViewResult(List.of(supplier, customerAddress));
        final MultiTableComparisonResult rawResult = new MultiTableComparisonResult(List.of());
        return new WebappComparisonExecutionService.ComparisonExecutionOutcome(rawResult, viewResult, "{}", "a: b\n", new byte[]{1, 2});
    }
}
