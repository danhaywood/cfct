package com.danhaywood.sqlcomparer.config;

public enum ComparisonOutputType {
    JSON("json"),
    YAML("yaml"),
    EXCEL("excel");

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
        final String normalizedValue = value.trim();
        for (ComparisonOutputType outputType : values()) {
            if (outputType.jsonValue.equalsIgnoreCase(normalizedValue)) {
                return outputType;
            }
        }
        throw new ComparisonRequestException("Unsupported comparison output type: %s".formatted(value));
    }
}
