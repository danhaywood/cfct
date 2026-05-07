package com.danhaywood.cfct.webapp.auth;

import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.config.WebappDatasourceProperties;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationStatusHolder;
import com.danhaywood.cfct.webapp.validation.SqlServerConnectivityValidationService;

import org.springframework.stereotype.Service;

@Service
public class WebappAuthenticationService {

    private static final String LOGGED_OUT_SUMMARY = "Login required.";

    private final WebappComparisonProperties properties;
    private final WebappDatasourceProperties datasourceProperties;
    private final SqlServerConnectivityValidationService validationService;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;
    private final ConnectionValidationStatusHolder statusHolder;

    public WebappAuthenticationService(
            final WebappComparisonProperties properties,
            final WebappDatasourceProperties datasourceProperties,
            final SqlServerConnectivityValidationService validationService,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder,
            final ConnectionValidationStatusHolder statusHolder) {
        this.properties = properties;
        this.datasourceProperties = datasourceProperties;
        this.validationService = validationService;
        this.authenticatedContextHolder = authenticatedContextHolder;
        this.statusHolder = statusHolder;
    }

    public ConnectionLoginRequest loginDefaults() {
        return new ConnectionLoginRequest(
                datasourceProperties.getUrl(),
                datasourceProperties.getDriverClassName(),
                datasourceProperties.getUsername(),
                datasourceProperties.getPassword(),
                properties.getConnection().getLeftDatabase(),
                properties.getConnection().getRightDatabase());
    }

    public void authenticate(final ConnectionLoginRequest request) {
        try {
            final AuthenticatedConnectionContext context = request.toAuthenticatedContext();
            validationService.validate(context);
            authenticatedContextHolder.set(context);
            statusHolder.markOk("Connected to SQL Server and selected databases.");
        } catch (RuntimeException ex) {
            authenticatedContextHolder.clear();
            statusHolder.markFailed(ex.getMessage() == null ? "Login failed." : ex.getMessage());
            throw ex;
        }
    }

    public void logout() {
        authenticatedContextHolder.clear();
        statusHolder.markOk(LOGGED_OUT_SUMMARY);
    }

    public boolean isAuthenticated() {
        return authenticatedContextHolder.isAuthenticated();
    }
}
