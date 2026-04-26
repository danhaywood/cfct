package com.danhaywood.sqlcomparer.config;

import com.danhaywood.sqlcomparer.core.MultiTableComparer;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;

@Service
public final class ConfiguredComparisonService {

    private final JsonComparisonRequestLoader requestLoader;
    private final MultiTableComparer comparer;
    private final JsonMultiTableComparisonReportRenderer jsonRenderer;

    public ConfiguredComparisonService(
            final JsonComparisonRequestLoader requestLoader,
            final MultiTableComparer comparer,
            final JsonMultiTableComparisonReportRenderer jsonRenderer) {
        this.requestLoader = requestLoader;
        this.comparer = comparer;
        this.jsonRenderer = jsonRenderer;
    }

    public String compare(final Connection leftConnection, final Connection rightConnection, final Path requestPath) {
        return compare(leftConnection, rightConnection, requestLoader.load(requestPath));
    }

    public String compare(final Connection leftConnection, final Connection rightConnection, final InputStream inputStream) {
        return compare(leftConnection, rightConnection, requestLoader.load(inputStream));
    }

    private String compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final JsonComparisonRequest request) {
        final ComparisonOutputType outputType = request.outputType();
        if (outputType != ComparisonOutputType.JSON) {
            throw new ComparisonRequestException("Unsupported comparison output type: %s".formatted(outputType.jsonValue()));
        }
        return jsonRenderer.render(comparer.compare(leftConnection, rightConnection, request.toMultiTableComparisonRequest()));
    }
}
