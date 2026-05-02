package com.danhaywood.cfct.implspring;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IgnoreColumnAdvisorsPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(ComparisonImplementationConfiguration.class);

    @Test
    void advisorsAreEnabledByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            final List<IgnoreColumnAdvisor> advisors = context.getBeanProvider(IgnoreColumnAdvisor.class).orderedStream().toList();
            assertThat(advisors).hasSize(3);

            assertThat(ignoredByAny(advisors, new ColumnMetadata(new ColumnRef("id"), true, "int"))).isTrue();
            assertThat(ignoredByAny(advisors, new ColumnMetadata(new ColumnRef("uuid"), false, "uniqueidentifier"))).isTrue();
            assertThat(ignoredByAny(advisors, new ColumnMetadata(new ColumnRef("version"), false, "datetime2"))).isTrue();
        });
    }

    @Test
    void oneAdvisorCanBeDisabledWithoutAffectingOthers() {
        contextRunner
                .withPropertyValues("cfct.comparison.ignore-column-advisors.uuid-enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    final List<IgnoreColumnAdvisor> advisors = context.getBeanProvider(IgnoreColumnAdvisor.class).orderedStream().toList();
                    assertThat(advisors).hasSize(3);

                    assertThat(ignoredByAny(advisors, new ColumnMetadata(new ColumnRef("id"), true, "int"))).isTrue();
                    assertThat(ignoredByAny(advisors, new ColumnMetadata(new ColumnRef("uuid"), false, "uniqueidentifier"))).isFalse();
                    assertThat(ignoredByAny(advisors, new ColumnMetadata(new ColumnRef("version"), false, "datetime2"))).isTrue();
                });
    }

    private static boolean ignoredByAny(final List<IgnoreColumnAdvisor> advisors, final ColumnMetadata columnMetadata) {
        return advisors.stream().anyMatch(advisor -> advisor.shouldIgnore(columnMetadata));
    }
}
