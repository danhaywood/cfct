package com.danhaywood.cfct.request;

import com.danhaywood.cfct.service.ComparisonProgressListener;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public record ComparisonOptions(
        String businessKeyIndexSuffix,
        Set<String> ignoredColumnNames,
        ComparisonProgressListener progressListener) {

    public ComparisonOptions {
        if (businessKeyIndexSuffix == null || businessKeyIndexSuffix.isBlank()) {
            throw new IllegalArgumentException("businessKeyIndexSuffix is required");
        }
        ignoredColumnNames = ignoredColumnNames.stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        progressListener = progressListener == null ? ComparisonProgressListener.NO_OP : progressListener;
    }

    public ComparisonOptions(final String businessKeyIndexSuffix, final Set<String> ignoredColumnNames) {
        this(businessKeyIndexSuffix, ignoredColumnNames, ComparisonProgressListener.NO_OP);
    }

    public static ComparisonOptions defaults() {
        return new ComparisonOptions("_PK", Set.of("version"), ComparisonProgressListener.NO_OP);
    }

    public boolean ignores(final String columnName) {
        return ignoredColumnNames.contains(columnName.toLowerCase(Locale.ROOT));
    }
}
