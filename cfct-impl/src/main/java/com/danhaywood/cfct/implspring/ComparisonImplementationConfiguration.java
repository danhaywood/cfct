package com.danhaywood.cfct.implspring;

import com.danhaywood.cfct.comparison.MultiTableComparisonServiceDefault;
import com.danhaywood.cfct.comparison.MultiTableComparisonViewServiceDefault;
import com.danhaywood.cfct.comparison.TableComparisonServiceDefault;
import com.danhaywood.cfct.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.MultiTableComparisonReportFormatterDefault;
import com.danhaywood.cfct.report.TextMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.YamlMultiTableComparisonReportRenderer;
import com.danhaywood.cfct.report.TextTableComparisonReportRenderer;
import com.danhaywood.cfct.service.MultiTableComparisonReportFormatter;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.service.MultiTableComparisonViewService;
import com.danhaywood.cfct.service.TableComparisonService;
import com.danhaywood.cfct.spi.CommandAuditTouchedTableResolver;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;
import com.danhaywood.cfct.spi.TableMetadataReader;
import com.danhaywood.cfct.spi.TableRowReader;
import com.danhaywood.cfct.sqlserver.CommandAuditTouchedTableResolverSqlServer;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorForIdentityColumns;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorForTimestamps;
import com.danhaywood.cfct.sqlserver.IgnoreColumnAdvisorForUuidColumns;
import com.danhaywood.cfct.sqlserver.TableMetadataReaderSqlServer;
import com.danhaywood.cfct.sqlserver.TableRowReaderSqlServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(IgnoreColumnAdvisorsProperties.class)
public class ComparisonImplementationConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TableMetadataReader tableMetadataReader(final List<IgnoreColumnAdvisor> ignoreColumnAdvisors) {
        return new TableMetadataReaderSqlServer(ignoreColumnAdvisors);
    }

    @Bean
    public IgnoreColumnAdvisor ignoreColumnAdvisorForIdentityColumns(
            final IgnoreColumnAdvisorsProperties properties) {
        return new IgnoreColumnAdvisorForIdentityColumns(properties.isIdentityEnabled());
    }

    @Bean
    public IgnoreColumnAdvisor ignoreColumnAdvisorForUuidColumns(
            final IgnoreColumnAdvisorsProperties properties) {
        return new IgnoreColumnAdvisorForUuidColumns(properties.isUuidEnabled());
    }

    @Bean
    public IgnoreColumnAdvisor ignoreColumnAdvisorForTimestamps(
            final IgnoreColumnAdvisorsProperties properties) {
        return new IgnoreColumnAdvisorForTimestamps(properties.isTimestampsEnabled());
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
