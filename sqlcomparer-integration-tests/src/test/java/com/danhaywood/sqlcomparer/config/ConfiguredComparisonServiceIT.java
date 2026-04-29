package com.danhaywood.sqlcomparer.config;

import com.danhaywood.sqlcomparer.comparison.MultiTableComparisonServiceDefault;
import com.danhaywood.sqlcomparer.comparison.TableComparisonServiceDefault;
import com.danhaywood.sqlcomparer.harness.DatabaseSide;
import com.danhaywood.sqlcomparer.harness.SqlServerTestHarness;
import com.danhaywood.sqlcomparer.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.YamlMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.sqlserver.TableMetadataReaderSqlServer;
import com.danhaywood.sqlcomparer.sqlserver.TableRowReaderSqlServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class ConfiguredComparisonServiceIT {

    private static SqlServerTestHarness harness;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConfiguredComparisonService service = new ConfiguredComparisonService(
            new JsonComparisonRequestLoader(objectMapper),
            new MultiTableComparisonServiceDefault(new TableComparisonServiceDefault(new TableMetadataReaderSqlServer(), new TableRowReaderSqlServer())),
            new JsonMultiTableComparisonReportRenderer(objectMapper),
            new YamlMultiTableComparisonReportRenderer(objectMapper),
            new ExcelMultiTableComparisonReportRenderer());

    @BeforeAll
    static void startHarness() {
        harness = new SqlServerTestHarness().start();
    }

    @AfterAll
    static void stopHarness() {
        if (harness != null) {
            harness.close();
        }
    }

    @Test
    void approvesConfiguredJsonComparisonOutput() throws Exception {
        initializeFixture("purchase-order");
        initializeFixture("supplier");
        initializeFixture("product");

        final String json;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT);
             var inputStream = ConfiguredComparisonServiceIT.class.getResourceAsStream("/sql/comparisons/supplier-product.json")) {
            json = service.compare(left, right, inputStream);
        }

        assertThat(json).contains("\"name\" : \"Supplier\"");
        assertThat(json).contains("\"name\" : \"Product\"");
        assertThat(json).contains("\"summary\" : {");
        assertThat(json).contains("\"rowsOnlyInLeftCount\" : 1");
        assertThat(json).contains("\"leftValues\" : {");
        assertThat(json).contains("\"rightValues\" : {");
        assertThat(json).contains("\"reference\" : \"SUP-003\"");
        assertThat(json).contains("\"sku\" : \"SKU-004\"");
        assertThat(json).doesNotContain("PurchaseOrder_PK");
        Approvals.verify(json, new Options().forFile().withExtension(".json"));
    }

    @Test
    void approvesConfiguredYamlComparisonOutput() throws Exception {
        initializeFixture("purchase-order");
        initializeFixture("supplier");
        initializeFixture("product");

        final String yaml;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT);
             var inputStream = ConfiguredComparisonServiceIT.class.getResourceAsStream("/sql/comparisons/supplier-product-yaml.json")) {
            yaml = service.compare(left, right, inputStream);
        }

        assertThat(yaml).contains("hasDifferences: true");
        assertThat(yaml).contains("name: \"Supplier\"");
        assertThat(yaml).contains("name: \"Product\"");
        assertThat(yaml).contains("rowsOnlyInLeftCount: 1");
        assertThat(yaml).contains("leftValues:");
        assertThat(yaml).contains("rightValues:");
        assertThat(yaml).contains("reference: \"SUP-003\"");
        assertThat(yaml).contains("sku: \"SKU-004\"");
        assertThat(yaml).doesNotContain("PurchaseOrder_PK");
        Approvals.verify(yaml, new Options().forFile().withExtension(".yaml"));
    }

    @Test
    void configuredExcelComparisonOutputIsAWorkbook() throws Exception {
        initializeFixture("purchase-order");
        initializeFixture("supplier");
        initializeFixture("product");

        final ConfiguredComparisonOutput output;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT);
             var inputStream = ConfiguredComparisonServiceIT.class.getResourceAsStream("/sql/comparisons/supplier-product-excel.json")) {
            output = service.compareOutput(left, right, inputStream);
        }

        assertThat(output.outputType()).isEqualTo(ComparisonOutputType.EXCEL);
        assertThat(output.mediaType()).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(output.fileExtension()).isEqualTo("xlsx");
        final Path outputPath = Path.of("target", "excel-comparison-output", "supplier-product-comparison.xlsx");
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, output.bytes());
        assertThat(outputPath).exists();

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(output.bytes()))) {
            assertThat(workbook.getSheetName(0)).isEqualTo("Table of Contents");
            assertThat(workbook.getSheetName(1)).isEqualTo("dbo.Supplier");
            assertThat(workbook.getSheetName(2)).isEqualTo("dbo.Product");
            assertThat(workbook.getSheet("Table of Contents").getRow(1).getCell(0).getStringCellValue()).isEqualTo("dbo.Supplier");
            assertThat(workbook.getSheet("Table of Contents").getRow(1).getCell(0).getHyperlink().getAddress()).isEqualTo("'dbo.Supplier'!A1");
            assertThat(workbook.getSheet("Table of Contents").getRow(2).getCell(0).getStringCellValue()).isEqualTo("dbo.Product");
            assertThat(workbook.getSheet("Table of Contents").getRow(2).getCell(0).getHyperlink().getAddress()).isEqualTo("'dbo.Product'!A1");

            final var supplierSheet = workbook.getSheet("dbo.Supplier");
            assertThat(supplierSheet.getRow(6).getCell(0).getStringCellValue()).isEqualTo("Result");
            assertThat(supplierSheet.getRow(6).getCell(1).getStringCellValue()).isEqualTo("reference");
            assertThat(supplierSheet.getRow(7).getCell(1).getStringCellValue()).isEqualTo("<<<");
            assertThat(supplierSheet.getRow(7).getCell(2).getStringCellValue()).isEqualTo(">>>");
            assertThat(supplierSheet.getMergedRegions()).anyMatch(region -> region.formatAsString().equals("A7:A8"));
            assertThat(supplierSheet.getMergedRegions()).anyMatch(region -> region.formatAsString().equals("B7:C7"));
        }
    }

    @Test
    void writesConfiguredComparisonApprovalArtifactsForInspection() throws Exception {
        initializeFixture("purchase-order");
        initializeFixture("supplier");
        initializeFixture("product");

        final String json;
        final ConfiguredComparisonOutput yaml;
        final ConfiguredComparisonOutput excel;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT);
             var jsonInputStream = ConfiguredComparisonServiceIT.class.getResourceAsStream("/sql/comparisons/supplier-product.json");
             var yamlInputStream = ConfiguredComparisonServiceIT.class.getResourceAsStream("/sql/comparisons/supplier-product-yaml.json");
             var excelInputStream = ConfiguredComparisonServiceIT.class.getResourceAsStream("/sql/comparisons/supplier-product-excel.json")) {
            json = service.compare(left, right, jsonInputStream);
            yaml = service.compareOutput(left, right, yamlInputStream);
            excel = service.compareOutput(left, right, excelInputStream);
        }

        final Path outputDirectory = Path.of("target", "comparison-output-approval-files");
        final Path jsonOutputPath = outputDirectory.resolve("supplier-product-comparison.json");
        final Path yamlOutputPath = outputDirectory.resolve("supplier-product-comparison.yaml");
        final Path excelOutputPath = outputDirectory.resolve("supplier-product-comparison.xlsx");
        Files.createDirectories(outputDirectory);
        Files.writeString(jsonOutputPath, json);
        Files.writeString(yamlOutputPath, yaml.contentAsString());
        Files.write(excelOutputPath, excel.bytes());

        assertThat(jsonOutputPath).exists();
        assertThat(yamlOutputPath).exists();
        assertThat(excelOutputPath).exists();
        assertThat(json).contains("\"summary\" : {");
        assertThat(json).contains("\"leftValues\" : {");
        assertThat(json).contains("\"rightValues\" : {");
        assertThat(yaml.outputType()).isEqualTo(ComparisonOutputType.YAML);
        assertThat(excel.outputType()).isEqualTo(ComparisonOutputType.EXCEL);
    }

    private static void initializeFixture(final String fixtureName) {
        initializeFixture(DatabaseSide.LEFT, fixtureName, "/sql/fixtures/%s/left-data.sql".formatted(fixtureName));
        initializeFixture(DatabaseSide.RIGHT, fixtureName, "/sql/fixtures/%s/right-data.sql".formatted(fixtureName));
    }

    private static void initializeFixture(final DatabaseSide side, final String fixtureName, final String dataResourcePath) {
        harness.initializeFromResource(side, "/sql/fixtures/%s/schema.sql".formatted(fixtureName));
        harness.initializeFromResource(side, dataResourcePath);
    }
}
