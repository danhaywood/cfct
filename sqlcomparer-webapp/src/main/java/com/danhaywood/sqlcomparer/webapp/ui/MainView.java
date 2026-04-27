package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;
import com.danhaywood.sqlcomparer.webapp.selection.ManualTableSelectionState;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatus;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Route("")
public class MainView extends AppLayout {

    private final ManualTableSelectionState selectionState;
    private final Button compareButton = new Button("Compare");
    private final Span collapsedNavigationIndicator = new Span("Hidden navigation items available");

    public MainView(
            final ConnectionValidationStatusHolder statusHolder,
            final SqlServerTableCatalogService tableCatalogService,
            final WebappComparisonProperties properties) {
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog();
        this.selectionState = new ManualTableSelectionState(tableCatalog);

        setPrimarySection(Section.DRAWER);
        getElement().setAttribute("data-testid", "main-app-layout");
        addToNavbar(buildDrawerToggle(), buildCollapsedNavigationIndicator(), buildTitle());
        addToDrawer(buildSelectionPanel(tableCatalog));
        setContent(buildMainContent(properties, statusHolder.current()));
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

    private Span buildCollapsedNavigationIndicator() {
        collapsedNavigationIndicator.getElement().setAttribute("data-testid", "navigation-collapsed-indicator");
        collapsedNavigationIndicator.getStyle()
                .set("margin-left", "var(--lumo-space-m)")
                .set("padding", "0.1rem 0.4rem")
                .set("border-radius", "var(--lumo-border-radius-s)")
                .set("background", "var(--lumo-primary-color-10pct)")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("display", "inline-flex");
        return collapsedNavigationIndicator;
    }

    private H2 buildTitle() {
        final H2 title = new H2("sqlcomparer");
        title.getElement().setAttribute("data-testid", "main-shell-header");
        title.getStyle()
                .set("margin", "0")
                .set("padding", "var(--lumo-space-m)");
        return title;
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
        content.add(buildComparisonPlaceholder(), buildFooter(properties, status));
        return content;
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
                .set("gap", "var(--lumo-space-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("background", "var(--lumo-base-color)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        final WebappComparisonProperties.Connection connection = properties.getConnection();
        final Div connectionDetails = new Div();
        connectionDetails.getElement().setAttribute("data-testid", "connection-details-inline");
        connectionDetails.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)");

        final Span server = new Span(safe(connection.getServer()));
        server.getElement().setAttribute("data-testid", "connection-server");
        final Span databases = new Span(safe(connection.getLeftDatabase()) + " ↔ " + safe(connection.getRightDatabase()));
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
                .set("width", "100%");

        compareButton.setEnabled(selectionState.isCompareEnabled());
        compareButton.getElement().setAttribute("data-testid", "compare-button");
        compareButton.getElement().setAttribute("title", "Comparison execution is not implemented yet.");

        layout.add(actionBar, tableFilter, tableGrid);
        panel.add(layout);
        return panel;
    }

    private Grid<TableCatalogEntry> buildSelectionGrid(
            final ListDataProvider<TableCatalogEntry> dataProvider) {
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

    private Div buildComparisonPlaceholder() {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "comparison-stage-placeholder");
        panel.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)");
        panel.add(new H3("Comparison stage"), new Paragraph("Results will be shown here after an explicit run action."));
        return panel;
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
                .set("gap", "var(--lumo-space-s)");

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
