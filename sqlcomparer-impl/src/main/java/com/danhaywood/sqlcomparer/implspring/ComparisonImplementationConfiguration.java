package com.danhaywood.sqlcomparer.implspring;

import com.danhaywood.sqlcomparer.comparison.MultiTableComparisonServiceDefault;
import com.danhaywood.sqlcomparer.comparison.MultiTableComparisonViewServiceDefault;
import com.danhaywood.sqlcomparer.comparison.TableComparisonServiceDefault;
import com.danhaywood.sqlcomparer.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.MultiTableComparisonReportFormatterDefault;
import com.danhaywood.sqlcomparer.report.TextMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.YamlMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.TextTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonReportFormatter;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonViewService;
import com.danhaywood.sqlcomparer.service.TableComparisonService;
import com.danhaywood.sqlcomparer.spi.CommandAuditTouchedTableResolver;
import com.danhaywood.sqlcomparer.spi.TableMetadataReader;
import com.danhaywood.sqlcomparer.spi.TableRowReader;
import com.danhaywood.sqlcomparer.sqlserver.CommandAuditTouchedTableResolverSqlServer;
import com.danhaywood.sqlcomparer.sqlserver.TableMetadataReaderSqlServer;
import com.danhaywood.sqlcomparer.sqlserver.TableRowReaderSqlServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComparisonImplementationConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TableMetadataReader tableMetadataReader() {
        return new TableMetadataReaderSqlServer();
    }

    @Bean
    public TableRowReader tableRowReader() {
        return new TableRowReaderSqlServer();
    }

    @Bean
    public CommandAuditTouchedTableResolver commandAuditTouchedTableResolver() {
        return new CommandAuditTouchedTableResolverSqlServer();
    }

    @Bean
    public TableComparisonService tableComparisonService(
            final TableMetadataReader tableMetadataReader,
            final TableRowReader tableRowReader) {
        return new TableComparisonServiceDefault(tableMetadataReader, tableRowReader);
    }

    @Bean
    public MultiTableComparisonService multiTableComparisonService(final TableComparisonService tableComparisonService) {
        return new MultiTableComparisonServiceDefault(tableComparisonService);
    }

    @Bean
    public MultiTableComparisonViewService multiTableComparisonViewService(
            final MultiTableComparisonService multiTableComparisonService) {
        return new MultiTableComparisonViewServiceDefault(multiTableComparisonService);
    }

    @Bean
    public TextTableComparisonReportRenderer textTableComparisonReportRenderer() {
        return new TextTableComparisonReportRenderer();
    }

    @Bean
    public TextMultiTableComparisonReportRenderer textMultiTableComparisonReportRenderer(
            final TextTableComparisonReportRenderer textTableComparisonReportRenderer) {
        return new TextMultiTableComparisonReportRenderer(textTableComparisonReportRenderer);
    }

    @Bean
    public JsonMultiTableComparisonReportRenderer jsonMultiTableComparisonReportRenderer(final ObjectMapper objectMapper) {
        return new JsonMultiTableComparisonReportRenderer(objectMapper);
    }

    @Bean
    public YamlMultiTableComparisonReportRenderer yamlMultiTableComparisonReportRenderer(final ObjectMapper objectMapper) {
        return new YamlMultiTableComparisonReportRenderer(objectMapper);
    }

    @Bean
    public ExcelMultiTableComparisonReportRenderer excelMultiTableComparisonReportRenderer() {
        return new ExcelMultiTableComparisonReportRenderer();
    }

    @Bean
    public MultiTableComparisonReportFormatter multiTableComparisonReportFormatter(
            final TextMultiTableComparisonReportRenderer textRenderer,
            final JsonMultiTableComparisonReportRenderer jsonRenderer,
            final YamlMultiTableComparisonReportRenderer yamlRenderer,
            final ExcelMultiTableComparisonReportRenderer excelRenderer) {
        return new MultiTableComparisonReportFormatterDefault(textRenderer, jsonRenderer, yamlRenderer, excelRenderer);
    }
}
