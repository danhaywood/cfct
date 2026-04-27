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
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MainViewTest {

    @Test
    void rendersAppLayoutShellAndOkStatusInFooter() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(holder, catalogServiceWithDefaults(), propertiesWithDefaults());

        assertThat(view.getElement().getAttribute("data-testid")).isEqualTo("main-app-layout");
        assertThat(findByTestId(view, "hamburger-menu")).isPresent();
        assertThat(findByTestId(view, "main-shell-header")).isPresent();

        final Span status = (Span) findByTestId(view, "connection-status-state").orElseThrow();
        assertThat(status.getText()).contains(ConnectionValidationState.OK.name());
        assertThat(findByTestId(view, "connection-status-summary")).isEmpty();
    }

    @Test
    void rendersFailedStatusWithFailureSummaryInFooter() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markFailed("Configured database does not exist: missing_db");

        final MainView view = new MainView(holder, catalogServiceWithDefaults(), propertiesWithDefaults());

        final Span status = (Span) findByTestId(view, "connection-status-state").orElseThrow();
        final Span summary = (Span) findByTestId(view, "connection-status-summary").orElseThrow();

        assertThat(status.getText()).contains(ConnectionValidationState.FAILED.name());
        assertThat(summary.getText()).contains("missing_db");
    }

    @Test
    void rendersFooterConnectionDetailsWithoutPassword() {
        final WebappComparisonProperties properties = propertiesWithDefaults();
        properties.getConnection().setPassword("super-secret-password");

        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithDefaults(), properties);
        final Footer footer = (Footer) findByTestId(view, "connection-details-footer").orElseThrow();

        final String footerText = textOf(footer);

        assertThat(footerText).contains("localhost:1433", "left_db", "right_db", "SQL connectivity status");
        assertThat(footerText).doesNotContain("super-secret-password");
    }

    @Test
    void rendersSelectionPanelInNavigationAreaAndCompareButtonInMainActionBar() {
        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithDefaults(), propertiesWithDefaults());

        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();

        assertThat(compareButton.isEnabled()).isFalse();
        assertThat(findByTestId(view, "table-selection-panel")).isPresent();
        assertThat(findByTestId(view, "table-selection-grid")).isPresent();
        assertThat(findByTestId(view, "selected-table-feedback")).isEmpty();
        assertThat(findByTestId(view, "apply-table-filter")).isEmpty();
        assertThat(findByTestId(view, "comparison-stage-placeholder")).isPresent();
        assertThat(findByTestId(view, "comparison-action-bar")).isPresent();
    }

    private Optional<Component> findByTestId(final Component root, final String testId) {
        if (testId.equals(root.getElement().getAttribute("data-testid"))) {
            return Optional.of(root);
        }
        return root.getChildren()
                .flatMap(child -> streamWithDescendants(child))
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
