package com.danhaywood.sqlcomparer.webapp.config;

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
                        "sqlcomparer.webapp.comparison.connection.server=server-host",
                        "sqlcomparer.webapp.comparison.connection.username=sa",
                        "sqlcomparer.webapp.comparison.connection.password=secret",
                        "sqlcomparer.webapp.comparison.connection.left-database=left_db",
                        "sqlcomparer.webapp.comparison.connection.right-database=right_db",
                        "sqlcomparer.webapp.comparison.env-file=demo/.env",
                        "sqlcomparer.webapp.comparison.output.format=json",
                        "sqlcomparer.webapp.comparison.output.file=comparison.json",
                        "sqlcomparer.webapp.comparison.validation.enabled=false",
                        "sqlcomparer.webapp.comparison.validation.fail-fast=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final WebappComparisonProperties properties = context.getBean(WebappComparisonProperties.class);
                    assertThat(properties.getConnection().getServer()).isEqualTo("server-host");
                    assertThat(properties.getConnection().getUsername()).isEqualTo("sa");
                    assertThat(properties.getConnection().getPassword()).isEqualTo("secret");
                    assertThat(properties.getConnection().getLeftDatabase()).isEqualTo("left_db");
                    assertThat(properties.getConnection().getRightDatabase()).isEqualTo("right_db");
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
                        "sqlcomparer.webapp.comparison.table-selection.tables[0]=dbo.Supplier",
                        "sqlcomparer.webapp.comparison.table-selection.tables-file=demo/tables.txt")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Configuration
    @EnableConfigurationProperties(WebappComparisonProperties.class)
    static class TestConfiguration {
    }
}
