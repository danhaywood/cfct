package com.danhaywood.sqlcomparer.report;

import com.danhaywood.sqlcomparer.model.ColumnDifference;
import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.RowDifference;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class ExcelMultiTableComparisonReportRenderer {

    private static final String TABLE_OF_CONTENTS = "Table of Contents";
    private static final int MAX_SHEET_NAME_LENGTH = 31;

    public byte[] render(final MultiTableComparisonResult result) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final Styles styles = createStyles(workbook);
            final List<TableSheet> tableSheets = tableSheets(result);
            createTableOfContentsSheet(workbook, styles, tableSheets);
            createTableSheets(workbook, styles, tableSheets);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to render comparison result as Excel", ex);
        }
    }

    private void createTableOfContentsSheet(final Workbook workbook, final Styles styles, final List<TableSheet> tableSheets) {
        final Sheet sheet = workbook.createSheet(TABLE_OF_CONTENTS);
        final CreationHelper creationHelper = workbook.getCreationHelper();
        sheet.createFreezePane(1, 1);
        int rowIndex = 0;
        writeRow(sheet, rowIndex++, styles.header(), "Table", "Compared Columns", "Ignored Columns", "Rows Only In Left", "Rows Only In Right", "Differing Rows", "Has Differences");
        for (TableSheet tableSheet : tableSheets) {
            final TableComparisonResult tableResult = tableSheet.result();
            final Row row = sheet.createRow(rowIndex++);
            final Cell tableCell = writeCell(row, 0, tableResult.table().displayName(), styles.hyperlink());
            final Hyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
            hyperlink.setAddress("'" + tableSheet.sheetName().replace("'", "''") + "'!A1");
            tableCell.setHyperlink(hyperlink);
            writeCell(row, 1, tableResult.comparedColumns().size(), null);
            writeCell(row, 2, tableResult.ignoredColumns().size(), null);
            writeCell(row, 3, tableResult.rowsOnlyInLeft().size(), null);
            writeCell(row, 4, tableResult.rowsOnlyInRight().size(), null);
            writeCell(row, 5, tableResult.differingRows().size(), null);
            writeCell(row, 6, tableResult.hasDifferences(), null);
        }
        autosizeColumns(sheet, 7);
    }

    private void createTableSheets(final Workbook workbook, final Styles styles, final List<TableSheet> tableSheets) {
        for (TableSheet tableSheet : tableSheets) {
            final Sheet sheet = workbook.createSheet(tableSheet.sheetName());
            populateTableSheet(sheet, styles, tableSheet.result());
        }
    }

    private List<TableSheet> tableSheets(final MultiTableComparisonResult result) {
        final Set<String> usedNames = new HashSet<>();
        usedNames.add(TABLE_OF_CONTENTS);
        final List<TableSheet> tableSheets = new ArrayList<>();
        for (TableComparisonResult tableResult : result.tableResults()) {
            tableSheets.add(new TableSheet(tableResult, safeSheetName(tableResult.table().displayName(), usedNames)));
        }
        return List.copyOf(tableSheets);
    }

    private void populateTableSheet(final Sheet sheet, final Styles styles, final TableComparisonResult result) {
        int rowIndex = 0;
        writeRow(sheet, rowIndex++, styles.metadata(), "Table", result.table().displayName());
        writeRow(sheet, rowIndex++, styles.metadata(), "Business Key Index", result.businessKey().indexName());
        writeRow(sheet, rowIndex++, styles.metadata(), "Business Key Columns", columnNames(result.businessKey().columns()));
        writeRow(sheet, rowIndex++, styles.metadata(), "Compared Columns", columnNames(result.comparedColumns()));
        writeRow(sheet, rowIndex++, styles.metadata(), "Ignored Columns", columnNames(result.ignoredColumns()));
        rowIndex++;

        final List<ColumnRef> keyColumns = result.businessKey().columns();
        final List<ColumnRef> displayedColumns = java.util.stream.Stream.concat(keyColumns.stream(), result.comparedColumns().stream()).toList();
        final int topHeaderRowIndex = rowIndex++;
        final int bottomHeaderRowIndex = rowIndex++;
        final Row topHeaderRow = sheet.createRow(topHeaderRowIndex);
        final Row bottomHeaderRow = sheet.createRow(bottomHeaderRowIndex);
        writeCell(topHeaderRow, 0, "Result", styles.header());
        writeCell(bottomHeaderRow, 0, "", styles.header());
        sheet.addMergedRegion(new CellRangeAddress(topHeaderRowIndex, bottomHeaderRowIndex, 0, 0));

        int columnIndex = 1;
        for (ColumnRef displayedColumn : displayedColumns) {
            final int leftColumnIndex = columnIndex++;
            final int rightColumnIndex = columnIndex++;
            writeCell(topHeaderRow, leftColumnIndex, displayedColumn.name(), styles.header());
            writeCell(topHeaderRow, rightColumnIndex, "", styles.header());
            sheet.addMergedRegion(new CellRangeAddress(topHeaderRowIndex, topHeaderRowIndex, leftColumnIndex, rightColumnIndex));
            writeCell(bottomHeaderRow, leftColumnIndex, "<<<", styles.header());
            writeCell(bottomHeaderRow, rightColumnIndex, ">>>", styles.header());
        }

        for (RowKey rowKey : result.rowsOnlyInLeft()) {
            rowIndex = writeActualRow(sheet, rowIndex, styles.onlyInLeft(), styles.onlyInLeft(), "Only in left", rowKey, result.rowsOnlyInLeftValues().get(rowKey), null, keyColumns, result.comparedColumns(), Set.of());
        }
        for (RowKey rowKey : result.rowsOnlyInRight()) {
            rowIndex = writeActualRow(sheet, rowIndex, styles.onlyInRight(), styles.onlyInRight(), "Only in right", rowKey, null, result.rowsOnlyInRightValues().get(rowKey), keyColumns, result.comparedColumns(), Set.of());
        }
        for (RowDifference rowDifference : result.differingRows()) {
            final Set<ColumnRef> changedColumns = rowDifference.columnDifferences().stream()
                    .map(ColumnDifference::column)
                    .collect(Collectors.toSet());
            rowIndex = writeActualRow(sheet, rowIndex, styles.present(), styles.changed(), "Differ", rowDifference.key(), rowDifference.leftValues(), rowDifference.rightValues(), keyColumns, result.comparedColumns(), changedColumns);
        }
        if (!result.hasDifferences()) {
            writeRow(sheet, rowIndex, styles.present(), "No differences found");
        }

        sheet.createFreezePane(1 + (keyColumns.size() * 2), bottomHeaderRowIndex + 1);
        autosizeColumns(sheet, 1 + (displayedColumns.size() * 2));
    }

    private int writeActualRow(
            final Sheet sheet,
            final int rowIndex,
            final CellStyle rowStyle,
            final CellStyle changedStyle,
            final String differenceType,
            final RowKey rowKey,
            final Map<ColumnRef, String> leftValues,
            final Map<ColumnRef, String> rightValues,
            final List<ColumnRef> keyColumns,
            final List<ColumnRef> comparedColumns,
            final Set<ColumnRef> changedColumns) {
        final Row row = sheet.createRow(rowIndex);
        writeCell(row, 0, differenceType, rowStyle);
        int columnIndex = 1;
        for (int keyIndex = 0; keyIndex < keyColumns.size(); keyIndex++) {
            final String keyValue = keyIndex < rowKey.values().size() ? rowKey.values().get(keyIndex) : "";
            writeCell(row, columnIndex++, leftValues == null ? "" : keyValue, rowStyle);
            writeCell(row, columnIndex++, rightValues == null ? "" : keyValue, rowStyle);
        }
        for (ColumnRef comparedColumn : comparedColumns) {
            final CellStyle valueStyle = changedColumns.contains(comparedColumn) ? changedStyle : rowStyle;
            writeCell(row, columnIndex++, value(leftValues, comparedColumn), valueStyle);
            writeCell(row, columnIndex++, value(rightValues, comparedColumn), valueStyle);
        }
        return rowIndex + 1;
    }

    private String value(final Map<ColumnRef, String> values, final ColumnRef column) {
        if (values == null) {
            return "";
        }
        return values.getOrDefault(column, "");
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

    private void writeRow(final Sheet sheet, final int rowIndex, final CellStyle style, final Object... values) {
        final Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
            writeCell(row, columnIndex, values[columnIndex], style);
        }
    }

    private Cell writeCell(final Row row, final int columnIndex, final Object value, final CellStyle style) {
        final Cell cell = row.createCell(columnIndex);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof Boolean bool) {
            cell.setCellValue(bool);
        } else {
            cell.setCellValue(value == null ? "" : value.toString());
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    private void autosizeColumns(final Sheet sheet, final int columnCount) {
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    private Styles createStyles(final Workbook workbook) {
        final CellStyle header = workbook.createCellStyle();
        header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle metadata = workbook.createCellStyle();
        metadata.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        metadata.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle changed = workbook.createCellStyle();
        changed.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        changed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle present = workbook.createCellStyle();
        present.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        present.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle onlyInLeft = workbook.createCellStyle();
        onlyInLeft.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        onlyInLeft.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle onlyInRight = workbook.createCellStyle();
        onlyInRight.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        onlyInRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle hyperlink = workbook.createCellStyle();
        final var hyperlinkFont = workbook.createFont();
        hyperlinkFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
        hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());
        hyperlink.setFont(hyperlinkFont);

        return new Styles(header, metadata, changed, present, onlyInLeft, onlyInRight, hyperlink);
    }

    private record TableSheet(TableComparisonResult result, String sheetName) {
    }

    private record Styles(
            CellStyle header,
            CellStyle metadata,
            CellStyle changed,
            CellStyle present,
            CellStyle onlyInLeft,
            CellStyle onlyInRight,
            CellStyle hyperlink
    ) {
    }
}
