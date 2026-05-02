package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgnoreColumnAdvisorForTimestampsTest {

    @Test
    void ignoresTimestampLikeColumnsWhenEnabled() {
        final var advisor = new IgnoreColumnAdvisorForTimestamps(true);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("version"), false, "datetime2"))).isTrue();
        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("rowVersion"), false, "rowversion"))).isTrue();
        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("legacyStamp"), false, "timestamp"))).isTrue();
    }

    @Test
    void doesNotIgnoreWhenDisabled() {
        final var advisor = new IgnoreColumnAdvisorForTimestamps(false);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("version"), false, "rowversion"))).isFalse();
    }
}
