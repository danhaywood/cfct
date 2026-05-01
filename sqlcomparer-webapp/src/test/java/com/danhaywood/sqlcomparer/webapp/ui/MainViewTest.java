package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.ComparisonRowStatus;
import com.danhaywood.sqlcomparer.model.ComparisonRowView;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonViewResult;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonViewResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.sqlcomparer.webapp.auth.WebappAuthenticationService;
import com.danhaywood.sqlcomparer.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;
import com.danhaywood.sqlcomparer.webapp.selection.CommandCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerCommandCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                mock(WebappAuthenticationService.class));

        final BeforeEnterEvent event = mock(BeforeEnterEvent.class);
        view.beforeEnter(event);

        final Component accountMenu = findByTestId(view, "account-menu").orElseThrow();
        assertThat(accountMenu.isVisible()).isFalse();
        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        assertThat(compareButton.isEnabled()).isFalse();
    }

    @Test
    void rendersAppLayoutShellAndOkStatusInFooter() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(
                holder,
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                mock(WebappAuthenticationService.class));

        assertThat(view.getElement().getAttribute("data-testid")).isEqualTo("main-app-layout");
        assertThat(findByTestId(view, "hamburger-menu")).isPresent();
        assertThat(findByTestId(view, "account-menu")).isPresent();
        assertThat(findByTestId(view, "logout-button")).isEmpty();

        final Span status = (Span) findByTestId(view, "connection-status-state").orElseThrow();
        assertThat(status.getText()).contains(ConnectionValidationState.OK.name());
        final Span summary = (Span) findByTestId(view, "connection-status-summary").orElseThrow();
        assertThat(summary.isVisible()).isFalse();
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
                mock(WebappAuthenticationService.class));

        final Component actionBar = findByTestId(view, "navigation-compare-action-bar").orElseThrow();
        assertThat(actionBar.getElement().getAttribute("class")).contains("navigation-compare-action-bar");
    }

    @Test
    void rendersCommandGridAboveTableGridWithSpacer() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                mock(WebappAuthenticationService.class));

        assertThat(findByTestId(view, "command-selection-spacer")).isPresent();
        assertThat(findByTestId(view, "command-selection-grid")).isPresent();
        assertThat(findByTestId(view, "table-selection-grid")).isPresent();

        final List<String> testIdsInOrder = view.getChildren()
                .flatMap(this::streamWithDescendants)
                .map(component -> component.getElement().getAttribute("data-testid"))
                .filter(id -> id != null && !id.isBlank())
                .toList();

        assertThat(testIdsInOrder.indexOf("command-selection-grid"))
                .isLessThan(testIdsInOrder.indexOf("table-selection-grid"));
    }

    @Test
    void commandGridDisplaysTimestampMemberInteractionColumnsInOrder() {
        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithDefaults(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                mock(WebappComparisonExecutionService.class),
                authenticatedHolder(),
                mock(WebappAuthenticationService.class));

        final Grid<?> commandGrid = (Grid<?>) findByTestId(view, "command-selection-grid").orElseThrow();
        final List<String> headers = commandGrid.getColumns().stream().map(Grid.Column::getHeaderText).toList();
        assertThat(headers).containsExactly("", "Timestamp", "Member", "Interaction");
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
                mock(WebappAuthenticationService.class));

        assertThat(view.selectedCommandInteractionIdsForStageOne())
                .containsExactly("11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222");
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
                mock(WebappAuthenticationService.class));
        final Footer footer = (Footer) findByTestId(view, "connection-details-footer").orElseThrow();

        final String footerText = textOf(footer);

        assertThat(footerText).contains("localhost:1433", "left_db", "right_db", "Status: OK");
        assertThat(footerText).doesNotContain("super-secret-password", "SQL connectivity status");
    }

    @Test
    void executesCompareAndRendersResultTabs() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedSupplier(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                mock(WebappAuthenticationService.class));

        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        assertThat(compareButton.isEnabled()).isTrue();

        invokeExecuteComparison(view);

        verify(comparisonExecutionService).compare(Mockito.any(MultiTableComparisonRequest.class));
        assertThat(findByTestId(view, "comparison-result-export-actions")).isPresent();
        assertThat(findByTestId(view, "comparison-result-actions")).isPresent();
        assertThat(findByTestId(view, "comparison-table-filter")).isPresent();
        assertThat(findByTestId(view, "download-json")).isPresent();
        assertThat(findByTestId(view, "download-excel")).isPresent();
        assertThat(findByTestId(view, "comparison-stage-error")).isPresent();
        assertThat(findByTestId(view, "comparison-stage-state")).isEmpty();
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
                mock(WebappAuthenticationService.class));

        holder.set(new AuthenticatedConnectionContext("localhost:1433", "sa", "super-secret-password", "left_db", "right_db"));

        assertThatCode(() -> invokeOnAuthenticationSuccess(view)).doesNotThrowAnyException();
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
                mock(WebappAuthenticationService.class));

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
        final List<String> headers = grid.getColumns().stream()
                .map(Grid.Column::getHeaderText)
                .toList();
        assertThat(headers).contains("name");
        assertThat(headers).doesNotContain("L: name", "R: name");
    }

    @Test
    void compareRequestUsesCurrentSelectedTables() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class)))
                .thenReturn(sampleComparisonOutcome());

        final MainView view = new MainView(
                new ConnectionValidationStatusHolder(),
                catalogServiceWithPreselectedProduct(),
                commandCatalogServiceWithDefaults(),
                propertiesWithDefaults(),
                comparisonExecutionService,
                authenticatedHolder(),
                mock(WebappAuthenticationService.class));

        invokeExecuteComparison(view);

        final ArgumentCaptor<MultiTableComparisonRequest> captor = ArgumentCaptor.forClass(MultiTableComparisonRequest.class);
        verify(comparisonExecutionService).compare(captor.capture());
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
                mock(WebappAuthenticationService.class));

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
                mock(WebappAuthenticationService.class));

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
                mock(WebappAuthenticationService.class));

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
                mock(WebappAuthenticationService.class));

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
        holder.set(new AuthenticatedConnectionContext("localhost:1433", "sa", "super-secret-password", "left_db", "right_db"));
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

    private void invokeExecuteComparison(final MainView view) {
        try {
            final var method = MainView.class.getDeclaredMethod("executeComparison");
            method.setAccessible(true);
            method.invoke(view);
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
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "EXPORTED", "FOREGROUND", "2026-04-05T10:00:00.000", false),
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", false)));
        return service;
    }

    private SqlServerCommandCatalogService commandCatalogServiceWithPreselectedEntries() {
        final SqlServerCommandCatalogService service = Mockito.mock(SqlServerCommandCatalogService.class);
        when(service.discoverCommandCatalog()).thenReturn(List.of(
                new CommandCatalogEntry("11111111-1111-1111-1111-111111111111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "EXPORTED", "FOREGROUND", "2026-04-05T10:00:00.000", true),
                new CommandCatalogEntry("22222222-2222-2222-2222-222222222222", "supplier.Supplier#updateName", "supplier.Supplier:302", "EXPORTED", "FOREGROUND", "2026-04-05T10:30:00.000", true),
                new CommandCatalogEntry("33333333-3333-3333-3333-333333333333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", false)));
        return service;
    }

    private WebappComparisonProperties propertiesWithDefaults() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.getConnection().setServer("localhost:1433");
        properties.getConnection().setUsername("sa");
        properties.getConnection().setPassword("super-secret-password");
        properties.getConnection().setLeftDatabase("left_db");
        properties.getConnection().setRightDatabase("right_db");
        return properties;
    }

    private WebappComparisonExecutionService.ComparisonExecutionOutcome sampleComparisonOutcome() {
        final ColumnRef name = new ColumnRef("name");
        final ComparisonRowView row = new ComparisonRowView(
                new RowKey(List.of("SUP-001")),
                ComparisonRowStatus.DIFFERENT,
                Map.of(name, "Supplier A"),
                Map.of(name, "Supplier A (R)"),
                List.of(name));
        final TableComparisonViewResult supplier = new TableComparisonViewResult(
                new TableRef("dbo", "Supplier"),
                List.of(name),
                List.of(row));
        final MultiTableComparisonViewResult viewResult = new MultiTableComparisonViewResult(List.of(supplier));
        final MultiTableComparisonResult rawResult = new MultiTableComparisonResult(List.of());
        return new WebappComparisonExecutionService.ComparisonExecutionOutcome(rawResult, viewResult, "{}", new byte[]{1, 2});
    }
}
