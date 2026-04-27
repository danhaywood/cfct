package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MainViewTest {

    @Test
    void rendersOkStatusWithoutFailureSummary() {
        final ConnectionValidationStatusHolder holder = new ConnectionValidationStatusHolder();
        holder.markOk("Connected to configured SQL Server and databases.");

        final MainView view = new MainView(holder);
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

        final MainView view = new MainView(holder);
        final Div panel = (Div) view.getChildren().toList().get(1);

        final List<String> texts = panel.getChildren()
                .filter(component -> component instanceof HasText)
                .map(component -> ((HasText) component).getText())
                .toList();

        assertThat(texts).anyMatch(text -> text.contains(ConnectionValidationState.FAILED.name()));
        assertThat(texts).anyMatch(text -> text.contains("missing_db"));
    }
}
