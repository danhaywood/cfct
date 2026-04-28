package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.ComparisonRowStatus;
import com.danhaywood.sqlcomparer.model.ComparisonRowView;
import com.danhaywood.sqlcomparer.model.TableComparisonViewResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.sqlcomparer.webapp.auth.WebappAuthenticationService;
import com.danhaywood.sqlcomparer.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;
import com.danhaywood.sqlcomparer.webapp.selection.ManualTableSelectionState;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatus;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Route("")
public class MainView extends AppLayout implements BeforeEnterObserver {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final ManualTableSelectionState selectionState;
    private final WebappComparisonExecutionService comparisonExecutionService;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;
    private final WebappAuthenticationService authenticationService;

    private final Button compareButton = new Button("Compare");
    private final Span comparisonError = new Span();
    private final Div comparisonResultsContainer = new Div();
    private final TextField comparedTableFilter = new TextField();
    private final Anchor downloadJson = new Anchor();
    private final Anchor downloadExcel = new Anchor();
    private final HorizontalLayout resultActions = new HorizontalLayout();

    private WebappComparisonExecutionService.ComparisonExecutionOutcome latestOutcome;

    public MainView(
            final ConnectionValidationStatusHolder statusHolder,
            final SqlServerTableCatalogService tableCatalogService,
            final WebappComparisonProperties properties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder,
            final WebappAuthenticationService authenticationService) {
        this.comparisonExecutionService = comparisonExecutionService;
        this.authenticatedContextHolder = authenticatedContextHolder;
        this.authenticationService = authenticationService;

        final List<TableCatalogEntry> tableCatalog = authenticatedContextHolder.isAuthenticated()
                ? tableCatalogService.discoverTableCatalog()
                : List.of();
        this.selectionState = new ManualTableSelectionState(tableCatalog);

        setPrimarySection(Section.DRAWER);
        getElement().setAttribute("data-testid", "main-app-layout");
        addToNavbar(buildDrawerToggle(), buildLogoutButton());
        addToDrawer(buildSelectionPanel(tableCatalog));
        setContent(buildMainContent(properties, statusHolder.current()));
        renderEmptyComparisonState();
    }

    public List<TableRef> selectedTablesForStageTwo() {
        return selectionState.selectedTables();
    }

    private DrawerToggle buildDrawerToggle() {
        final DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Open navigation menu");
        toggle.getElement().setAttribute("data-testid", "hamburger-menu");
        toggle.getElement().setAttribute("title", "Open navigation menu");
        return toggle;
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (!authenticatedContextHolder.isAuthenticated()) {
            event.rerouteTo(LoginView.class);
        }
    }

    private VerticalLayout buildMainContent(final WebappComparisonProperties properties, final ConnectionValidationStatus status) {
        final VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.getStyle()
                .set("gap", "var(--lumo-space-l)")
                .set("padding-bottom", "5rem");
        content.getElement().setAttribute("data-testid", "main-content");
        content.add(buildComparisonStage(), buildFooter(properties, status));
        return content;
    }

    private Component buildComparisonStage() {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "comparison-stage-placeholder");
        panel.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-m)");

        comparedTableFilter.setPlaceholder("Filter compared tables");
        comparedTableFilter.setClearButtonVisible(true);
        comparedTableFilter.setValueChangeMode(ValueChangeMode.EAGER);
        comparedTableFilter.getElement().setAttribute("data-testid", "comparison-table-filter");
        comparedTableFilter.setWidth("24rem");
        comparedTableFilter.addValueChangeListener(event -> renderComparisonTabs());

        downloadJson.getElement().setAttribute("data-testid", "download-json");
        downloadExcel.getElement().setAttribute("data-testid", "download-excel");
        downloadJson.add(new Button("Download JSON"));
        downloadExcel.add(new Button("Download Excel"));
        downloadJson.getElement().setAttribute("download", true);
        downloadExcel.getElement().setAttribute("download", true);

        resultActions.setWidthFull();
        resultActions.setSpacing(true);
        resultActions.getElement().setAttribute("data-testid", "comparison-result-actions");
        resultActions.add(comparedTableFilter, downloadJson, downloadExcel);
        resultActions.setVisible(false);

        comparisonError.getElement().setAttribute("data-testid", "comparison-stage-error");
        comparisonError.getStyle().set("color", "var(--lumo-error-text-color)");

        comparisonResultsContainer.getElement().setAttribute("data-testid", "comparison-results-container");

        panel.add(resultActions, comparisonError, comparisonResultsContainer);
        return panel;
    }

    private Button buildLogoutButton() {
        final Button logoutButton = new Button("Logout");
        logoutButton.getElement().setAttribute("data-testid", "logout-button");
        logoutButton.addClickListener(event -> {
            authenticationService.logout();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        return logoutButton;
    }

    private Footer buildFooter(final WebappComparisonProperties properties, final ConnectionValidationStatus status) {
        final Footer footer = new Footer();
        footer.getElement().setAttribute("data-testid", "connection-details-footer");
        footer.getStyle()
                .set("position", "fixed")
                .set("left", "0")
                .set("right", "0")
                .set("bottom", "0")
                .set("z-index", "100")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-xl)")
                .set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                .set("background", "var(--lumo-base-color)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        final AuthenticatedConnectionContext context = authenticatedContextHolder.current().orElseGet(() -> {
            final WebappComparisonProperties.Connection configured = properties.getConnection();
            return new AuthenticatedConnectionContext(
                    configured.getServer(),
                    configured.getUsername(),
                    configured.getPassword(),
                    configured.getLeftDatabase(),
                    configured.getRightDatabase());
        });
        final Div connectionDetails = new Div();
        connectionDetails.getElement().setAttribute("data-testid", "connection-details-inline");
        connectionDetails.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)");

        final Span server = new Span(safe(context.server()));
        server.getElement().setAttribute("data-testid", "connection-server");
        final Span databases = new Span(safe(context.leftDatabase()) + " ↔ " + safe(context.rightDatabase()));
        databases.getElement().setAttribute("data-testid", "connection-database-pair");
        connectionDetails.add(server, databases);

        final Div statusPanel = renderConnectionStatus(status);
        statusPanel.getStyle().set("margin-left", "auto");

        footer.add(connectionDetails, statusPanel);
        return footer;
    }

    private String safe(final String value) {
        return value == null || value.isBlank() ? "not configured" : value;
    }

    private Div buildSelectionPanel(final List<TableCatalogEntry> tableCatalog) {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "table-selection-panel");
        panel.getStyle()
                .set("width", "32rem")
                .set("max-width", "100%")
                .set("padding", "var(--lumo-space-m)");

        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.getStyle().set("gap", "var(--lumo-space-m)");

        final ListDataProvider<TableCatalogEntry> dataProvider = new ListDataProvider<>(tableCatalog);
        final TextField tableFilter = filterField("Filter table", "table-filter-table", dataProvider);
        tableFilter.setWidthFull();
        final Grid<TableCatalogEntry> tableGrid = buildSelectionGrid(dataProvider);

        final Div actionBar = new Div(compareButton);
        actionBar.getElement().setAttribute("data-testid", "navigation-compare-action-bar");
        actionBar.getStyle()
                .set("display", "flex")
                .set("justify-content", "flex-end")
                .set("width", "100%")
                .set("margin-top", "var(--lumo-space-s)")
                .set("margin-bottom", "var(--lumo-space-xs)");

        compareButton.setEnabled(selectionState.isCompareEnabled());
        compareButton.getElement().setAttribute("data-testid", "compare-button");
        compareButton.getElement().setAttribute("title", "Execute comparison for selected eligible tables.");
        compareButton.addClickListener(event -> executeComparison());

        layout.add(actionBar, tableFilter, tableGrid);
        panel.add(layout);
        return panel;
    }

    private Grid<TableCatalogEntry> buildSelectionGrid(final ListDataProvider<TableCatalogEntry> dataProvider) {
        final Grid<TableCatalogEntry> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "table-selection-grid");
        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        grid.setPartNameGenerator(entry -> entry.eligible() ? "eligible-table-row" : "ineligible-table-row");
        grid.setDataProvider(dataProvider);

        grid.addColumn(new ComponentRenderer<>(entry -> {
            final Checkbox checkbox = new Checkbox();
            checkbox.setEnabled(entry.eligible());
            checkbox.setValue(selectionState.isSelected(entry.table()));
            checkbox.getElement().setAttribute("data-testid", "table-checkbox-" + selectorToken(entry.table()));
            if (!entry.eligible()) {
                checkbox.getElement().setAttribute("disabled", true);
                if (entry.eligibilityReason() != null && !entry.eligibilityReason().isBlank()) {
                    checkbox.getElement().setAttribute("title", entry.eligibilityReason());
                }
            }
            checkbox.addValueChangeListener(event -> {
                selectionState.updateSelection(entry.table(), event.getValue());
                compareButton.setEnabled(selectionState.isCompareEnabled());
            });
            return checkbox;
        })).setHeader("").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(entry -> entry.table().schemaName())
                .setHeader("Schema")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(entry -> entry.table().schemaName(), String.CASE_INSENSITIVE_ORDER))
                .setKey("schema");
        grid.addColumn(new ComponentRenderer<>(entry -> {
                    final Span tableName = new Span(entry.table().tableName());
                    tableName.getElement().setAttribute("data-testid", "table-label-" + selectorToken(entry.table()));
                    if (!entry.eligible() && entry.eligibilityReason() != null && !entry.eligibilityReason().isBlank()) {
                        tableName.getElement().setAttribute("title", entry.eligibilityReason());
                    }
                    return tableName;
                }))
                .setHeader("Table")
                .setSortable(true)
                .setComparator(Comparator.comparing(entry -> entry.table().tableName(), String.CASE_INSENSITIVE_ORDER))
                .setKey("table");

        return grid;
    }

    private TextField filterField(
            final String placeholder,
            final String testId,
            final ListDataProvider<TableCatalogEntry> dataProvider) {
        final TextField filter = new TextField();
        filter.setPlaceholder(placeholder);
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.getElement().setAttribute("data-testid", testId);
        filter.addValueChangeListener(event -> applyFilter(dataProvider, event.getValue()));
        return filter;
    }

    private void applyFilter(final ListDataProvider<TableCatalogEntry> dataProvider, final String value) {
        dataProvider.clearFilters();
        if (value != null && !value.isBlank()) {
            dataProvider.addFilter(entry -> selectionState.matchesFilter(entry, value));
        }
    }

    private void executeComparison() {
        final List<TableRef> selectedTables = selectionState.selectedTables();
        if (selectedTables.isEmpty()) {
            return;
        }

        compareButton.setEnabled(false);
        comparisonError.setText("");

        try {
            latestOutcome = comparisonExecutionService.compare(MultiTableComparisonRequest.forTables(selectedTables));
            resultActions.setVisible(true);
            refreshDownloadLinks();
            renderComparisonTabs();
        } catch (RuntimeException ex) {
            latestOutcome = null;
            resultActions.setVisible(false);
            comparisonResultsContainer.removeAll();
            comparisonError.setText(safeError(ex));
            renderEmptyComparisonState();
        } finally {
            compareButton.setEnabled(selectionState.isCompareEnabled());
        }
    }

    private void refreshDownloadLinks() {
        final String ts = LocalDateTime.now().format(FILE_TS);
        downloadJson.setHref(new StreamResource("comparison-" + ts + ".json",
                () -> new ByteArrayInputStream(latestOutcome.json().getBytes(StandardCharsets.UTF_8))));
        downloadExcel.setHref(new StreamResource("comparison-" + ts + ".xlsx",
                () -> new ByteArrayInputStream(latestOutcome.excel())));
    }

    private void renderComparisonTabs() {
        comparisonResultsContainer.removeAll();
        if (latestOutcome == null || latestOutcome.viewResult().tableResults().isEmpty()) {
            renderEmptyComparisonState();
            return;
        }

        final List<TableComparisonViewResult> filtered = latestOutcome.viewResult().tableResults().stream()
                .filter(this::matchesComparedTableFilter)
                .toList();

        if (filtered.isEmpty()) {
            comparisonResultsContainer.add(new Span("No compared tables match the filter."));
            return;
        }

        final Tabs tabs = new Tabs();
        tabs.getElement().setAttribute("data-testid", "comparison-results-tabs");
        tabs.setWidthFull();

        final Div tabContent = new Div();
        tabContent.getElement().setAttribute("data-testid", "comparison-results-tab-content");

        final Map<Tab, TableComparisonViewResult> mapping = new LinkedHashMap<>();
        for (TableComparisonViewResult tableResult : filtered) {
            final Tab tab = new Tab(tableResult.tableDisplayName());
            tab.getElement().setAttribute("data-testid", "comparison-result-tab-" + selectorToken(tableResult.table()));
            mapping.put(tab, tableResult);
            tabs.add(tab);
        }

        tabs.addSelectedChangeListener(event -> {
            final TableComparisonViewResult selected = mapping.get(event.getSelectedTab());
            if (selected != null) {
                tabContent.removeAll();
                tabContent.add(buildResultGrid(selected));
            }
        });

        final TableComparisonViewResult first = mapping.get(tabs.getSelectedTab());
        if (first != null) {
            tabContent.add(buildResultGrid(first));
        }

        comparisonResultsContainer.add(tabs, tabContent);
    }

    private boolean matchesComparedTableFilter(final TableComparisonViewResult result) {
        final String filter = comparedTableFilter.getValue();
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return result.tableDisplayName().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private void renderEmptyComparisonState() {
        comparisonResultsContainer.removeAll();
        final Span empty = new Span("No comparison results yet.");
        empty.getElement().setAttribute("data-testid", "comparison-stage-empty");
        comparisonResultsContainer.add(empty);
    }

    private Component buildResultGrid(final TableComparisonViewResult tableResult) {
        final Grid<ComparisonRowView> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "comparison-grid-" + selectorToken(tableResult.table()));
        grid.setItems(tableResult.rows());
        grid.setWidthFull();
        grid.setAllRowsVisible(true);
        grid.addThemeNames("column-borders", "compact");

        grid.addColumn(row -> row.key().display())
                .setHeader("Business Key")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::statusBadge))
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);

        for (ColumnRef column : tableResult.comparedColumns()) {
            final String columnName = column.name();
            grid.addColumn(row -> row.leftValues().getOrDefault(column, ""))
                    .setHeader("L: " + columnName)
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.START);
            grid.addColumn(row -> row.rightValues().getOrDefault(column, ""))
                    .setHeader("R: " + columnName)
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.START);
        }

        return grid;
    }

    private Component statusBadge(final ComparisonRowView row) {
        final Span badge = new Span(row.status().name());
        badge.getStyle()
                .set("padding", "0.15rem 0.45rem")
                .set("border-radius", "999px")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("display", "inline-block")
                .set("border", "1px solid transparent");

        if (row.status() == ComparisonRowStatus.DIFFERENT) {
            badge.getStyle().set("background", "#fde68a").set("border-color", "#f59e0b").set("color", "#78350f");
        } else if (row.status() == ComparisonRowStatus.ONLY_IN_LEFT) {
            badge.getStyle().set("background", "#dbeafe").set("border-color", "#3b82f6").set("color", "#1e3a8a");
        } else if (row.status() == ComparisonRowStatus.ONLY_IN_RIGHT) {
            badge.getStyle().set("background", "#dcfce7").set("border-color", "#22c55e").set("color", "#14532d");
        } else {
            badge.getStyle().set("background", "#f3f4f6").set("border-color", "#d1d5db").set("color", "#374151");
        }
        return badge;
    }

    private String safeError(final RuntimeException ex) {
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Comparison execution failed.";
        }
        return ex.getMessage();
    }

    private String selectorToken(final TableRef tableRef) {
        return (tableRef.schemaName() + "-" + tableRef.tableName()).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }

    private Div renderConnectionStatus(final ConnectionValidationStatus status) {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "connection-status-panel");
        panel.getStyle()
                .set("display", "inline-flex")
                .set("justify-content", "flex-end")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)")
                .set("padding-left", "var(--lumo-space-m)");

        final Span state = new Span("Status: " + status.state());
        state.getElement().setAttribute("data-testid", "connection-status-state");
        state.getStyle().set("font-weight", "600");
        panel.add(state);

        if (status.state() == ConnectionValidationState.FAILED && status.summary() != null && !status.summary().isBlank()) {
            final Span summary = new Span(status.summary());
            summary.getElement().setAttribute("data-testid", "connection-status-summary");
            panel.add(summary);
        }

        return panel;
    }
}
