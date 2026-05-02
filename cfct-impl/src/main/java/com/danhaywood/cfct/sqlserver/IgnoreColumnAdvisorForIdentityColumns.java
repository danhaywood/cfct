package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;

public class IgnoreColumnAdvisorForIdentityColumns implements IgnoreColumnAdvisor {

    private final boolean enabled;

    public IgnoreColumnAdvisorForIdentityColumns(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean shouldIgnore(final ColumnMetadata columnMetadata) {
        return enabled && columnMetadata.identity();
    }
}
