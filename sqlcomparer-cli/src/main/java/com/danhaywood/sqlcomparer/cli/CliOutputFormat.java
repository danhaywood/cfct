package com.danhaywood.sqlcomparer.cli;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum CliOutputFormat {
    TEXT("text"),
    JSON("json"),
    EXCEL("excel");

    private final String argumentValue;

    CliOutputFormat(final String argumentValue) {
        this.argumentValue = argumentValue;
    }

    public String argumentValue() {
        return argumentValue;
    }

    public static CliOutputFormat parse(final String value) {
        if (value == null || value.isBlank()) {
            return TEXT;
        }
        final String normalizedValue = value.trim();
        for (CliOutputFormat format : values()) {
            if (format.argumentValue.equalsIgnoreCase(normalizedValue)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported output format '%s'. Supported values: %s"
                .formatted(value, supportedValues()));
    }

    private static String supportedValues() {
        return Arrays.stream(values())
                .map(CliOutputFormat::argumentValue)
                .collect(Collectors.joining(", "));
    }
}
