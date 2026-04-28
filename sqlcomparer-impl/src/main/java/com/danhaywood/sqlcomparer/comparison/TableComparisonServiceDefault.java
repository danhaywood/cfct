package com.danhaywood.sqlcomparer.comparison;

import com.danhaywood.sqlcomparer.model.ColumnDifference;
import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.RowDifference;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.model.TableMetadata;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;
import com.danhaywood.sqlcomparer.service.TableComparisonService;
import com.danhaywood.sqlcomparer.spi.TableMetadataReader;
import com.danhaywood.sqlcomparer.spi.TableRowReader;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public final class TableComparisonServiceDefault implements TableComparisonService {

    private final TableMetadataReader metadataReader;
    private final TableRowReader rowReader;

    public TableComparisonServiceDefault(final TableMetadataReader metadataReader, final TableRowReader rowReader) {
        this.metadataReader = metadataReader;
        this.rowReader = rowReader;
    }

    @Override
    public TableComparisonResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final TableComparisonRequest request) {
        final TableMetadata metadata = metadataReader.read(leftConnection, request);
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
        final Map<RowKey, Map<ColumnRef, String>> values = new java.util.LinkedHashMap<>();
        for (RowKey rowKey : rowKeys) {
            values.put(rowKey, rows.get(rowKey));
        }
        return values;
    }
}
