package com.danhaywood.sqlcomparer.report;

import com.danhaywood.sqlcomparer.core.BusinessKey;
import com.danhaywood.sqlcomparer.core.ColumnDifference;
import com.danhaywood.sqlcomparer.core.ColumnRef;
import com.danhaywood.sqlcomparer.core.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.core.RowDifference;
import com.danhaywood.sqlcomparer.core.RowKey;
import com.danhaywood.sqlcomparer.core.TableComparisonResult;
import com.danhaywood.sqlcomparer.core.TableRef;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
            assertThat(contents.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Table");
            assertThat(contents.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Compared Columns");
            assertThat(contents.getRow(1).getCell(0).getStringCellValue()).isEqualTo("dbo.Supplier");
            assertThat(contents.getRow(1).getCell(0).getHyperlink().getAddress()).isEqualTo("'dbo.Supplier'!A1");
            assertThat(contents.getRow(1).getCell(1).getNumericCellValue()).isEqualTo(2);
            assertThat(contents.getRow(1).getCell(3).getNumericCellValue()).isEqualTo(1);
            assertThat(contents.getRow(1).getCell(4).getNumericCellValue()).isEqualTo(1);
            assertThat(contents.getRow(1).getCell(5).getNumericCellValue()).isEqualTo(1);
            assertThat(contents.getRow(1).getCell(6).getBooleanCellValue()).isTrue();
            assertThat(contents.getRow(2).getCell(0).getStringCellValue()).isEqualTo("dbo.Product");
            assertThat(contents.getRow(2).getCell(0).getHyperlink().getAddress()).isEqualTo("'dbo.Product'!A1");
            assertThat(contents.getRow(2).getCell(6).getBooleanCellValue()).isFalse();
            assertThat(contents.getPaneInformation().getVerticalSplitPosition()).isEqualTo((short) 1);
            assertThat(contents.getPaneInformation().getHorizontalSplitPosition()).isEqualTo((short) 1);
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
            assertThat(sheet.getRow(6).getCell(0).getStringCellValue()).isEqualTo("Difference Type");
            assertThat(sheet.getRow(6).getCell(1).getStringCellValue()).isEqualTo("reference");
            assertThat(sheet.getRow(6).getCell(2).getStringCellValue()).isEqualTo("name");
            assertThat(sheet.getRow(6).getCell(3).getStringCellValue()).isEqualTo("status");
            assertThat(sheet.getRow(7).getCell(0).getStringCellValue()).isEqualTo("Only in left");
            assertThat(sheet.getRow(7).getCell(1).getStringCellValue()).isEqualTo("SUP-LEFT");
            assertThat(sheet.getRow(7).getCell(2).getStringCellValue()).isEqualTo("Left-only supplier");
            assertThat(sheet.getRow(7).getCell(2).getCellStyle().getFillForegroundColor()).isEqualTo(IndexedColors.LIGHT_YELLOW.getIndex());
            assertThat(sheet.getRow(8).getCell(0).getStringCellValue()).isEqualTo("Only in right");
            assertThat(sheet.getRow(8).getCell(1).getStringCellValue()).isEqualTo("SUP-RIGHT");
            assertThat(sheet.getRow(8).getCell(2).getStringCellValue()).isEqualTo("Right-only supplier");
            assertThat(sheet.getRow(8).getCell(2).getCellStyle().getFillForegroundColor()).isEqualTo(IndexedColors.YELLOW.getIndex());
            assertThat(sheet.getRow(9).getCell(0).getStringCellValue()).isEqualTo("Left");
            assertThat(sheet.getRow(9).getCell(1).getStringCellValue()).isEqualTo("SUP-DIFF");
            assertThat(sheet.getRow(9).getCell(2).getCellStyle().getFillForegroundColor()).isEqualTo(IndexedColors.LIGHT_GREEN.getIndex());
            assertThat(sheet.getRow(9).getCell(3).getStringCellValue()).isEqualTo("ACTIVE");
            assertThat(sheet.getRow(10).getCell(0).getStringCellValue()).isEqualTo("Right");
            assertThat(sheet.getRow(10).getCell(1).getStringCellValue()).isEqualTo("SUP-DIFF");
            assertThat(sheet.getRow(10).getCell(3).getStringCellValue()).isEqualTo("INACTIVE");
            assertThat(sheet.getRow(9).getCell(3).getCellStyle().getFillForegroundColor()).isEqualTo(IndexedColors.ROSE.getIndex());
            assertThat(sheet.getRow(10).getCell(3).getCellStyle().getFillForegroundColor()).isEqualTo(IndexedColors.ROSE.getIndex());
            assertThat(sheet.getPaneInformation().getVerticalSplitPosition()).isEqualTo((short) 2);
            assertThat(sheet.getPaneInformation().getHorizontalSplitPosition()).isEqualTo((short) 7);
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
        final ColumnRef name = new ColumnRef("name");
        final ColumnRef status = new ColumnRef("status");
        final RowKey leftOnly = new RowKey(List.of("SUP-LEFT"));
        final RowKey rightOnly = new RowKey(List.of("SUP-RIGHT"));
        final RowKey differing = new RowKey(List.of("SUP-DIFF"));
        final Map<ColumnRef, String> leftOnlyValues = Map.of(name, "Left-only supplier", status, "ACTIVE");
        final Map<ColumnRef, String> rightOnlyValues = Map.of(name, "Right-only supplier", status, "ACTIVE");
        final Map<ColumnRef, String> differingLeftValues = Map.of(name, "Shared supplier", status, "ACTIVE");
        final Map<ColumnRef, String> differingRightValues = Map.of(name, "Shared supplier", status, "INACTIVE");

        return new TableComparisonResult(
                new TableRef(schemaName, tableName),
                new BusinessKey(tableName + "_BK", List.of(new ColumnRef("reference"))),
                List.of(name, status),
                List.of(new ColumnRef("id")),
                withDifferences ? List.of(leftOnly) : List.of(),
                withDifferences ? List.of(rightOnly) : List.of(),
                withDifferences
                        ? List.of(new RowDifference(
                        differing,
                        differingLeftValues,
                        differingRightValues,
                        List.of(new ColumnDifference(status, "ACTIVE", "INACTIVE"))))
                        : List.of(),
                withDifferences ? Map.of(leftOnly, leftOnlyValues) : Map.of(),
                withDifferences ? Map.of(rightOnly, rightOnlyValues) : Map.of());
    }
}
