package com.danhaywood.sqlcomparer.config;

public enum ComparisonOutputType {
    JSON("json");

    private final String jsonValue;

    ComparisonOutputType(final String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String jsonValue() {
        return jsonValue;
    }

    public static ComparisonOutputType parse(final String value) {
        if (value == null || value.isBlank()) {
            throw new ComparisonRequestException("Comparison output type is required");
        }
        if (JSON.jsonValue.equalsIgnoreCase(value.trim())) {
            return JSON;
        }
        throw new ComparisonRequestException("Unsupported comparison output type: %s".formatted(value));
    }
}
