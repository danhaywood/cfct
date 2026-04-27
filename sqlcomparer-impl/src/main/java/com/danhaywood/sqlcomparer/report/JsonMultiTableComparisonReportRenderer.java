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
        model.put("businessKey", businessKeyModel(result));
        model.put("comparedColumns", columnNames(result.comparedColumns()));
        model.put("ignoredColumns", columnNames(result.ignoredColumns()));
        model.put("rowsOnlyInLeft", rowKeys(result.rowsOnlyInLeft()));
        model.put("rowsOnlyInRight", rowKeys(result.rowsOnlyInRight()));
        model.put("differingRows", result.differingRows().stream().map(this::toRowDifferenceModel).toList());
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

    private Map<String, Object> toRowDifferenceModel(final RowDifference rowDifference) {
        final Map<String, Object> model = new LinkedHashMap<>();
        model.put("key", rowDifference.key().values());
        model.put("differences", rowDifference.columnDifferences().stream().map(this::toColumnDifferenceModel).toList());
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

    private List<List<String>> rowKeys(final List<RowKey> rows) {
        return rows.stream().map(RowKey::values).toList();
    }
}
