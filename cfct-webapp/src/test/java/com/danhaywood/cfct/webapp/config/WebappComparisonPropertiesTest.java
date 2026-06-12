package com.danhaywood.cfct.webapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class WebappComparisonPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void bindsSharedExecutionPropertiesFromConfiguration() {
        contextRunner
                .withPropertyValues(
                        "spring.datasource.url=jdbc:sqlserver://server-host;encrypt=false;trustServerCertificate=true",
                        "spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver",
                        "spring.datasource.username=sa",
                        "spring.datasource.password=secret",
                        "cfct.webapp.connection.left-database=left_db",
                        "cfct.webapp.connection.right-database=right_db",
                        "cfct.webapp.validation.enabled=false",
                        "cfct.webapp.validation.fail-fast=false",
                        "cfct.webapp.automation.enabled=true",
                        "cfct.webapp.automation.username=robot",
                        "cfct.webapp.automation.password=secret",
                        "cfct.webapp.automation.left-database=automation_left",
                        "cfct.webapp.automation.right-database=automation_right")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final WebappDatasourceProperties datasourceProperties = context.getBean(WebappDatasourceProperties.class);
                    assertThat(datasourceProperties.getUrl()).isEqualTo("jdbc:sqlserver://server-host;encrypt=false;trustServerCertificate=true");
                    assertThat(datasourceProperties.getDriverClassName()).isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    assertThat(datasourceProperties.getUsername()).isEqualTo("sa");
                    assertThat(datasourceProperties.getPassword()).isEqualTo("secret");

                    final WebappComparisonProperties properties = context.getBean(WebappComparisonProperties.class);
                    assertThat(properties.getConnection().getLeftDatabase()).isEqualTo("left_db");
                    assertThat(properties.getConnection().getRightDatabase()).isEqualTo("right_db");
                    assertThat(properties.getValidation().isEnabled()).isFalse();
                    assertThat(properties.getValidation().isFailFast()).isFalse();
                    assertThat(properties.getAutomation().isEnabled()).isTrue();
                    assertThat(properties.getAutomation().getUsername()).isEqualTo("robot");
                    assertThat(properties.getAutomation().getPassword()).isEqualTo("secret");
                    assertThat(properties.getAutomation().getLeftDatabase()).isEqualTo("automation_left");
                    assertThat(properties.getAutomation().getRightDatabase()).isEqualTo("automation_right");
                });
    }

    @Test
    void bindsDatasourceDefaultsWhenNotConfigured() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final WebappDatasourceProperties datasourceProperties = context.getBean(WebappDatasourceProperties.class);
                    assertThat(datasourceProperties.getUrl()).isBlank();
                    assertThat(datasourceProperties.getDriverClassName()).isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    assertThat(datasourceProperties.getUsername()).isEqualTo("sa");
                    assertThat(datasourceProperties.getPassword()).isEqualTo("change-me");
                    final WebappComparisonProperties properties = context.getBean(WebappComparisonProperties.class);
                    assertThat(properties.getAutomation().isEnabled()).isFalse();
                    assertThat(properties.getAutomation().getUsername()).isBlank();
                    assertThat(properties.getAutomation().getPassword()).isBlank();
                });
    }

    @Test
    void ignoresDeprecatedTableSelectionKeysDuringMigration() {
        contextRunner
                .withPropertyValues(
                        "cfct.webapp.comparison.table-selection.tables[0]=dbo.Supplier",
                        "cfct.webapp.comparison.table-selection.tables-file=demo/tables.txt")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void configurationPropertiesClassesAvoidFieldLevelValueInjection() {
        assertThat(Arrays.stream(WebappComparisonProperties.class.getDeclaredFields())
                .map(this::valueAnnotation)
                .filter(annotation -> annotation != null))
                .isEmpty();
        assertThat(Arrays.stream(WebappDatasourceProperties.class.getDeclaredFields())
                .map(this::valueAnnotation)
                .filter(annotation -> annotation != null))
                .isEmpty();
    }

    private Value valueAnnotation(final Field field) {
        return field.getAnnotation(Value.class);
    }

    @Configuration
    @EnableConfigurationProperties({WebappComparisonProperties.class, WebappDatasourceProperties.class})
    static class TestConfiguration {
    }
}
