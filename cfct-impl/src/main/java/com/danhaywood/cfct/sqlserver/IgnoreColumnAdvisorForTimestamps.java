package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;

import java.util.Locale;

public class IgnoreColumnAdvisorForTimestamps implements IgnoreColumnAdvisor {

    private final boolean enabled;

    public IgnoreColumnAdvisorForTimestamps(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean shouldIgnore(final ColumnMetadata columnMetadata) {
        if (!enabled) {
            return false;
        }
        final String typeName = columnMetadata.sqlTypeName();
        if (typeName != null) {
            final String normalizedType = typeName.toLowerCase(Locale.ROOT);
            if ("timestamp".equals(normalizedType) || "rowversion".equals(normalizedType)) {
                return true;
            }
        }
        return "version".equalsIgnoreCase(columnMetadata.column().name());
    }
}
