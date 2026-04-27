package com.danhaywood.sqlcomparer.webapp.validation;

import com.danhaywood.sqlcomparer.webapp.config.WebappComparisonProperties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class ConnectivityValidationStartupRunnerTest {

    @Test
    void marksOkWhenValidationSucceeds() throws Exception {
        final SqlServerConnectivityValidationService service = mock(SqlServerConnectivityValidationService.class);
        final ConnectionValidationStatusHolder statusHolder = new ConnectionValidationStatusHolder();
        final ConnectivityValidationStartupRunner runner = new ConnectivityValidationStartupRunner(
                service,
                configuredValidation(true, true),
                statusHolder);

        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();
        assertThat(statusHolder.current().state()).isEqualTo(ConnectionValidationState.OK);
    }

    @Test
    void remainsFailFastByDefaultWhenValidationFails() {
        final SqlServerConnectivityValidationService service = mock(SqlServerConnectivityValidationService.class);
        doThrow(new SqlServerConnectivityValidationException("Unable to reach SQL Server"))
                .when(service)
                .validateConfiguredTargets();

        final ConnectionValidationStatusHolder statusHolder = new ConnectionValidationStatusHolder();
        final ConnectivityValidationStartupRunner runner = new ConnectivityValidationStartupRunner(
                service,
                configuredValidation(true, true),
                statusHolder);

        assertThatThrownBy(() -> runner.run(null))
                .isInstanceOf(SqlServerConnectivityValidationException.class)
                .hasMessageContaining("Unable to reach SQL Server");
        assertThat(statusHolder.current().state()).isEqualTo(ConnectionValidationState.FAILED);
    }

    @Test
    void allowsStartupWhenFailFastDisabled() throws Exception {
        final SqlServerConnectivityValidationService service = mock(SqlServerConnectivityValidationService.class);
        doThrow(new SqlServerConnectivityValidationException("Configured database does not exist: missing_db"))
                .when(service)
                .validateConfiguredTargets();

        final ConnectionValidationStatusHolder statusHolder = new ConnectionValidationStatusHolder();
        final ConnectivityValidationStartupRunner runner = new ConnectivityValidationStartupRunner(
                service,
                configuredValidation(true, false),
                statusHolder);

        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();
        assertThat(statusHolder.current().state()).isEqualTo(ConnectionValidationState.FAILED);
        assertThat(statusHolder.current().summary()).contains("missing_db");
    }

    @Test
    void skipsValidationWhenDisabled() throws Exception {
        final SqlServerConnectivityValidationService service = mock(SqlServerConnectivityValidationService.class);
        final ConnectionValidationStatusHolder statusHolder = new ConnectionValidationStatusHolder();
        final ConnectivityValidationStartupRunner runner = new ConnectivityValidationStartupRunner(
                service,
                configuredValidation(false, true),
                statusHolder);

        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();
        verifyNoInteractions(service);
        assertThat(statusHolder.current().state()).isEqualTo(ConnectionValidationState.OK);
        assertThat(statusHolder.current().summary()).contains("disabled");
    }

    private static WebappComparisonProperties configuredValidation(final boolean enabled, final boolean failFast) {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        final WebappComparisonProperties.Validation validation = new WebappComparisonProperties.Validation();
        validation.setEnabled(enabled);
        validation.setFailFast(failFast);
        properties.setValidation(validation);
        return properties;
    }
}
