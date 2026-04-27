package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;
import com.danhaywood.sqlcomparer.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.sqlcomparer.webapp.selection.TableCatalogEntry;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MainViewTest {

    @Test
    void rendersMainShellAndOkStatusWithoutFailureSummary() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(holder, catalogServiceWithDefaults(), propertiesWithDefaults());
        final List<Component> topLevel = view.getChildren().toList();

        assertThat(topLevel.get(0).getElement().getAttribute("data-testid")).isEqualTo("main-shell-header");
        final Button hamburger = topLevel.get(0).getChildren()
                .filter(component -> "hamburger-menu".equals(component.getElement().getAttribute("data-testid")))
                .map(component -> (Button) component)
                .findFirst()
                .orElseThrow();
        assertThat(hamburger.getElement().getAttribute("aria-label")).isEqualTo("Open navigation menu");

        final VerticalLayout content = (VerticalLayout) topLevel.get(1);
        final Div panel = (Div) content.getChildren().toList().get(0);
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

        final MainView view = new MainView(holder, catalogServiceWithDefaults(), propertiesWithDefaults());
        final VerticalLayout content = (VerticalLayout) view.getChildren().toList().get(1);
        final Div panel = (Div) content.getChildren().toList().get(0);

        final List<String> texts = panel.getChildren()
                .filter(component -> component instanceof HasText)
                .map(component -> ((HasText) component).getText())
                .toList();

        assertThat(texts).anyMatch(text -> text.contains(ConnectionValidationState.FAILED.name()));
        assertThat(texts).anyMatch(text -> text.contains("missing_db"));
    }

    @Test
    void rendersFooterConnectionDetailsWithoutPassword() {
        final WebappComparisonProperties properties = propertiesWithDefaults();
        properties.getConnection().setPassword("super-secret-password");

        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithDefaults(), properties);
        final Footer footer = (Footer) view.getChildren().toList().get(2);

        final String footerText = footer.getChildren()
                .filter(component -> component instanceof HasText)
                .map(component -> ((HasText) component).getText())
                .reduce("", (left, right) -> left + " " + right);

        assertThat(footer.getElement().getAttribute("data-testid")).isEqualTo("connection-details-footer");
        assertThat(footerText).contains("localhost:1433", "left_db", "right_db");
        assertThat(footerText).doesNotContain("super-secret-password");
    }

    @Test
    void rendersSelectionPanelWithGridAndDisabledCompareButton() {
        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithDefaults(), propertiesWithDefaults());
        final VerticalLayout content = (VerticalLayout) view.getChildren().toList().get(1);
        final HorizontalLayout stages = (HorizontalLayout) content.getChildren().toList().get(1);
        final Div leftPanel = (Div) stages.getChildren().toList().get(0);

        final Span feedback = leftPanel.getChildren()
                .filter(component -> "selected-table-feedback".equals(component.getElement().getAttribute("data-testid")))
                .map(component -> (Span) component)
                .findFirst()
                .orElseThrow();
        final Button compareButton = leftPanel.getChildren()
                .filter(component -> "compare-button".equals(component.getElement().getAttribute("data-testid")))
                .map(component -> (Button) component)
                .findFirst()
                .orElseThrow();

        assertThat(feedback.getText()).isEqualTo("Selected tables: 0");
        assertThat(compareButton.isEnabled()).isFalse();
        assertThat(leftPanel.getChildren().anyMatch(component -> "table-selection-grid".equals(component.getElement().getAttribute("data-testid")))).isTrue();

        final Div rightPanel = (Div) stages.getChildren().toList().get(1);
        assertThat(rightPanel.getElement().getAttribute("data-testid")).isEqualTo("comparison-stage-placeholder");
    }

    private SqlServerTableCatalogService catalogServiceWithDefaults() {
        final SqlServerTableCatalogService service = Mockito.mock(SqlServerTableCatalogService.class);
        when(service.discoverTableCatalog()).thenReturn(List.of(
                TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                TableCatalogEntry.ineligible(new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"), "No unique index ending with _PK.")));
        return service;
    }

    private WebappComparisonProperties propertiesWithDefaults() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.getConnection().setServer("localhost:1433");
        properties.getConnection().setLeftDatabase("left_db");
        properties.getConnection().setRightDatabase("right_db");
        return properties;
    }
}
