package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;
import com.danhaywood.sqlcomparer.webapp.selection.ManualTableSelectionState;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatus;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
public class MainView extends VerticalLayout {

    private final ManualTableSelectionState selectionState;
    private final Button compareButton = new Button("Compare");

    public MainView(
            final ConnectionValidationStatusHolder statusHolder,
            final SqlServerTableCatalogService tableCatalogService,
            final WebappComparisonProperties properties) {
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog();
        this.selectionState = new ManualTableSelectionState(tableCatalog);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        add(buildHeader());
        add(buildMainContent(statusHolder.current(), tableCatalog));
        add(buildFooter(properties));
    }

    public List<TableRef> selectedTablesForStageTwo() {
        return selectionState.selectedTables();
    }

    private Div buildHeader() {
        final Div header = new Div();
        header.getElement().setAttribute("data-testid", "main-shell-header");
        header.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-m)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        final Button hamburger = new Button("☰");
        hamburger.setAriaLabel("Open navigation menu");
        hamburger.getElement().setAttribute("data-testid", "hamburger-menu");
        hamburger.getElement().setAttribute("title", "Open navigation menu");

        header.add(hamburger, new H2("sqlcomparer"));
        return header;
    }

    private VerticalLayout buildMainContent(final ConnectionValidationStatus status, final List<TableCatalogEntry> tableCatalog) {
        final VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.getElement().setAttribute("data-testid", "main-content");
        content.add(renderConnectionStatus(status));
        content.add(buildStageLayout(tableCatalog));
        return content;
    }

    private Footer buildFooter(final WebappComparisonProperties properties) {
        final Footer footer = new Footer();
        footer.getElement().setAttribute("data-testid", "connection-details-footer");
        footer.getStyle()
                .set("padding", "var(--lumo-space-m)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        final WebappComparisonProperties.Connection connection = properties.getConnection();
        footer.add(
                footerItem("connection-server", "Server", connection.getServer()),
                footerItem("connection-left-database", "Left database", connection.getLeftDatabase()),
                footerItem("connection-right-database", "Right database", connection.getRightDatabase()));
        return footer;
    }

    private Span footerItem(final String testId, final String label, final String value) {
        final Span item = new Span(label + ": " + safe(value));
        item.getElement().setAttribute("data-testid", testId);
        item.getStyle().set("margin-right", "var(--lumo-space-l)");
        return item;
    }

    private String safe(final String value) {
        return value == null || value.isBlank() ? "not configured" : value;
    }

    private HorizontalLayout buildStageLayout(final List<TableCatalogEntry> tableCatalog) {
        final HorizontalLayout stageLayout = new HorizontalLayout();
        stageLayout.setWidthFull();
        stageLayout.setSpacing(true);
        stageLayout.setAlignItems(Alignment.START);

        final Div leftPanel = buildSelectionPanel(tableCatalog);
        leftPanel.setWidth("35%");
        final Div rightPanel = buildComparisonPlaceholder();
        rightPanel.setWidth("65%");

        stageLayout.add(leftPanel, rightPanel);
        return stageLayout;
    }

    private Div buildSelectionPanel(final List<TableCatalogEntry> tableCatalog) {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "table-selection-panel");

        final H3 heading = new H3("Select tables");
        final Span selectedFeedback = new Span(selectionState.feedbackText());
        selectedFeedback.getElement().setAttribute("data-testid", "selected-table-feedback");

        compareButton.setEnabled(selectionState.isCompareEnabled());
        compareButton.getElement().setAttribute("data-testid", "compare-button");
        compareButton.getElement().setAttribute("title", "Comparison execution is not implemented yet.");

        final ListDataProvider<TableCatalogEntry> dataProvider = new ListDataProvider<>(tableCatalog);
        final TextField tableFilter = filterField("Filter table", "table-filter-table", dataProvider);
        final Button applyFilter = new Button("Apply filter", event -> applyFilter(dataProvider, tableFilter.getValue()));
        applyFilter.getElement().setAttribute("data-testid", "apply-table-filter");
        final Grid<TableCatalogEntry> tableGrid = buildSelectionGrid(dataProvider, selectedFeedback);
        panel.add(heading, selectedFeedback, compareButton, tableFilter, applyFilter, tableGrid);
        return panel;
    }

    private Grid<TableCatalogEntry> buildSelectionGrid(
            final ListDataProvider<TableCatalogEntry> dataProvider,
            final Span selectedFeedback) {
        final Grid<TableCatalogEntry> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "table-selection-grid");
        grid.setAllRowsVisible(true);
        grid.setPartNameGenerator(entry -> entry.eligible() ? "eligible-table-row" : "ineligible-table-row");

        grid.setDataProvider(dataProvider);

        final Grid.Column<TableCatalogEntry> selectionColumn = grid.addColumn(new ComponentRenderer<>(entry -> {
            final Checkbox checkbox = new Checkbox();
            checkbox.setEnabled(entry.eligible());
            checkbox.setValue(selectionState.isSelected(entry.table()));
            checkbox.getElement().setAttribute("data-testid", "table-checkbox-" + selectorToken(entry.table()));
            if (!entry.eligible() && entry.eligibilityReason() != null && !entry.eligibilityReason().isBlank()) {
                checkbox.getElement().setAttribute("title", entry.eligibilityReason());
            }
            checkbox.addValueChangeListener(event -> {
                selectionState.updateSelection(entry.table(), event.getValue());
                selectedFeedback.setText(selectionState.feedbackText());
                compareButton.setEnabled(selectionState.isCompareEnabled());
            });
            return checkbox;
        })).setHeader("Select").setAutoWidth(true).setFlexGrow(0);

        final Grid.Column<TableCatalogEntry> schemaColumn = grid.addColumn(entry -> entry.table().schemaName())
                .setHeader("Schema")
                .setSortable(true)
                .setComparator(Comparator.comparing(entry -> entry.table().schemaName(), String.CASE_INSENSITIVE_ORDER))
                .setKey("schema");
        final Grid.Column<TableCatalogEntry> tableColumn = grid.addColumn(entry -> entry.table().tableName())
                .setHeader("Table")
                .setSortable(true)
                .setComparator(Comparator.comparing(entry -> entry.table().tableName(), String.CASE_INSENSITIVE_ORDER))
                .setKey("table");
        grid.addColumn(entry -> entry.eligible() ? "Eligible" : "Ineligible")
                .setHeader("Eligibility")
                .setSortable(true)
                .setComparator(Comparator.comparing(TableCatalogEntry::eligible))
                .setKey("eligibility");

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
        panel.add(new H3("Comparison stage"), new Paragraph("Results will be shown here after an explicit run action."));
        return panel;
    }

    private String selectorToken(final TableRef tableRef) {
        return (tableRef.schemaName() + "-" + tableRef.tableName()).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }

    private Div renderConnectionStatus(final ConnectionValidationStatus status) {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "connection-status-panel");

        final H3 header = new H3("SQL connectivity status");
        final Span state = new Span("Status: " + status.state());
        state.getElement().setAttribute("data-testid", "connection-status-state");
        panel.add(header, state);

        if (status.state() == ConnectionValidationState.FAILED && status.summary() != null && !status.summary().isBlank()) {
            final Paragraph summary = new Paragraph(status.summary());
            summary.getElement().setAttribute("data-testid", "connection-status-summary");
            panel.add(summary);
        }

        return panel;
    }
}
