package com.danhaywood.cfct.request;

import com.danhaywood.cfct.service.ComparisonProgressListener;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public record ComparisonOptions(
        String businessKeyIndexSuffix,
        Set<String> ignoredColumnNames,
        ComparisonProgressListener progressListener,
        int maxParallelComparisons) {

    public ComparisonOptions {
        if (businessKeyIndexSuffix == null || businessKeyIndexSuffix.isBlank()) {
            throw new IllegalArgumentException("businessKeyIndexSuffix is required");
        }
        ignoredColumnNames = ignoredColumnNames.stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        progressListener = progressListener == null ? ComparisonProgressListener.NO_OP : progressListener;
        if (maxParallelComparisons < 1) {
            throw new IllegalArgumentException("maxParallelComparisons must be >= 1");
        }
    }

    public ComparisonOptions(final String businessKeyIndexSuffix, final Set<String> ignoredColumnNames) {
        this(businessKeyIndexSuffix, ignoredColumnNames, ComparisonProgressListener.NO_OP, defaultParallelism());
    }

    public ComparisonOptions(
            final String businessKeyIndexSuffix,
            final Set<String> ignoredColumnNames,
            final ComparisonProgressListener progressListener) {
        this(businessKeyIndexSuffix, ignoredColumnNames, progressListener, defaultParallelism());
    }

    public static ComparisonOptions defaults() {
        return new ComparisonOptions("_PK", Set.of("version"), ComparisonProgressListener.NO_OP, defaultParallelism());
    }

    private static int defaultParallelism() {
        return Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    public boolean ignores(final String columnName) {
        return ignoredColumnNames.contains(columnName.toLowerCase(Locale.ROOT));
    }
}
