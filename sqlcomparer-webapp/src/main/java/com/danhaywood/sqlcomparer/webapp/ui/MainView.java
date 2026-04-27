package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationState;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatus;
import com.danhaywood.sqlcomparer.webapp.validation.ConnectionValidationStatusHolder;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {

    public MainView(final ConnectionValidationStatusHolder statusHolder) {
        add(new H2("sqlcomparer webapp scaffold"));
        add(renderConnectionStatus(statusHolder.current()));
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
