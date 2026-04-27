package com.danhaywood.sqlcomparer.sqlserver;

import com.danhaywood.sqlcomparer.model.BusinessKey;
import com.danhaywood.sqlcomparer.model.ColumnMetadata;
import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.request.ComparisonOptions;
import com.danhaywood.sqlcomparer.exception.MetadataException;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;
import com.danhaywood.sqlcomparer.model.TableMetadata;
import com.danhaywood.sqlcomparer.spi.TableMetadataReader;
import com.danhaywood.sqlcomparer.model.TableRef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class TableMetadataReaderSqlServer implements TableMetadataReader {

    @Override
    public TableMetadata read(final Connection connection, final TableComparisonRequest request) {
        final TableRef table = request.table();
        final List<ColumnMetadata> columns = readColumns(connection, table);
        if (columns.isEmpty()) {
            throw new MetadataException("Table %s was not found or has no columns".formatted(table.displayName()));
        }
        final BusinessKey businessKey = readBusinessKey(connection, table, request.options());
        final Set<String> keyColumnNames = lowerCaseColumnNames(businessKey.columns());
        final var ignoredColumns = new ArrayList<ColumnRef>();
        final var comparedColumns = new ArrayList<ColumnRef>();
        for (final ColumnMetadata column : columns) {
            final String columnName = column.column().name();
            if (keyColumnNames.contains(columnName.toLowerCase(Locale.ROOT))) {
                continue;
            }
            if (request.options().ignores(columnName)) {
                ignoredColumns.add(column.column());
                continue;
            }
            comparedColumns.add(column.column());
        }
        return new TableMetadata(table, businessKey, columns, businessKey.columns(), ignoredColumns, comparedColumns);
    }

    private List<ColumnMetadata> readColumns(final Connection connection, final TableRef table) {
        final String sql = """
                SELECT c.name, c.is_identity
                FROM sys.schemas s
                JOIN sys.tables t ON s.schema_id = t.schema_id
                JOIN sys.columns c ON t.object_id = c.object_id
                WHERE s.name = ? AND t.name = ?
                ORDER BY c.column_id
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, table.schemaName());
            statement.setString(2, table.tableName());
            try (ResultSet resultSet = statement.executeQuery()) {
                final var columns = new ArrayList<ColumnMetadata>();
                while (resultSet.next()) {
                    columns.add(new ColumnMetadata(
                            new ColumnRef(resultSet.getString("name")),
                            resultSet.getBoolean("is_identity")));
                }
                return columns;
            }
        } catch (SQLException ex) {
            throw new MetadataException("Failed to read columns for table %s".formatted(table.displayName()), ex);
        }
    }

    private BusinessKey readBusinessKey(
            final Connection connection,
            final TableRef table,
            final ComparisonOptions options) {
        final String sql = """
                SELECT i.name AS index_name, ic.key_ordinal, c.name AS column_name
                FROM sys.schemas s
                JOIN sys.tables t ON s.schema_id = t.schema_id
                JOIN sys.indexes i ON t.object_id = i.object_id
                JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE s.name = ?
                  AND t.name = ?
                  AND i.is_unique = 1
                  AND i.name IS NOT NULL
                  AND ic.key_ordinal > 0
                ORDER BY i.name, ic.key_ordinal
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, table.schemaName());
            statement.setString(2, table.tableName());
            try (ResultSet resultSet = statement.executeQuery()) {
                final Map<String, List<ColumnRef>> matchingIndexes = new LinkedHashMap<>();
                while (resultSet.next()) {
                    final String indexName = resultSet.getString("index_name");
                    if (!indexName.endsWith(options.businessKeyIndexSuffix())) {
                        continue;
                    }
                    matchingIndexes.computeIfAbsent(indexName, ignored -> new ArrayList<>())
                            .add(new ColumnRef(resultSet.getString("column_name")));
                }
                if (matchingIndexes.isEmpty()) {
                    throw new MetadataException("Table %s has no unique index ending with %s".formatted(
                            table.displayName(), options.businessKeyIndexSuffix()));
                }
                if (matchingIndexes.size() > 1) {
                    throw new MetadataException("Table %s has multiple unique indexes ending with %s: %s".formatted(
                            table.displayName(), options.businessKeyIndexSuffix(), String.join(", ", matchingIndexes.keySet())));
                }
                final Map.Entry<String, List<ColumnRef>> entry = matchingIndexes.entrySet().iterator().next();
                return new BusinessKey(entry.getKey(), entry.getValue());
            }
        } catch (SQLException ex) {
            throw new MetadataException("Failed to read business-key index for table %s".formatted(table.displayName()), ex);
        }
    }

    private Set<String> lowerCaseColumnNames(final List<ColumnRef> columns) {
        final Set<String> names = new LinkedHashSet<>();
        for (final ColumnRef column : columns) {
            names.add(column.name().toLowerCase(Locale.ROOT));
        }
        return names;
    }
}
