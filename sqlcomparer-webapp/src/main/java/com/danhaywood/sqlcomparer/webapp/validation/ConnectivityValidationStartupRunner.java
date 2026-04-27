package com.danhaywood.sqlcomparer.webapp.validation;

import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ConnectivityValidationStartupRunner implements ApplicationRunner {

    private final SqlServerConnectivityValidationService validationService;
    private final WebappComparisonProperties properties;
    private final ConnectionValidationStatusHolder statusHolder;

    public ConnectivityValidationStartupRunner(
            final SqlServerConnectivityValidationService validationService,
            final WebappComparisonProperties properties,
            final ConnectionValidationStatusHolder statusHolder) {
        this.validationService = validationService;
        this.properties = properties;
        this.statusHolder = statusHolder;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (properties.getValidation() != null && !properties.getValidation().isEnabled()) {
            statusHolder.markOk("Connectivity validation disabled by configuration.");
            return;
        }

        try {
            validationService.validateConfiguredTargets();
            statusHolder.markOk("Connected to configured SQL Server and databases.");
        } catch (SqlServerConnectivityValidationException ex) {
            statusHolder.markFailed(ex.getMessage());
            if (properties.getValidation() == null || properties.getValidation().isFailFast()) {
                throw ex;
            }
        }
    }
}
