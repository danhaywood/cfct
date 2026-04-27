package com.danhaywood.sqlcomparer.config;

import com.danhaywood.sqlcomparer.core.MultiTableComparer;
import com.danhaywood.sqlcomparer.core.TableComparer;
import com.danhaywood.sqlcomparer.harness.DatabaseSide;
import com.danhaywood.sqlcomparer.harness.SqlServerTestHarness;
import com.danhaywood.sqlcomparer.report.ExcelMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.sqlserver.SqlServerTableMetadataReader;
import com.danhaywood.sqlcomparer.sqlserver.SqlServerTableRowReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.approvaltests.Approvals;
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
            new MultiTableComparer(new TableComparer(new SqlServerTableMetadataReader(), new SqlServerTableRowReader())),
            new JsonMultiTableComparisonReportRenderer(objectMapper),
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
        assertThat(json).doesNotContain("PurchaseOrder_BK");
        Approvals.verify(json);
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
        }
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
