package com.danhaywood.cfct.config;

import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.YamlMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;

@Service
public final class ConfiguredComparisonService {

    private final JsonComparisonRequestLoader requestLoader;
    private final MultiTableComparisonService comparer;
    private final JsonMultiTableComparisonReportRenderer jsonRenderer;
    private final YamlMultiTableComparisonReportRenderer yamlRenderer;
    private final ExcelMultiTableComparisonReportRenderer excelRenderer;

    public ConfiguredComparisonService(
            final JsonComparisonRequestLoader requestLoader,
            final MultiTableComparisonService comparer,
            final JsonMultiTableComparisonReportRenderer jsonRenderer,
            final YamlMultiTableComparisonReportRenderer yamlRenderer,
            final ExcelMultiTableComparisonReportRenderer excelRenderer) {
        this.requestLoader = requestLoader;
        this.comparer = comparer;
        this.jsonRenderer = jsonRenderer;
        this.yamlRenderer = yamlRenderer;
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
            case YAML -> ConfiguredComparisonOutput.yaml(yamlRenderer.render(result));
            case EXCEL -> ConfiguredComparisonOutput.excel(excelRenderer.render(result));
        };
    }
}
