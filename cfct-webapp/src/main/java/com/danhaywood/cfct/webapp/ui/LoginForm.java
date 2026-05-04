package com.danhaywood.cfct.webapp.ui;

import com.danhaywood.cfct.webapp.auth.ConnectionLoginRequest;
import com.danhaywood.cfct.webapp.auth.WebappAuthenticationService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class LoginForm extends VerticalLayout {

    private final WebappAuthenticationService authenticationService;
    private final Runnable onSuccess;

    private final TextField jdbcUrl = new TextField("JDBC URL");
    private final TextField jdbcDriver = new TextField("JDBC Driver");
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final TextField leftDatabase = new TextField("Left database");
    private final TextField rightDatabase = new TextField("Right database");
    private final Span error = new Span();

    public LoginForm(final WebappAuthenticationService authenticationService, final Runnable onSuccess) {
        this.authenticationService = authenticationService;
        this.onSuccess = onSuccess;

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        getElement().setAttribute("data-testid", "login-form");

        final VerticalLayout fieldsColumn = new VerticalLayout();
        fieldsColumn.setPadding(false);
        fieldsColumn.setSpacing(true);
        fieldsColumn.setWidthFull();
        fieldsColumn.getStyle().set("min-width", "3rem");

        jdbcUrl.getElement().setAttribute("data-testid", "login-jdbc-url");
        jdbcDriver.getElement().setAttribute("data-testid", "login-jdbc-driver");
        username.getElement().setAttribute("data-testid", "login-username");
        password.getElement().setAttribute("data-testid", "login-password");
        leftDatabase.getElement().setAttribute("data-testid", "login-left-database");
        rightDatabase.getElement().setAttribute("data-testid", "login-right-database");

        final ConnectionLoginRequest configuredDefaults = authenticationService.loginDefaults();
        final ConnectionLoginRequest defaults = configuredDefaults == null
                ? new ConnectionLoginRequest(null, null, null, null, null, null)
                : configuredDefaults;
        jdbcUrl.setValue(orEmpty(defaults.jdbcUrl()));
        jdbcDriver.setValue(orEmpty(defaults.jdbcDriver()));
        username.setValue(orEmpty(defaults.username()));
        password.setValue(orEmpty(defaults.password()));
        leftDatabase.setValue(orEmpty(defaults.leftDatabase()));
        rightDatabase.setValue(orEmpty(defaults.rightDatabase()));

        final Button login = new Button("Login");
        login.getElement().setAttribute("data-testid", "login-submit");
        login.addClickListener(event -> authenticate());

        error.getElement().setAttribute("data-testid", "login-error");
        error.getStyle().set("color", "var(--lumo-error-text-color)");

        fieldsColumn.add(jdbcUrl, jdbcDriver, username, password, leftDatabase, rightDatabase, login, error);

        final VerticalLayout brandingPanel = new VerticalLayout();
        brandingPanel.getElement().setAttribute("data-testid", "login-branding-panel");
        brandingPanel.setPadding(false);
        brandingPanel.setSpacing(true);
        brandingPanel.setAlignItems(FlexComponent.Alignment.CENTER);
        brandingPanel.getStyle()
                .set("min-width", "5rem")
                .set("justify-content", "center");

        final Image logo = new Image("/images/cfct-logo.png", "CFCT logo");
        logo.getElement().setAttribute("data-testid", "login-branding-logo");
        logo.setWidth("360px");

        final Span brandingName = new Span("CFCT");
        brandingName.getElement().setAttribute("data-testid", "login-branding-name");
        brandingName.getStyle().set("font-weight", "700");

        brandingPanel.add(logo, brandingName);

        final HorizontalLayout content = new HorizontalLayout(fieldsColumn, brandingPanel);
        content.setWidthFull();
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle()
                .set("flex-wrap", "nowrap")
                .set("column-gap", "var(--lumo-space-m)");
        content.getElement().setAttribute("data-testid", "login-form-content");

        add(content);
    }

    private void authenticate() {
        error.setText("");
        try {
            authenticationService.authenticate(new ConnectionLoginRequest(
                    jdbcUrl.getValue(),
                    jdbcDriver.getValue(),
                    username.getValue(),
                    password.getValue(),
                    leftDatabase.getValue(),
                    rightDatabase.getValue()));
            onSuccess.run();
        } catch (RuntimeException ex) {
            error.setText(ex.getMessage() == null ? "Login failed." : ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private String orEmpty(final String value) {
        return value == null ? "" : value;
    }
}
