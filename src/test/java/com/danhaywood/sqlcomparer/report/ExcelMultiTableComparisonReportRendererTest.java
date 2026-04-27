package com.danhaywood.sqlcomparer.report;

import com.danhaywood.sqlcomparer.core.BusinessKey;
import com.danhaywood.sqlcomparer.core.ColumnDifference;
import com.danhaywood.sqlcomparer.core.ColumnRef;
import com.danhaywood.sqlcomparer.core.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.core.RowDifference;
import com.danhaywood.sqlcomparer.core.RowKey;
import com.danhaywood.sqlcomparer.core.TableComparisonResult;
import com.danhaywood.sqlcomparer.core.TableRef;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelMultiTableComparisonReportRendererTest {

    private final ExcelMultiTableComparisonReportRenderer renderer = new ExcelMultiTableComparisonReportRenderer();

    @Test
    void rendersWorkbookWithTableOfContentsAndTableSheets() throws Exception {
        final byte[] bytes = renderer.render(result(
                tableResult("dbo", "Supplier", true),
                tableResult("dbo", "Product", false)));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
            assertThat(workbook.getSheetName(0)).isEqualTo("Table of Contents");
            assertThat(workbook.getSheetName(1)).isEqualTo("dbo.Supplier");
            assertThat(workbook.getSheetName(2)).isEqualTo("dbo.Product");

            final var contents = workbook.getSheet("Table of Contents");
            assertThat(contents.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Schema");
            assertThat(contents.getRow(1).getCell(0).getStringCellValue()).isEqualTo("dbo");
            assertThat(contents.getRow(1).getCell(1).getStringCellValue()).isEqualTo("Supplier");
            assertThat(contents.getRow(1).getCell(2).getNumericCellValue()).isEqualTo(2);
            assertThat(contents.getRow(1).getCell(4).getNumericCellValue()).isEqualTo(1);
            assertThat(contents.getRow(1).getCell(5).getNumericCellValue()).isEqualTo(1);
            assertThat(contents.getRow(1).getCell(6).getNumericCellValue()).isEqualTo(1);
            assertThat(contents.getRow(1).getCell(7).getBooleanCellValue()).isTrue();
            assertThat(contents.getRow(2).getCell(7).getBooleanCellValue()).isFalse();
        }
    }

    @Test
    void rendersTableMetadataAndDifferences() throws Exception {
        final byte[] bytes = renderer.render(result(tableResult("dbo", "Supplier", true)));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            final var sheet = workbook.getSheet("dbo.Supplier");
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Table");
            assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("dbo.Supplier");
            assertThat(sheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo("Supplier_BK");
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo("reference");
            assertThat(sheet.getRow(3).getCell(1).getStringCellValue()).isEqualTo("name, status");
            assertThat(sheet.getRow(4).getCell(1).getStringCellValue()).isEqualTo("id");
            assertThat(sheet.getRow(8).getCell(0).getStringCellValue()).isEqualTo("SUP-LEFT");
            assertThat(sheet.getRow(12).getCell(0).getStringCellValue()).isEqualTo("SUP-RIGHT");
            assertThat(sheet.getRow(16).getCell(0).getStringCellValue()).isEqualTo("SUP-DIFF");
            assertThat(sheet.getRow(16).getCell(1).getStringCellValue()).isEqualTo("status");
            assertThat(sheet.getRow(16).getCell(2).getStringCellValue()).isEqualTo("ACTIVE");
            assertThat(sheet.getRow(16).getCell(3).getStringCellValue()).isEqualTo("INACTIVE");
        }
    }

    @Test
    void writesSampleWorkbookForManualInspection() throws Exception {
        final byte[] bytes = renderer.render(result(
                tableResult("dbo", "Supplier", true),
                tableResult("dbo", "Product", false)));

        final Path outputPath = Path.of("target", "excel-comparison-output", "sample-comparison.xlsx");
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, bytes);

        assertThat(outputPath).exists();
    }

    @Test
    void rendersSafeDeterministicSheetNames() throws Exception {
        final byte[] bytes = renderer.render(result(
                tableResult("dbo", "Table/With*Invalid:Characters?AndVeryLongName", false),
                tableResult("dbo", "Table/With*Invalid:Characters?AndVeryLongName", false)));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertThat(workbook.getSheetName(1)).isEqualTo("dbo.Table With Invalid Characte");
            assertThat(workbook.getSheetName(2)).isEqualTo("dbo.Table With Invalid Char (2)");
        }
    }

    private MultiTableComparisonResult result(final TableComparisonResult... tableResults) {
        return new MultiTableComparisonResult(List.of(tableResults));
    }

    private TableComparisonResult tableResult(final String schemaName, final String tableName, final boolean withDifferences) {
        return new TableComparisonResult(
                new TableRef(schemaName, tableName),
                new BusinessKey(tableName + "_BK", List.of(new ColumnRef("reference"))),
                List.of(new ColumnRef("name"), new ColumnRef("status")),
                List.of(new ColumnRef("id")),
                withDifferences ? List.of(new RowKey(List.of("SUP-LEFT"))) : List.of(),
                withDifferences ? List.of(new RowKey(List.of("SUP-RIGHT"))) : List.of(),
                withDifferences
                        ? List.of(new RowDifference(
                        new RowKey(List.of("SUP-DIFF")),
                        List.of(new ColumnDifference(new ColumnRef("status"), "ACTIVE", "INACTIVE"))))
                        : List.of());
    }
}
