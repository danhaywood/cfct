package com.danhaywood.sqlcomparer.webapp.ui;

import com.danhaywood.sqlcomparer.webapp.auth.ConnectionLoginRequest;
import com.danhaywood.sqlcomparer.webapp.auth.WebappAuthenticationService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final WebappAuthenticationService authenticationService;

    private final TextField server = new TextField("Server");
    private final TextField leftDatabase = new TextField("Left database");
    private final TextField rightDatabase = new TextField("Right database");
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final Span error = new Span();

    public LoginView(final WebappAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;

        setWidthFull();
        setMaxWidth("32rem");
        setPadding(true);
        setSpacing(true);
        getElement().setAttribute("data-testid", "login-view");

        add(new H2("Login"));

        server.getElement().setAttribute("data-testid", "login-server");
        leftDatabase.getElement().setAttribute("data-testid", "login-left-database");
        rightDatabase.getElement().setAttribute("data-testid", "login-right-database");
        username.getElement().setAttribute("data-testid", "login-username");
        password.getElement().setAttribute("data-testid", "login-password");

        final ConnectionLoginRequest defaults = authenticationService.loginDefaults();
        server.setValue(orEmpty(defaults.server()));
        leftDatabase.setValue(orEmpty(defaults.leftDatabase()));
        rightDatabase.setValue(orEmpty(defaults.rightDatabase()));
        username.setValue(orEmpty(defaults.username()));
        password.setValue(orEmpty(defaults.password()));

        final Button login = new Button("Login");
        login.getElement().setAttribute("data-testid", "login-submit");
        login.addClickListener(event -> authenticate());

        error.getElement().setAttribute("data-testid", "login-error");
        error.getStyle().set("color", "var(--lumo-error-text-color)");

        add(server, leftDatabase, rightDatabase, username, password, login, error);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (authenticationService.isAuthenticated()) {
            event.rerouteTo(MainView.class);
        }
    }

    private void authenticate() {
        error.setText("");
        try {
            authenticationService.authenticate(new ConnectionLoginRequest(
                    server.getValue(),
                    username.getValue(),
                    password.getValue(),
                    leftDatabase.getValue(),
                    rightDatabase.getValue()));
            getUI().ifPresent(ui -> ui.navigate(MainView.class));
        } catch (RuntimeException ex) {
            error.setText(ex.getMessage() == null ? "Login failed." : ex.getMessage());
        }
    }

    private String orEmpty(final String value) {
        return value == null ? "" : value;
    }
}
