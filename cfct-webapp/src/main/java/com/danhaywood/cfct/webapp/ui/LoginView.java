package com.danhaywood.cfct.webapp.ui;

import com.danhaywood.cfct.webapp.auth.WebappAuthenticationService;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    public LoginView(final WebappAuthenticationService authenticationService) {
        setWidthFull();
        setPadding(false);
        getElement().setAttribute("data-testid", "login-view");
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        event.rerouteTo(MainView.class);
    }
}
