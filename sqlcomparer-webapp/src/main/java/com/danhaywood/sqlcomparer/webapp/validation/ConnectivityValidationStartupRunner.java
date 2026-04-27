package com.danhaywood.sqlcomparer.webapp.validation;

import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ConnectivityValidationStartupRunner implements ApplicationRunner {

    private final SqlServerConnectivityValidationService validationService;
    private final WebappComparisonProperties properties;

    public ConnectivityValidationStartupRunner(
            final SqlServerConnectivityValidationService validationService,
            final WebappComparisonProperties properties) {
        this.validationService = validationService;
        this.properties = properties;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (properties.getValidation() != null && !properties.getValidation().isEnabled()) {
            return;
        }
        validationService.validateConfiguredTargets();
    }
}
