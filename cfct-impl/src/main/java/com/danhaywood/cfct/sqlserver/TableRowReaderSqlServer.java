package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.spi.TableRowReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TableRowReaderSqlServer implements TableRowReader {

    @Override
    public Map<RowKey, Map<ColumnRef, String>> readRows(final Connection connection, final TableMetadata metadata) {
        final List<ColumnRef> selectedColumns = Stream.concat(
                        metadata.keyColumns().stream(),
                        metadata.comparedColumns().stream())
                .toList();
        final String selectColumns = selectedColumns.stream()
                .map(SqlServerIdentifiers::quoteColumn)
                .collect(Collectors.joining(", "));
        final String orderByColumns = metadata.keyColumns().stream()
                .map(SqlServerIdentifiers::quoteColumn)
                .collect(Collectors.joining(", "));
        final String sql = "SELECT %s FROM %s ORDER BY %s".formatted(
                selectColumns,
                SqlServerIdentifiers.quoteTable(metadata.table()),
                orderByColumns);
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            final Map<RowKey, Map<ColumnRef, String>> rows = new LinkedHashMap<>();
            while (resultSet.next()) {
                final List<String> keyValues = new ArrayList<>();
                int columnIndex = 1;
                for (int i = 0; i < metadata.keyColumns().size(); i++) {
                    keyValues.add(SqlServerValueFormatter.formatValue(resultSet.getObject(columnIndex++)));
                }
                final Map<ColumnRef, String> values = new LinkedHashMap<>();
                for (final ColumnRef column : metadata.comparedColumns()) {
                    values.put(column, SqlServerValueFormatter.formatValue(resultSet.getObject(columnIndex++)));
                }
                rows.put(new RowKey(keyValues), values);
            }
            return rows;
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to read rows for table %s".formatted(metadata.table().displayName()), ex);
        }
    }

}
