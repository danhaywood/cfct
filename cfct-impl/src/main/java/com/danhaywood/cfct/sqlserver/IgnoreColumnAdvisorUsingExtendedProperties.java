package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;

import java.util.Locale;
import java.util.Set;

public class IgnoreColumnAdvisorUsingExtendedProperties implements IgnoreColumnAdvisor {

    private static final Set<String> TRUTHY_VALUES = Set.of("true", "1", "yes", "y", "on");

    private final boolean enabled;

    public IgnoreColumnAdvisorUsingExtendedProperties(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean shouldIgnore(final ColumnMetadata columnMetadata) {
        if (!enabled) {
            return false;
        }
        final String value = columnMetadata.ignoredExtendedPropertyValue();
        if (value == null) {
            return false;
        }
        return TRUTHY_VALUES.contains(value.trim().toLowerCase(Locale.ROOT));
    }
}
