package com.danhaywood.sqlcomparer.request;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public record ComparisonOptions(String businessKeyIndexSuffix, Set<String> ignoredColumnNames) {

    public ComparisonOptions {
        if (businessKeyIndexSuffix == null || businessKeyIndexSuffix.isBlank()) {
            throw new IllegalArgumentException("businessKeyIndexSuffix is required");
        }
        ignoredColumnNames = ignoredColumnNames.stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static ComparisonOptions defaults() {
        return new ComparisonOptions("_BK", Set.of("id", "version"));
    }

    public boolean ignores(final String columnName) {
        return ignoredColumnNames.contains(columnName.toLowerCase(Locale.ROOT));
    }
}
