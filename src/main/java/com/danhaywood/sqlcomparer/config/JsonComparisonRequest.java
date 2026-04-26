package com.danhaywood.sqlcomparer.config;

import com.danhaywood.sqlcomparer.core.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.core.TableRef;

import java.util.List;

public record JsonComparisonRequest(Output output, List<Table> tables) {

    public ComparisonOutputType outputType() {
        if (output == null) {
            throw new ComparisonRequestException("Comparison output type is required");
        }
        return ComparisonOutputType.parse(output.type());
    }

    public MultiTableComparisonRequest toMultiTableComparisonRequest() {
        if (tables == null || tables.isEmpty()) {
            throw new ComparisonRequestException("At least one table is required");
        }
        return MultiTableComparisonRequest.forTables(tables.stream()
                .map(Table::toTableRef)
                .toList());
    }

    public record Output(String type) {
    }

    public record Table(String schema, String name) {

        private TableRef toTableRef() {
            if (schema == null || schema.isBlank()) {
                throw new ComparisonRequestException("Table schema is required");
            }
            if (name == null || name.isBlank()) {
                throw new ComparisonRequestException("Table name is required");
            }
            return new TableRef(schema, name);
        }
    }
}
