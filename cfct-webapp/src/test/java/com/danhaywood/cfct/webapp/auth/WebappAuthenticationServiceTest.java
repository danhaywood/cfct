package com.danhaywood.cfct.webapp.auth;

import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationState;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationStatusHolder;
import com.danhaywood.cfct.webapp.validation.SqlServerConnectivityValidationException;
import com.danhaywood.cfct.webapp.validation.SqlServerConnectivityValidationService;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class WebappAuthenticationServiceTest {

    @Test
    void authenticatesAndStoresSessionContextOnSuccess() {
        final SqlServerConnectivityValidationService validationService = mock(SqlServerConnectivityValidationService.class);
        final AuthenticatedConnectionContextHolder authHolder = new AuthenticatedConnectionContextHolder();
        final ConnectionValidationStatusHolder statusHolder = new ConnectionValidationStatusHolder();
        final WebappAuthenticationService service = new WebappAuthenticationService(
                propertiesWithDefaults(),
                validationService,
                authHolder,
                statusHolder);

        service.authenticate(new ConnectionLoginRequest("jdbc:sqlserver://server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "secret", "left_db", "right_db"));

        assertThat(authHolder.isAuthenticated()).isTrue();
        assertThat(authHolder.required().jdbcUrl()).isEqualTo("jdbc:sqlserver://server");
        assertThat(statusHolder.current().state()).isEqualTo(ConnectionValidationState.OK);
    }

    @Test
    void failsAuthenticationWhenRequiredFieldMissing() {
        final WebappAuthenticationService service = new WebappAuthenticationService(
                propertiesWithDefaults(),
                mock(SqlServerConnectivityValidationService.class),
                new AuthenticatedConnectionContextHolder(),
                new ConnectionValidationStatusHolder());

        assertThatThrownBy(() -> service.authenticate(new ConnectionLoginRequest("", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "secret", "left_db", "right_db")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JDBC URL is required");
    }

    @Test
    void failsAuthenticationForInvalidConnectionInputs() {
        final SqlServerConnectivityValidationService validationService = mock(SqlServerConnectivityValidationService.class);
        doThrow(new SqlServerConnectivityValidationException("Authentication failed"))
                .when(validationService)
                .validate(new AuthenticatedConnectionContext("jdbc:sqlserver://server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "wrong", "left_db", "right_db"));

        final WebappAuthenticationService service = new WebappAuthenticationService(
                propertiesWithDefaults(),
                validationService,
                new AuthenticatedConnectionContextHolder(),
                new ConnectionValidationStatusHolder());

        assertThatThrownBy(() -> service.authenticate(new ConnectionLoginRequest("jdbc:sqlserver://server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "wrong", "left_db", "right_db")))
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Authentication failed");
    }

    @Test
    void logoutClearsAuthenticatedSessionState() {
        final SqlServerConnectivityValidationService validationService = mock(SqlServerConnectivityValidationService.class);
        final AuthenticatedConnectionContextHolder authHolder = new AuthenticatedConnectionContextHolder();
        final ConnectionValidationStatusHolder statusHolder = new ConnectionValidationStatusHolder();
        final WebappAuthenticationService service = new WebappAuthenticationService(
                propertiesWithDefaults(),
                validationService,
                authHolder,
                statusHolder);

        service.authenticate(new ConnectionLoginRequest("jdbc:sqlserver://server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "sa", "secret", "left_db", "right_db"));
        service.logout();

        assertThat(authHolder.isAuthenticated()).isFalse();
        assertThat(statusHolder.current().summary()).contains("Login required");
    }

    @Test
    void exposesConfigPropertiesAsLoginDefaults() {
        final WebappAuthenticationService service = new WebappAuthenticationService(
                propertiesWithDefaults(),
                mock(SqlServerConnectivityValidationService.class),
                new AuthenticatedConnectionContextHolder(),
                new ConnectionValidationStatusHolder());

        final ConnectionLoginRequest defaults = service.loginDefaults();
        assertThat(defaults.jdbcUrl()).isEqualTo("jdbc:sqlserver://localhost:1433");
        assertThat(defaults.jdbcDriver()).isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertThat(defaults.username()).isEqualTo("sa");
        assertThat(defaults.password()).isEqualTo("change-me");
        assertThat(defaults.leftDatabase()).isEqualTo("left_db");
        assertThat(defaults.rightDatabase()).isEqualTo("right_db");
    }

    private WebappComparisonProperties propertiesWithDefaults() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.setDatasourceUrl("jdbc:sqlserver://localhost:1433");
        properties.setDatasourceDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        properties.setDatasourceUsername("sa");
        properties.setDatasourcePassword("change-me");
        properties.setLeftDatabase("left_db");
        properties.setRightDatabase("right_db");
        return properties;
    }
}
