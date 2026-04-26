package com.danhaywood.sqlcomparer.core;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

@Service
public final class TableComparer {

    private final TableMetadataReader metadataReader;
    private final TableRowReader rowReader;

    public TableComparer(final TableMetadataReader metadataReader, final TableRowReader rowReader) {
        this.metadataReader = metadataReader;
        this.rowReader = rowReader;
    }

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
                differingRows.add(new RowDifference(key, columnDifferences));
            }
        }

        return new TableComparisonResult(
                metadata.table(),
                metadata.businessKey(),
                metadata.comparedColumns(),
                metadata.ignoredColumns(),
                rowsOnlyInLeft,
                rowsOnlyInRight,
                differingRows);
    }
}
