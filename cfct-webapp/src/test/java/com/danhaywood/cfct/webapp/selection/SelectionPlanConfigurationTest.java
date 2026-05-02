package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

class SelectionPlanConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void createsSelectionPlanFromExplicitTableProperties() {
        contextRunner
                .withPropertyValues(
                        "cfct.webapp.selection-plan.explicit.tables[0]=dbo.Supplier",
                        "cfct.webapp.selection-plan.explicit.tables[1]=dbo.PurchaseOrder")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final SelectionPlan plan = context.getBean(SelectionPlan.class);
                    assertThat(plan.resolveTables()).containsExactly(
                            new TableRef("dbo", "Supplier"),
                            new TableRef("dbo", "PurchaseOrder"));
                });
    }

    @Test
    void failsWhenExplicitTablesAreMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .hasStackTraceContaining("selection-plan.explicit.tables requires at least one schema.table entry");
        });
    }

    @Configuration
    @Import(SelectionPlanConfiguration.class)
    static class TestConfiguration {
    }
}
