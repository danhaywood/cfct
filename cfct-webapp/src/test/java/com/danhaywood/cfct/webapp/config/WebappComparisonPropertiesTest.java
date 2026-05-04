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
                        "cfct.webapp.connection.left-database=left_db",
                        "cfct.webapp.connection.right-database=right_db",
                        "cfct.webapp.validation.enabled=false",
                        "cfct.webapp.validation.fail-fast=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final WebappComparisonProperties properties = context.getBean(WebappComparisonProperties.class);
                    assertThat(properties.getDatasourceUrl()).isEqualTo("jdbc:sqlserver://server-host;encrypt=false;trustServerCertificate=true");
                    assertThat(properties.getDatasourceDriverClassName()).isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    assertThat(properties.getDatasourceUsername()).isEqualTo("sa");
                    assertThat(properties.getDatasourcePassword()).isEqualTo("secret");
                    assertThat(properties.getConnection().getLeftDatabase()).isEqualTo("left_db");
                    assertThat(properties.getConnection().getRightDatabase()).isEqualTo("right_db");
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
