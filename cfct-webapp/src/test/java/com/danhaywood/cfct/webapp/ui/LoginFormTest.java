package com.danhaywood.cfct.webapp.ui;

import com.danhaywood.cfct.webapp.auth.ConnectionLoginRequest;
import com.danhaywood.cfct.webapp.auth.WebappAuthenticationService;

import com.vaadin.flow.component.Component;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginFormTest {

    @Test
    void rendersRightSideBrandingElements() {
        final WebappAuthenticationService auth = mock(WebappAuthenticationService.class);
        when(auth.loginDefaults()).thenReturn(new ConnectionLoginRequest("jdbc:sqlserver://server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "secret", "left", "right"));

        final LoginForm form = new LoginForm(auth, () -> {
        });

        assertThat(findByTestId(form, "login-form-content")).isPresent();
        assertThat(findByTestId(form, "login-branding-panel")).isPresent();
        assertThat(findByTestId(form, "login-branding-logo")).isPresent();
        assertThat(findByTestId(form, "login-branding-name")).isPresent();
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
}
