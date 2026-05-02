package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;

import java.util.Locale;

public class IgnoreColumnAdvisorForUuidColumns implements IgnoreColumnAdvisor {

    private final boolean enabled;

    public IgnoreColumnAdvisorForUuidColumns(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean shouldIgnore(final ColumnMetadata columnMetadata) {
        if (!enabled) {
            return false;
        }
        final String normalized = columnMetadata.column().name().toLowerCase(Locale.ROOT);
        return "guid".equals(normalized)
                || "uuid".equals(normalized)
                || columnMetadata.uniqueIdentifierType();
    }
}
