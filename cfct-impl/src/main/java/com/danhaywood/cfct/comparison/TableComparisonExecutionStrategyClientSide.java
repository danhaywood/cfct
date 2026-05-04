package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.ColumnDifference;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.spi.TableRowReader;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

final class TableComparisonExecutionStrategyClientSide implements TableComparisonExecutionStrategy {

    private final TableRowReader rowReader;

    TableComparisonExecutionStrategyClientSide(final TableRowReader rowReader) {
        this.rowReader = rowReader;
    }

    @Override
    public boolean supports(final Connection leftConnection, final Connection rightConnection, final TableMetadata metadata) {
        return true;
    }

    @Override
    public TableComparisonResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final TableMetadata metadata) {
        final Map<RowKey, Map<ColumnRef, String>> leftRows = rowReader.readRows(leftConnection, metadata);
        final Map<RowKey, Map<ColumnRef, String>> rightRows = rowReader.readRows(rightConnection, metadata);

        final TreeSet<RowKey> allKeys = new TreeSet<>();
        allKeys.addAll(leftRows.keySet());
        allKeys.addAll(rightRows.keySet());

        final var rowsOnlyInLeft = new ArrayList<RowKey>();
        final var rowsOnlyInRight = new ArrayList<RowKey>();
        final var differingRows = new ArrayList<RowDifference>();
        final var matchingRows = new ArrayList<RowKey>();

        for (final RowKey key : allKeys) {
            final Map<ColumnRef, String> leftValues = leftRows.get(key);
            final Map<ColumnRef, String> rightValues = rightRows.get(key);
            if (leftValues == null) {
                rowsOnlyInRight.add(key);
                continue;
            }
            if (rightValues == null) {
                rowsOnlyInLeft.add(key);
                continue;
            }
            final var columnDifferences = new ArrayList<ColumnDifference>();
            for (final ColumnRef column : metadata.comparedColumns()) {
                final String leftValue = leftValues.get(column);
                final String rightValue = rightValues.get(column);
                if (!java.util.Objects.equals(leftValue, rightValue)) {
                    columnDifferences.add(new ColumnDifference(column, leftValue, rightValue));
                }
            }
            if (!columnDifferences.isEmpty()) {
                differingRows.add(new RowDifference(key, leftValues, rightValues, columnDifferences));
            } else {
                matchingRows.add(key);
            }
        }

        return new TableComparisonResult(
                metadata.table(),
                metadata.businessKey(),
                metadata.comparedColumns(),
                metadata.ignoredColumns(),
                rowsOnlyInLeft,
                rowsOnlyInRight,
                differingRows,
                valuesFor(rowsOnlyInLeft, leftRows),
                valuesFor(rowsOnlyInRight, rightRows),
                valuesFor(matchingRows, leftRows));
    }

    private Map<RowKey, Map<ColumnRef, String>> valuesFor(
            final java.util.List<RowKey> rowKeys,
            final Map<RowKey, Map<ColumnRef, String>> rows) {
        final Map<RowKey, Map<ColumnRef, String>> values = new LinkedHashMap<>();
        for (RowKey rowKey : rowKeys) {
            values.put(rowKey, rows.get(rowKey));
        }
        return values;
    }
}
