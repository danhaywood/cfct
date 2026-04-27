package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.webapp.selection.ManualTableSelectionState;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatus;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

@Route("")
public class MainView extends VerticalLayout {

    private final ManualTableSelectionState selectionState;

    public MainView(
            final ConnectionValidationStatusHolder statusHolder,
            final SqlServerTableCatalogService tableCatalogService) {
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog();
        this.selectionState = new ManualTableSelectionState(tableCatalog);

        setSizeFull();
        add(new H2("sqlcomparer webapp scaffold"));
        add(renderConnectionStatus(statusHolder.current()));
        add(buildStageLayout(tableCatalog));
    }

    public List<TableRef> selectedTablesForStageTwo() {
        return selectionState.selectedTables();
    }

    private HorizontalLayout buildStageLayout(final List<TableCatalogEntry> tableCatalog) {
        final HorizontalLayout stageLayout = new HorizontalLayout();
        stageLayout.setWidthFull();
        stageLayout.setSpacing(true);
        stageLayout.setAlignItems(Alignment.START);

        final Div leftPanel = buildSelectionPanel(tableCatalog);
        leftPanel.setWidth("25%");
        final Div rightPanel = buildComparisonPlaceholder();
        rightPanel.setWidth("75%");

        stageLayout.add(leftPanel, rightPanel);
        return stageLayout;
    }

    private Div buildSelectionPanel(final List<TableCatalogEntry> tableCatalog) {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "table-selection-panel");

        final H3 heading = new H3("Select tables");
        final Span selectedFeedback = new Span(selectionState.feedbackText());
        selectedFeedback.getElement().setAttribute("data-testid", "selected-table-feedback");

        final VerticalLayout tableRows = new VerticalLayout();
        tableRows.setPadding(false);
        tableRows.setSpacing(false);
        tableRows.getElement().setAttribute("data-testid", "table-selection-grid");

        for (TableCatalogEntry entry : tableCatalog) {
            final String token = selectorToken(entry.table());
            final HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setAlignItems(Alignment.CENTER);
            row.getElement().setAttribute("data-testid", "table-row-" + token);

            final Checkbox checkbox = new Checkbox();
            checkbox.setEnabled(entry.eligible());
            checkbox.getElement().setAttribute("data-testid", "table-checkbox-" + token);
            checkbox.addValueChangeListener(event -> {
                selectionState.updateSelection(entry.table(), event.getValue());
                selectedFeedback.setText(selectionState.feedbackText());
            });

            final Span tableLabel = new Span(entry.table().displayName());
            tableLabel.getElement().setAttribute("data-testid", "table-label-" + token);
            row.add(checkbox, tableLabel);

            if (!entry.eligible()) {
                row.getStyle().set("color", "#888888");
                if (entry.eligibilityReason() != null && !entry.eligibilityReason().isBlank()) {
                    row.getElement().setAttribute("title", entry.eligibilityReason());
                }
            }
            tableRows.add(row);
        }

        panel.add(heading, selectedFeedback, tableRows);
        return panel;
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
