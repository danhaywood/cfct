package com.danhaywood.sqlcomparer.sqlserver;

import com.danhaywood.sqlcomparer.core.ColumnRef;
import com.danhaywood.sqlcomparer.core.ComparisonException;
import com.danhaywood.sqlcomparer.core.RowKey;
import com.danhaywood.sqlcomparer.core.TableMetadata;
import com.danhaywood.sqlcomparer.core.TableRowReader;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public final class SqlServerTableRowReader implements TableRowReader {

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
                    keyValues.add(formatValue(resultSet.getObject(columnIndex++)));
                }
                final Map<ColumnRef, String> values = new LinkedHashMap<>();
                for (final ColumnRef column : metadata.comparedColumns()) {
                    values.put(column, formatValue(resultSet.getObject(columnIndex++)));
                }
                rows.put(new RowKey(keyValues), values);
            }
            return rows;
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to read rows for table %s".formatted(metadata.table().displayName()), ex);
        }
    }

    private static String formatValue(final Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        }
        if (value instanceof Date date) {
            return date.toLocalDate().toString();
        }
        if (value instanceof Time time) {
            return time.toLocalTime().toString();
        }
        return value.toString();
    }
}
