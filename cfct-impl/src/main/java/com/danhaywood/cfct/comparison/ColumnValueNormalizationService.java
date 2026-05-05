package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.spi.ColumnValueNormalizer;

import java.util.List;

final class ColumnValueNormalizationService {

    private final List<ColumnValueNormalizer> normalizers;

    ColumnValueNormalizationService(final List<ColumnValueNormalizer> normalizers) {
        this.normalizers = List.copyOf(normalizers);
    }

    ColumnValueNormalizer.NormalizedValues normalize(
            final ColumnMetadata columnMetadata,
            final String leftValue,
            final String rightValue) {
        var current = new ColumnValueNormalizer.NormalizedValues(leftValue, rightValue);
        for (final ColumnValueNormalizer normalizer : normalizers) {
            final var normalized = normalizer.normalize(columnMetadata, current.leftValue(), current.rightValue());
            if (normalized != null) {
                current = normalized;
            }
        }
        return current;
    }
}
