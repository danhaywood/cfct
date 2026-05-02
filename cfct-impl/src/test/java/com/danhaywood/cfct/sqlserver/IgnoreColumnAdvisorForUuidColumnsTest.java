package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgnoreColumnAdvisorForUuidColumnsTest {

    @Test
    void ignoresGuidAndUuidConventionsWhenEnabled() {
        final var advisor = new IgnoreColumnAdvisorForUuidColumns(true);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("Guid"), false, "nvarchar"))).isTrue();
        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("uuid"), false, "nvarchar"))).isTrue();
        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("external_id"), false, "uniqueidentifier"))).isTrue();
    }

    @Test
    void doesNotIgnoreWhenDisabled() {
        final var advisor = new IgnoreColumnAdvisorForUuidColumns(false);

        assertThat(advisor.shouldIgnore(new ColumnMetadata(new ColumnRef("Guid"), false, "uniqueidentifier"))).isFalse();
    }
}
