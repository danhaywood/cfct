package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.spi.ColumnValueNormalizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnValueNormalizationServiceTest {

    @Test
    void appliesNormalizersInProvidedOrder() {
        final List<String> calls = new ArrayList<>();
        final ColumnValueNormalizer first = (columnMetadata, leftValue, rightValue) -> {
            calls.add("first:" + leftValue + ":" + rightValue);
            return new ColumnValueNormalizer.NormalizedValues(leftValue + "-A", rightValue + "-A");
        };
        final ColumnValueNormalizer second = (columnMetadata, leftValue, rightValue) -> {
            calls.add("second:" + leftValue + ":" + rightValue);
            return new ColumnValueNormalizer.NormalizedValues(leftValue + "-B", rightValue + "-B");
        };

        final var service = new ColumnValueNormalizationService(List.of(first, second));

        final var normalized = service.normalize(
                new ColumnMetadata(new ColumnRef("status"), false, "nvarchar"),
                "left",
                "right");

        assertThat(calls).containsExactly(
                "first:left:right",
                "second:left-A:right-A");
        assertThat(normalized.leftValue()).isEqualTo("left-A-B");
        assertThat(normalized.rightValue()).isEqualTo("right-A-B");
    }

    @Test
    void returnsOriginalValuesWhenNormalizersDoNotChangeAnything() {
        final ColumnValueNormalizer noOp = (columnMetadata, leftValue, rightValue) ->
                new ColumnValueNormalizer.NormalizedValues(leftValue, rightValue);

        final var service = new ColumnValueNormalizationService(List.of(noOp));

        final var normalized = service.normalize(
                new ColumnMetadata(new ColumnRef("status"), false, "nvarchar"),
                "left",
                "right");

        assertThat(normalized.leftValue()).isEqualTo("left");
        assertThat(normalized.rightValue()).isEqualTo("right");
    }
}
