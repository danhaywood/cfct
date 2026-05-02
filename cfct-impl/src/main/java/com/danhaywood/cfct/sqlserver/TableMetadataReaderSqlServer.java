package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.exception.MetadataException;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.spi.IgnoreColumnAdvisor;
import com.danhaywood.cfct.spi.TableMetadataReader;
import com.danhaywood.cfct.model.TableRef;

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

    private final List<IgnoreColumnAdvisor> ignoreColumnAdvisors;

    public TableMetadataReaderSqlServer() {
        this(List.of(
                new IgnoreColumnAdvisorForIdentityColumns(true),
                new IgnoreColumnAdvisorForUuidColumns(true),
                new IgnoreColumnAdvisorForTimestamps(true),
                new IgnoreColumnAdvisorUsingExtendedProperties(true)));
    }

    public TableMetadataReaderSqlServer(final List<IgnoreColumnAdvisor> ignoreColumnAdvisors) {
        this.ignoreColumnAdvisors = List.copyOf(ignoreColumnAdvisors);
    }

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
            if (isIgnoredByAdvisor(column) || request.options().ignores(columnName)) {
                ignoredColumns.add(column.column());
                continue;
            }
            comparedColumns.add(column.column());
        }
        return new TableMetadata(table, businessKey, columns, businessKey.columns(), ignoredColumns, comparedColumns);
    }

    private List<ColumnMetadata> readColumns(final Connection connection, final TableRef table) {
        final String sql = """
                SELECT c.name,
                       c.is_identity,
                       ty.name AS sql_type_name,
                       CONVERT(nvarchar(255), ep.value) AS ignored_extended_property_value
                FROM sys.schemas s
                JOIN sys.tables t ON s.schema_id = t.schema_id
                JOIN sys.columns c ON t.object_id = c.object_id
                JOIN sys.types ty ON c.user_type_id = ty.user_type_id
                LEFT JOIN sys.extended_properties ep
                       ON ep.class = 1
                      AND ep.major_id = t.object_id
                      AND ep.minor_id = c.column_id
                      AND ep.name = 'cfct.ignored'
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
                            resultSet.getBoolean("is_identity"),
                            resultSet.getString("sql_type_name"),
                            resultSet.getString("ignored_extended_property_value")));
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
                final Map<String, List<ColumnRef>> matchingKeyObjects = new LinkedHashMap<>();
                while (resultSet.next()) {
                    final String keyObjectName = resultSet.getString("index_name");
                    if (!hasBusinessKeySuffix(keyObjectName, options.businessKeyIndexSuffix())) {
                        continue;
                    }
                    matchingKeyObjects.computeIfAbsent(keyObjectName, ignored -> new ArrayList<>())
                            .add(new ColumnRef(resultSet.getString("column_name")));
                }
                if (matchingKeyObjects.isEmpty()) {
                    throw new MetadataException("Table %s has no unique index or unique constraint ending with %s".formatted(
                            table.displayName(), options.businessKeyIndexSuffix()));
                }
                if (matchingKeyObjects.size() > 1) {
                    throw new MetadataException("Table %s has multiple unique indexes or unique constraints ending with %s: %s".formatted(
                            table.displayName(), options.businessKeyIndexSuffix(), String.join(", ", matchingKeyObjects.keySet())));
                }
                final Map.Entry<String, List<ColumnRef>> entry = matchingKeyObjects.entrySet().iterator().next();
                return new BusinessKey(entry.getKey(), entry.getValue());
            }
        } catch (SQLException ex) {
            throw new MetadataException("Failed to read business-key index or constraint for table %s".formatted(table.displayName()), ex);
        }
    }

    static boolean hasBusinessKeySuffix(final String candidateName, final String expectedSuffix) {
        if (candidateName == null || expectedSuffix == null) {
            return false;
        }
        final String normalizedName = candidateName.toLowerCase(Locale.ROOT);
        final String normalizedSuffix = expectedSuffix.toLowerCase(Locale.ROOT);
        return normalizedName.endsWith(normalizedSuffix);
    }

    private boolean isIgnoredByAdvisor(final ColumnMetadata column) {
        for (IgnoreColumnAdvisor advisor : ignoreColumnAdvisors) {
            if (advisor.shouldIgnore(column)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> lowerCaseColumnNames(final List<ColumnRef> columns) {
        final Set<String> names = new LinkedHashSet<>();
        for (final ColumnRef column : columns) {
            names.add(column.name().toLowerCase(Locale.ROOT));
        }
        return names;
    }
}
