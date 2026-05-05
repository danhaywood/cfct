package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.service.TableComparisonService;
import com.danhaywood.cfct.spi.ColumnValueNormalizer;
import com.danhaywood.cfct.spi.TableMetadataReader;
import com.danhaywood.cfct.spi.TableRowReader;
import com.danhaywood.cfct.sqlserver.ColumnValueNormalizerUsingExtendedProperties;

import java.sql.Connection;
import java.util.List;

public final class TableComparisonServiceDefault implements TableComparisonService {

    private final TableMetadataReader metadataReader;
    private final List<TableComparisonExecutionStrategy> executionStrategies;

    public TableComparisonServiceDefault(final TableMetadataReader metadataReader, final TableRowReader rowReader) {
        this(metadataReader, rowReader, List.of(new ColumnValueNormalizerUsingExtendedProperties()));
    }

    public TableComparisonServiceDefault(
            final TableMetadataReader metadataReader,
            final TableRowReader rowReader,
            final List<ColumnValueNormalizer> columnValueNormalizers) {
        this(metadataReader, List.of(
                new TableComparisonExecutionStrategyDatabaseSide(columnValueNormalizers),
                new TableComparisonExecutionStrategyClientSide(rowReader, columnValueNormalizers)));
    }

    TableComparisonServiceDefault(
            final TableMetadataReader metadataReader,
            final List<TableComparisonExecutionStrategy> executionStrategies) {
        this.metadataReader = metadataReader;
        this.executionStrategies = List.copyOf(executionStrategies);
    }

    @Override
    public TableComparisonResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final TableComparisonRequest request) {
        final TableMetadata metadata = metadataReader.read(leftConnection, request);
        return executionStrategies.stream()
                .filter(strategy -> strategy.supports(leftConnection, rightConnection, metadata))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No table comparison execution strategy supports this request"))
                .compare(leftConnection, rightConnection, metadata);
    }
}
