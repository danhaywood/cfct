package com.danhaywood.sqlcomparer.cli;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record CliExecutionOutput(
        CliOutputFormat outputFormat,
        String mediaType,
        String fileExtension,
        byte[] bytes
) {

    public CliExecutionOutput {
        if (outputFormat == null) {
            throw new IllegalArgumentException("outputFormat is required");
        }
        if (mediaType == null || mediaType.isBlank()) {
            throw new IllegalArgumentException("mediaType is required");
        }
        if (fileExtension == null || fileExtension.isBlank()) {
            throw new IllegalArgumentException("fileExtension is required");
        }
        bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public static CliExecutionOutput text(final String text) {
        return new CliExecutionOutput(
                CliOutputFormat.TEXT,
                "text/plain; charset=utf-8",
                "txt",
                text.getBytes(StandardCharsets.UTF_8));
    }

    public static CliExecutionOutput json(final String json) {
        return new CliExecutionOutput(
                CliOutputFormat.JSON,
                "application/json",
                "json",
                json.getBytes(StandardCharsets.UTF_8));
    }

    public static CliExecutionOutput yaml(final String yaml) {
        return new CliExecutionOutput(
                CliOutputFormat.YAML,
                "application/yaml",
                "yaml",
                yaml.getBytes(StandardCharsets.UTF_8));
    }

    public static CliExecutionOutput excel(final byte[] workbookBytes) {
        return new CliExecutionOutput(
                CliOutputFormat.EXCEL,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "xlsx",
                workbookBytes);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
