package com.danhaywood.sqlcomparer.report;

import com.danhaywood.sqlcomparer.model.ColumnDifference;
import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.RowDifference;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public final class JsonMultiTableComparisonReportRenderer {

    private final ObjectMapper objectMapper;

    public JsonMultiTableComparisonReportRenderer(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String render(final MultiTableComparisonResult result) {
        try {
            return objectMapper.writeValueAsString(toJsonModel(result)) + System.lineSeparator();
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to render comparison result as JSON", ex);
        }
    }

    private Map<String, Object> toJsonModel(final MultiTableComparisonResult result) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("hasDifferences", result.hasDifferences());
        model.put("tables", result.tableResults().stream().map(this::toTableModel).toList());
        return model;
    }

    private Map<String, Object> toTableModel(final TableComparisonResult result) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("table", tableModel(result));
        model.put("summary", summaryModel(result));
        model.put("businessKey", businessKeyModel(result));
        model.put("comparedColumns", columnNames(result.comparedColumns()));
        model.put("ignoredColumns", columnNames(result.ignoredColumns()));
        model.put("rowsOnlyInLeft", result.rowsOnlyInLeft().stream().map(rowKey -> toMissingRowModel(
                rowKey,
                result.rowsOnlyInLeftValues().get(rowKey),
                null,
                result.businessKey().columns(),
                result.comparedColumns())).toList());
        model.put("rowsOnlyInRight", result.rowsOnlyInRight().stream().map(rowKey -> toMissingRowModel(
                rowKey,
                null,
                result.rowsOnlyInRightValues().get(rowKey),
                result.businessKey().columns(),
                result.comparedColumns())).toList());
        model.put("differingRows", result.differingRows().stream().map(rowDifference -> toRowDifferenceModel(
                rowDifference,
                result.businessKey().columns(),
                result.comparedColumns())).toList());
        return model;
    }

    private Map<String, Object> summaryModel(final TableComparisonResult result) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("comparedColumnCount", result.comparedColumns().size());
        model.put("ignoredColumnCount", result.ignoredColumns().size());
        model.put("rowsOnlyInLeftCount", result.rowsOnlyInLeft().size());
        model.put("rowsOnlyInRightCount", result.rowsOnlyInRight().size());
        model.put("differingRowCount", result.differingRows().size());
        model.put("hasDifferences", result.hasDifferences());
        return model;
    }

    private Map<String, Object> tableModel(final TableComparisonResult result) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("schema", result.table().schemaName());
        model.put("name", result.table().tableName());
        return model;
    }

    private Map<String, Object> businessKeyModel(final TableComparisonResult result) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("index", result.businessKey().indexName());
        model.put("columns", columnNames(result.businessKey().columns()));
        return model;
    }

    private Map<String, Object> toMissingRowModel(
            final RowKey rowKey,
            final Map<ColumnRef, String> leftValues,
            final Map<ColumnRef, String> rightValues,
            final List<ColumnRef> keyColumns,
            final List<ColumnRef> comparedColumns) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("key", rowKey.values());
        model.put("leftValues", valuesModel(rowKey, leftValues, keyColumns, comparedColumns));
        model.put("rightValues", valuesModel(rowKey, rightValues, keyColumns, comparedColumns));
        return model;
    }

    private Map<String, Object> toRowDifferenceModel(
            final RowDifference rowDifference,
            final List<ColumnRef> keyColumns,
            final List<ColumnRef> comparedColumns) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("key", rowDifference.key().values());
        model.put("leftValues", valuesModel(rowDifference.key(), rowDifference.leftValues(), keyColumns, comparedColumns));
        model.put("rightValues", valuesModel(rowDifference.key(), rowDifference.rightValues(), keyColumns, comparedColumns));
        model.put("differences", rowDifference.columnDifferences().stream().map(this::toColumnDifferenceModel).toList());
        return model;
    }

    private Map<String, String> valuesModel(
            final RowKey rowKey,
            final Map<ColumnRef, String> values,
            final List<ColumnRef> keyColumns,
            final List<ColumnRef> comparedColumns) {
        if (values == null) {
            return Map.of();
        }
        final Map<String, String> model = new LinkedHashMap<>();
        for (int keyIndex = 0; keyIndex < keyColumns.size(); keyIndex++) {
            model.put(keyColumns.get(keyIndex).name(), keyIndex < rowKey.values().size() ? rowKey.values().get(keyIndex) : "");
        }
        for (ColumnRef comparedColumn : comparedColumns) {
            model.put(comparedColumn.name(), values.getOrDefault(comparedColumn, ""));
        }
        return model;
    }

    private Map<String, Object> toColumnDifferenceModel(final ColumnDifference columnDifference) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("column", columnDifference.column().name());
        model.put("left", columnDifference.leftValue());
        model.put("right", columnDifference.rightValue());
        return model;
    }

    private List<String> columnNames(final List<ColumnRef> columns) {
        return columns.stream().map(ColumnRef::name).toList();
    }

}
