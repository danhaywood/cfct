package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.model.ColumnDifference;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.DiffRowKind;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.sqlserver.SqlServerTableDiffQueryBuilder;
import com.danhaywood.cfct.sqlserver.SqlServerValueFormatter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class TableComparisonExecutionStrategyDatabaseSide implements TableComparisonExecutionStrategy {

    private final SqlServerTableDiffQueryBuilder queryBuilder;

    TableComparisonExecutionStrategyDatabaseSide() {
        this(new SqlServerTableDiffQueryBuilder());
    }

    TableComparisonExecutionStrategyDatabaseSide(final SqlServerTableDiffQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    public boolean supports(final Connection leftConnection, final Connection rightConnection, final TableMetadata metadata) {
        try {
            return isSqlServer(leftConnection)
                    && isSqlServer(rightConnection)
                    && Objects.equals(normalizedServerUrl(leftConnection), normalizedServerUrl(rightConnection))
                    && hasCatalog(leftConnection)
                    && hasCatalog(rightConnection);
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public TableComparisonResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final TableMetadata metadata) {
        try {
            final String sql = queryBuilder.buildQuery(metadata, leftConnection.getCatalog(), rightConnection.getCatalog());
            try (PreparedStatement statement = leftConnection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                return mapResult(metadata, resultSet);
            } catch (SQLException ex) {
                throw new ComparisonException("Failed to execute database-side diff for table %s".formatted(
                        metadata.table().displayName()), ex);
            }
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to execute database-side diff for table %s".formatted(
                    metadata.table().displayName()), ex);
        }
    }

    private TableComparisonResult mapResult(final TableMetadata metadata, final ResultSet resultSet) throws SQLException {
        final List<RowKey> rowsOnlyInLeft = new ArrayList<>();
        final List<RowKey> rowsOnlyInRight = new ArrayList<>();
        final List<RowDifference> differingRows = new ArrayList<>();
        final Map<RowKey, Map<ColumnRef, String>> rowsOnlyInLeftValues = new LinkedHashMap<>();
        final Map<RowKey, Map<ColumnRef, String>> rowsOnlyInRightValues = new LinkedHashMap<>();

        while (resultSet.next()) {
            final DiffRowKind kind = DiffRowKind.fromSqlMarker(resultSet.getString("diff_kind"));
            final RowKey key = rowKey(metadata, resultSet);
            final Map<ColumnRef, String> leftValues = values(resultSet, metadata.comparedColumns(), "l_");
            final Map<ColumnRef, String> rightValues = values(resultSet, metadata.comparedColumns(), "r_");

            switch (kind) {
                case ONLY_IN_LEFT -> {
                    rowsOnlyInLeft.add(key);
                    rowsOnlyInLeftValues.put(key, leftValues);
                }
                case ONLY_IN_RIGHT -> {
                    rowsOnlyInRight.add(key);
                    rowsOnlyInRightValues.put(key, rightValues);
                }
                case DIFFERENT -> differingRows.add(new RowDifference(
                        key,
                        leftValues,
                        rightValues,
                        differences(metadata.comparedColumns(), leftValues, rightValues)));
            }
        }

        rowsOnlyInLeft.sort(RowKey::compareTo);
        rowsOnlyInRight.sort(RowKey::compareTo);
        differingRows.sort(java.util.Comparator.comparing(RowDifference::key));

        return new TableComparisonResult(
                metadata.table(),
                metadata.businessKey(),
                metadata.comparedColumns(),
                metadata.ignoredColumns(),
                rowsOnlyInLeft,
                rowsOnlyInRight,
                differingRows,
                sortedValues(rowsOnlyInLeft, rowsOnlyInLeftValues),
                sortedValues(rowsOnlyInRight, rowsOnlyInRightValues),
                Map.of());
    }

    private RowKey rowKey(final TableMetadata metadata, final ResultSet resultSet) throws SQLException {
        final List<String> values = new ArrayList<>();
        for (ColumnRef keyColumn : metadata.keyColumns()) {
            values.add(SqlServerValueFormatter.formatValue(resultSet.getObject(keyColumn.name())));
        }
        return new RowKey(values);
    }

    private Map<ColumnRef, String> values(
            final ResultSet resultSet,
            final List<ColumnRef> comparedColumns,
            final String prefix) throws SQLException {
        final Map<ColumnRef, String> values = new LinkedHashMap<>();
        for (ColumnRef comparedColumn : comparedColumns) {
            values.put(comparedColumn, SqlServerValueFormatter.formatValue(resultSet.getObject(prefix + comparedColumn.name())));
        }
        return values;
    }

    private List<ColumnDifference> differences(
            final List<ColumnRef> comparedColumns,
            final Map<ColumnRef, String> leftValues,
            final Map<ColumnRef, String> rightValues) {
        return comparedColumns.stream()
                .filter(column -> !Objects.equals(leftValues.get(column), rightValues.get(column)))
                .map(column -> new ColumnDifference(column, leftValues.get(column), rightValues.get(column)))
                .collect(Collectors.toList());
    }

    private Map<RowKey, Map<ColumnRef, String>> sortedValues(
            final List<RowKey> orderedKeys,
            final Map<RowKey, Map<ColumnRef, String>> valuesByKey) {
        final Map<RowKey, Map<ColumnRef, String>> sorted = new LinkedHashMap<>();
        for (RowKey key : orderedKeys) {
            sorted.put(key, valuesByKey.get(key));
        }
        return sorted;
    }

    private boolean isSqlServer(final Connection connection) throws SQLException {
        final String productName = connection.getMetaData().getDatabaseProductName();
        return productName != null && productName.toLowerCase(Locale.ROOT).contains("sql server");
    }

    private String normalizedServerUrl(final Connection connection) throws SQLException {
        final DatabaseMetaData metaData = connection.getMetaData();
        final String url = metaData.getURL();
        if (url == null) {
            return null;
        }
        return url.replaceAll("(?i);databaseName=[^;]*", "");
    }

    private boolean hasCatalog(final Connection connection) throws SQLException {
        return connection.getCatalog() != null && !connection.getCatalog().isBlank();
    }
}
