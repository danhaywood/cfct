package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgnoreColumnAdvisorUsingExtendedPropertiesTest {

    @Test
    void ignoresWhenExtendedPropertyIsTruthy() {
        final var advisor = new IgnoreColumnAdvisorUsingExtendedProperties(true);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("col"), false, "nvarchar", "true"))).isTrue();
        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("col"), false, "nvarchar", "YES"))).isTrue();
        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("col"), false, "nvarchar", "1"))).isTrue();
    }

    @Test
    void doesNotIgnoreForMissingOrFalseyValuesOrWhenDisabled() {
        final var enabledAdvisor = new IgnoreColumnAdvisorUsingExtendedProperties(true);
        final var disabledAdvisor = new IgnoreColumnAdvisorUsingExtendedProperties(false);

        assertThat(enabledAdvisor.shouldIgnore(new ColumnMetadata(new ColumnRef("col"), false, "nvarchar", null))).isFalse();
        assertThat(enabledAdvisor.shouldIgnore(new ColumnMetadata(new ColumnRef("col"), false, "nvarchar", "no"))).isFalse();
        assertThat(disabledAdvisor.shouldIgnore(new ColumnMetadata(new ColumnRef("col"), false, "nvarchar", "true"))).isFalse();
    }
}
