package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.ComparisonRowStatus;
import com.danhaywood.sqlcomparer.model.ComparisonRowView;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonViewResult;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonViewResult;
import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.webapp.comparison.WebappComparisonExecutionService;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MainViewTest {

    @Test
    void rendersAppLayoutShellAndOkStatusInFooter() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(holder, catalogServiceWithDefaults(), propertiesWithDefaults(), mock(WebappComparisonExecutionService.class));

        assertThat(view.getElement().getAttribute("data-testid")).isEqualTo("main-app-layout");
        assertThat(findByTestId(view, "hamburger-menu")).isPresent();

        final Span status = (Span) findByTestId(view, "connection-status-state").orElseThrow();
        assertThat(status.getText()).contains(ConnectionValidationState.OK.name());
        assertThat(findByTestId(view, "connection-status-summary")).isEmpty();
    }

    @Test
    void rendersFailedStatusWithFailureSummaryInFooter() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markFailed("Configured database does not exist: missing_db");

        final MainView view = new MainView(holder, catalogServiceWithDefaults(), propertiesWithDefaults(), mock(WebappComparisonExecutionService.class));

        final Span status = (Span) findByTestId(view, "connection-status-state").orElseThrow();
        final Span summary = (Span) findByTestId(view, "connection-status-summary").orElseThrow();

        assertThat(status.getText()).contains(ConnectionValidationState.FAILED.name());
        assertThat(summary.getText()).contains("missing_db");
    }

    @Test
    void rendersFooterConnectionDetailsWithoutPassword() {
        final WebappComparisonProperties properties = propertiesWithDefaults();
        properties.getConnection().setPassword("super-secret-password");

        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithDefaults(), properties, mock(WebappComparisonExecutionService.class));
        final Footer footer = (Footer) findByTestId(view, "connection-details-footer").orElseThrow();

        final String footerText = textOf(footer);

        assertThat(footerText).contains("localhost:1433", "left_db", "right_db", "Status: OK");
        assertThat(footerText).doesNotContain("super-secret-password", "SQL connectivity status");
    }

    @Test
    void executesCompareAndRendersResultTabs() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class)))
                .thenReturn(sampleComparisonResult());

        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithPreselectedSupplier(), propertiesWithDefaults(), comparisonExecutionService);

        final Button compareButton = (Button) findByTestId(view, "compare-button").orElseThrow();
        assertThat(compareButton.isEnabled()).isTrue();

        compareButton.click();

        verify(comparisonExecutionService).compare(Mockito.any(MultiTableComparisonRequest.class));
        assertThat(findByTestId(view, "comparison-results-tabs")).isPresent();
        assertThat(findByTestId(view, "comparison-result-tab-dbo-supplier")).isPresent();
        assertThat(findByTestId(view, "comparison-grid-dbo-supplier")).isPresent();

        final Span state = (Span) findByTestId(view, "comparison-stage-state").orElseThrow();
        assertThat(state.getText()).contains("SUCCESS");
    }

    @Test
    void compareRequestUsesCurrentSelectedTables() {
        final WebappComparisonExecutionService comparisonExecutionService = mock(WebappComparisonExecutionService.class);
        when(comparisonExecutionService.compare(Mockito.any(MultiTableComparisonRequest.class)))
                .thenReturn(sampleComparisonResult());

        final MainView view = new MainView(new ConnectionValidationStatusHolder(), catalogServiceWithPreselectedProduct(), propertiesWithDefaults(), comparisonExecutionService);

        ((Button) findByTestId(view, "compare-button").orElseThrow()).click();

        final ArgumentCaptor<MultiTableComparisonRequest> captor = ArgumentCaptor.forClass(MultiTableComparisonRequest.class);
        verify(comparisonExecutionService).compare(captor.capture());
        assertThat(captor.getValue().tables()).containsExactly(new TableRef("dbo", "Product"));
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

    private WebappComparisonProperties propertiesWithDefaults() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.getConnection().setServer("localhost:1433");
        properties.getConnection().setLeftDatabase("left_db");
        properties.getConnection().setRightDatabase("right_db");
        return properties;
    }

    private MultiTableComparisonViewResult sampleComparisonResult() {
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
        return new MultiTableComparisonViewResult(List.of(supplier));
    }
}
