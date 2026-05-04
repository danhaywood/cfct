package com.danhaywood.cfct.webapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

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
                        "cfct.webapp.comparison.left-database=left_db",
                        "cfct.webapp.comparison.right-database=right_db",
                        "cfct.webapp.comparison.env-file=demo/.env",
                        "cfct.webapp.comparison.output.format=json",
                        "cfct.webapp.comparison.output.file=comparison.json",
                        "cfct.webapp.comparison.validation.enabled=false",
                        "cfct.webapp.comparison.validation.fail-fast=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final WebappComparisonProperties properties = context.getBean(WebappComparisonProperties.class);
                    assertThat(properties.getDatasourceUrl()).isEqualTo("jdbc:sqlserver://server-host;encrypt=false;trustServerCertificate=true");
                    assertThat(properties.getDatasourceDriverClassName()).isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    assertThat(properties.getDatasourceUsername()).isEqualTo("sa");
                    assertThat(properties.getDatasourcePassword()).isEqualTo("secret");
                    assertThat(properties.getLeftDatabase()).isEqualTo("left_db");
                    assertThat(properties.getRightDatabase()).isEqualTo("right_db");
                    assertThat(properties.getEnvFile()).isEqualTo("demo/.env");
                    assertThat(properties.getOutput().getFormat()).isEqualTo("json");
                    assertThat(properties.getOutput().getFile()).isEqualTo("comparison.json");
                    assertThat(properties.getValidation().isEnabled()).isFalse();
                    assertThat(properties.getValidation().isFailFast()).isFalse();
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

    @Configuration
    @EnableConfigurationProperties(WebappComparisonProperties.class)
    static class TestConfiguration {
    }
}
