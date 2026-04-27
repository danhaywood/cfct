package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MainViewTest {

    @Test
    void rendersOkStatusWithoutFailureSummary() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(holder, catalogServiceWithDefaults());
        final List<Component> topLevel = view.getChildren().toList();
        final Div panel = (Div) topLevel.get(1);

        final Span status = panel.getChildren()
                .filter(component -> "connection-status-state".equals(component.getElement().getAttribute("data-testid")))
                .map(component -> (Span) component)
                .findFirst()
                .orElseThrow();

        assertThat(status.getText()).contains(ConnectionValidationState.OK.name());
        assertThat(panel.getChildren().anyMatch(component -> component instanceof Paragraph)).isFalse();
    }

    @Test
    void rendersFailedStatusWithFailureSummary() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markFailed("Configured database does not exist: missing_db");

        final MainView view = new MainView(holder, catalogServiceWithDefaults());
        final Div panel = (Div) view.getChildren().toList().get(1);

        final List<String> texts = panel.getChildren()
                .filter(component -> component instanceof HasText)
                .map(component -> ((HasText) component).getText())
                .toList();

        assertThat(texts).anyMatch(text -> text.contains(ConnectionValidationState.FAILED.name()));
        assertThat(texts).anyMatch(text -> text.contains("missing_db"));
    }

    @Test
    void updatesSelectionFeedbackAndStageOneOutputWithoutAutoRun() {
        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithDefaults());
        final HorizontalLayout stages = (HorizontalLayout) view.getChildren().toList().get(2);
        final Div leftPanel = (Div) stages.getChildren().toList().get(0);

        final Span feedback = leftPanel.getChildren()
                .filter(component -> "selected-table-feedback".equals(component.getElement().getAttribute("data-testid")))
                .map(component -> (Span) component)
                .findFirst()
                .orElseThrow();

        final VerticalLayoutRows rows = new VerticalLayoutRows(leftPanel);
        final Checkbox eligibleCheckbox = rows.findCheckbox("table-checkbox-dbo-supplier");
        final Checkbox ineligibleCheckbox = rows.findCheckbox("table-checkbox-dbo-purchaseorderwithoutbusinesskey");

        assertThat(ineligibleCheckbox.isEnabled()).isFalse();
        eligibleCheckbox.setValue(true);

        assertThat(feedback.getText()).isEqualTo("Selected tables: 1");
        assertThat(view.selectedTablesForStageTwo()).containsExactly(new TableRef("dbo", "Supplier"));

        final Div rightPanel = (Div) stages.getChildren().toList().get(1);
        assertThat(rightPanel.getElement().getAttribute("data-testid")).isEqualTo("comparison-stage-placeholder");
    }

    private SqlServerTableCatalogService catalogServiceWithDefaults() {
        final SqlServerTableCatalogService service = Mockito.mock(SqlServerTableCatalogService.class);
        when(service.discoverTableCatalog()).thenReturn(List.of(
                TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                TableCatalogEntry.ineligible(new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"), "No unique index ending with _BK.")));
        return service;
    }

    private record VerticalLayoutRows(Div leftPanel) {
        Checkbox findCheckbox(final String testId) {
            return leftPanel.getChildren()
                    .filter(component -> "table-selection-grid".equals(component.getElement().getAttribute("data-testid")))
                    .findFirst()
                    .orElseThrow()
                    .getChildren()
                    .flatMap(Component::getChildren)
                    .filter(component -> testId.equals(component.getElement().getAttribute("data-testid")))
                    .map(component -> (Checkbox) component)
                    .findFirst()
                    .orElseThrow();
        }
    }
}
