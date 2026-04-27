package com.danhaywood.sqlcomparer.report;

import com.danhaywood.sqlcomparer.core.ColumnDifference;
import com.danhaywood.sqlcomparer.core.ColumnRef;
import com.danhaywood.sqlcomparer.core.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.core.RowDifference;
import com.danhaywood.sqlcomparer.core.RowKey;
import com.danhaywood.sqlcomparer.core.TableComparisonResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class ExcelMultiTableComparisonReportRenderer {

    private static final String TABLE_OF_CONTENTS = "Table of Contents";
    private static final int MAX_SHEET_NAME_LENGTH = 31;

    public byte[] render(final MultiTableComparisonResult result) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            createTableOfContentsSheet(workbook, result);
            createTableSheets(workbook, result);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to render comparison result as Excel", ex);
        }
    }

    private void createTableOfContentsSheet(final Workbook workbook, final MultiTableComparisonResult result) {
        final Sheet sheet = workbook.createSheet(TABLE_OF_CONTENTS);
        int rowIndex = 0;
        writeRow(sheet, rowIndex++, "Schema", "Table", "Compared Columns", "Ignored Columns", "Rows Only In Left", "Rows Only In Right", "Differing Rows", "Has Differences");
        for (TableComparisonResult tableResult : result.tableResults()) {
            writeRow(sheet, rowIndex++,
                    tableResult.table().schemaName(),
                    tableResult.table().tableName(),
                    tableResult.comparedColumns().size(),
                    tableResult.ignoredColumns().size(),
                    tableResult.rowsOnlyInLeft().size(),
                    tableResult.rowsOnlyInRight().size(),
                    tableResult.differingRows().size(),
                    tableResult.hasDifferences());
        }
        autosizeColumns(sheet, 8);
    }

    private void createTableSheets(final Workbook workbook, final MultiTableComparisonResult result) {
        final Set<String> usedNames = new HashSet<>();
        usedNames.add(TABLE_OF_CONTENTS);
        for (TableComparisonResult tableResult : result.tableResults()) {
            final Sheet sheet = workbook.createSheet(safeSheetName(tableResult.table().displayName(), usedNames));
            populateTableSheet(sheet, tableResult);
        }
    }

    private void populateTableSheet(final Sheet sheet, final TableComparisonResult result) {
        int rowIndex = 0;
        writeRow(sheet, rowIndex++, "Table", result.table().displayName());
        writeRow(sheet, rowIndex++, "Business Key Index", result.businessKey().indexName());
        writeRow(sheet, rowIndex++, "Business Key Columns", columnNames(result.businessKey().columns()));
        writeRow(sheet, rowIndex++, "Compared Columns", columnNames(result.comparedColumns()));
        writeRow(sheet, rowIndex++, "Ignored Columns", columnNames(result.ignoredColumns()));
        rowIndex++;

        rowIndex = writeRowKeysSection(sheet, rowIndex, "Rows Only In Left", result.rowsOnlyInLeft());
        rowIndex = writeRowKeysSection(sheet, rowIndex, "Rows Only In Right", result.rowsOnlyInRight());
        writeDifferingRowsSection(sheet, rowIndex, result.differingRows());
        autosizeColumns(sheet, 5);
    }

    private int writeRowKeysSection(final Sheet sheet, final int startRowIndex, final String title, final List<RowKey> rowKeys) {
        int rowIndex = startRowIndex;
        writeRow(sheet, rowIndex++, title);
        writeRow(sheet, rowIndex++, "Key");
        if (rowKeys.isEmpty()) {
            writeRow(sheet, rowIndex++, "<none>");
        } else {
            for (RowKey rowKey : rowKeys) {
                writeRow(sheet, rowIndex++, rowKey.display());
            }
        }
        return rowIndex + 1;
    }

    private void writeDifferingRowsSection(final Sheet sheet, final int startRowIndex, final List<RowDifference> differingRows) {
        int rowIndex = startRowIndex;
        writeRow(sheet, rowIndex++, "Differing Rows");
        writeRow(sheet, rowIndex++, "Key", "Column", "Left", "Right");
        if (differingRows.isEmpty()) {
            writeRow(sheet, rowIndex, "<none>");
            return;
        }
        for (RowDifference rowDifference : differingRows) {
            for (ColumnDifference columnDifference : rowDifference.columnDifferences()) {
                writeRow(sheet, rowIndex++,
                        rowDifference.key().display(),
                        columnDifference.column().name(),
                        columnDifference.leftValue(),
                        columnDifference.rightValue());
            }
        }
    }

    private String safeSheetName(final String requestedName, final Set<String> usedNames) {
        final String baseName = truncate(WorkbookUtil.createSafeSheetName(requestedName), MAX_SHEET_NAME_LENGTH);
        String candidate = baseName;
        int suffix = 2;
        while (usedNames.contains(candidate)) {
            final String suffixText = " (" + suffix++ + ")";
            candidate = truncate(baseName, MAX_SHEET_NAME_LENGTH - suffixText.length()) + suffixText;
        }
        usedNames.add(candidate);
        return candidate;
    }

    private String truncate(final String value, final int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String columnNames(final List<ColumnRef> columns) {
        return columns.stream().map(ColumnRef::name).collect(Collectors.joining(", "));
    }

    private void writeRow(final Sheet sheet, final int rowIndex, final Object... values) {
        final Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
            if (values[columnIndex] instanceof Number number) {
                row.createCell(columnIndex).setCellValue(number.doubleValue());
            } else if (values[columnIndex] instanceof Boolean bool) {
                row.createCell(columnIndex).setCellValue(bool);
            } else {
                row.createCell(columnIndex).setCellValue(values[columnIndex] == null ? "" : values[columnIndex].toString());
            }
        }
    }

    private void autosizeColumns(final Sheet sheet, final int columnCount) {
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }
}
