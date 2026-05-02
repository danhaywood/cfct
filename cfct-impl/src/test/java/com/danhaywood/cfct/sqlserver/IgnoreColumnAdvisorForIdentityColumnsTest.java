package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgnoreColumnAdvisorForIdentityColumnsTest {

    @Test
    void ignoresIdentityWhenEnabled() {
        final var advisor = new IgnoreColumnAdvisorForIdentityColumns(true);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("id"), true, "int"))).isTrue();
    }

    @Test
    void doesNotIgnoreIdentityWhenDisabled() {
        final var advisor = new IgnoreColumnAdvisorForIdentityColumns(false);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("id"), true, "int"))).isFalse();
    }
}
