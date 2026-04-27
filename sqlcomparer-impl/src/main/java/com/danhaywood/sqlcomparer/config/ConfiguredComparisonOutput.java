package com.danhaywood.sqlcomparer.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record ConfiguredComparisonOutput(
        ComparisonOutputType outputType,
        String mediaType,
        String fileExtension,
        byte[] bytes
) {

    public ConfiguredComparisonOutput {
        if (outputType == null) {
            throw new IllegalArgumentException("outputType is required");
        }
        if (mediaType == null || mediaType.isBlank()) {
            throw new IllegalArgumentException("mediaType is required");
        }
        if (fileExtension == null || fileExtension.isBlank()) {
            throw new IllegalArgumentException("fileExtension is required");
        }
        bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public static ConfiguredComparisonOutput json(final String json) {
        return new ConfiguredComparisonOutput(
                ComparisonOutputType.JSON,
                "application/json",
                "json",
                json.getBytes(StandardCharsets.UTF_8));
    }

    public static ConfiguredComparisonOutput excel(final byte[] workbookBytes) {
        return new ConfiguredComparisonOutput(
                ComparisonOutputType.EXCEL,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "xlsx",
                workbookBytes);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String contentAsString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
