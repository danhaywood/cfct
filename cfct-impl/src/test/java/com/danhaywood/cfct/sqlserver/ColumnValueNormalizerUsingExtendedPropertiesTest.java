package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnValueNormalizerUsingExtendedPropertiesTest {

    @Test
    void scrubsTimestampFragmentsUsingConfiguredMaskAndPreservesOtherText() {
        final var normalizer = new ColumnValueNormalizerUsingExtendedProperties();
        final var metadata = new ColumnMetadata(
                new ColumnRef("payload"),
                false,
                "nvarchar",
                null,
                "yyyy-MM-ddThh:MM.ss.SSS");

        final var normalized = normalizer.normalize(
                metadata,
                "2026-04-20T14:08:09.050 - VT - [RENT, RENT_FIXED] - 2026-06-01",
                "2027-05-21T15:09:10.111 - VT - [RENT, RENT_FIXED] - 2026-06-01");

        assertThat(normalized.leftValue()).isEqualTo("yyyy-MM-ddThh:MM.ss.SSS - VT - [RENT, RENT_FIXED] - 2026-06-01");
        assertThat(normalized.rightValue()).isEqualTo("yyyy-MM-ddThh:MM.ss.SSS - VT - [RENT, RENT_FIXED] - 2026-06-01");
    }

    @Test
    void returnsOriginalValuesWhenMaskMissingUnsupportedOrUnmatched() {
        final var normalizer = new ColumnValueNormalizerUsingExtendedProperties();

        final var withoutMask = normalizer.normalize(
                new ColumnMetadata(new ColumnRef("payload"), false, "nvarchar", null, null),
                "left",
                "right");

        final var unsupportedMask = normalizer.normalize(
                new ColumnMetadata(new ColumnRef("payload"), false, "nvarchar", null, "yyyy-MMM-dd"),
                "2026-Apr-20",
                "2026-May-20");

        final var unmatchedMask = normalizer.normalize(
                new ColumnMetadata(new ColumnRef("payload"), false, "nvarchar", null, "yyyy-MM-dd"),
                "not-a-date",
                "still-not-a-date");

        assertThat(withoutMask.leftValue()).isEqualTo("left");
        assertThat(withoutMask.rightValue()).isEqualTo("right");
        assertThat(unsupportedMask.leftValue()).isEqualTo("2026-Apr-20");
        assertThat(unsupportedMask.rightValue()).isEqualTo("2026-May-20");
        assertThat(unmatchedMask.leftValue()).isEqualTo("not-a-date");
        assertThat(unmatchedMask.rightValue()).isEqualTo("still-not-a-date");
    }
}
