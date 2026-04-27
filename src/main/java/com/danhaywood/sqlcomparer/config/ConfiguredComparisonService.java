package com.danhaywood.sqlcomparer.config;

import com.danhaywood.sqlcomparer.core.MultiTableComparer;
import com.danhaywood.sqlcomparer.core.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.report.ExcelMultiTableComparisonReportRenderer;
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
    private final ExcelMultiTableComparisonReportRenderer excelRenderer;

    public ConfiguredComparisonService(
            final JsonComparisonRequestLoader requestLoader,
            final MultiTableComparer comparer,
            final JsonMultiTableComparisonReportRenderer jsonRenderer,
            final ExcelMultiTableComparisonReportRenderer excelRenderer) {
        this.requestLoader = requestLoader;
        this.comparer = comparer;
        this.jsonRenderer = jsonRenderer;
        this.excelRenderer = excelRenderer;
    }

    public String compare(final Connection leftConnection, final Connection rightConnection, final Path requestPath) {
        return compareOutput(leftConnection, rightConnection, requestPath).contentAsString();
    }

    public String compare(final Connection leftConnection, final Connection rightConnection, final InputStream inputStream) {
        return compareOutput(leftConnection, rightConnection, inputStream).contentAsString();
    }

    public ConfiguredComparisonOutput compareOutput(final Connection leftConnection, final Connection rightConnection, final Path requestPath) {
        return compareOutput(leftConnection, rightConnection, requestLoader.load(requestPath));
    }

    public ConfiguredComparisonOutput compareOutput(final Connection leftConnection, final Connection rightConnection, final InputStream inputStream) {
        return compareOutput(leftConnection, rightConnection, requestLoader.load(inputStream));
    }

    private ConfiguredComparisonOutput compareOutput(
            final Connection leftConnection,
            final Connection rightConnection,
            final JsonComparisonRequest request) {
        final MultiTableComparisonResult result = comparer.compare(leftConnection, rightConnection, request.toMultiTableComparisonRequest());
        return switch (request.outputType()) {
            case JSON -> ConfiguredComparisonOutput.json(jsonRenderer.render(result));
            case EXCEL -> ConfiguredComparisonOutput.excel(excelRenderer.render(result));
        };
    }
}
