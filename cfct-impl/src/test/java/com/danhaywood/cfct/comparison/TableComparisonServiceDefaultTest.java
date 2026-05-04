package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.TableComparisonRequest;
import com.danhaywood.cfct.spi.TableMetadataReader;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TableComparisonServiceDefaultTest {

    @Test
    void choosesFirstSupportingStrategyAndFallsBackWhenNeeded() {
        final TableMetadata metadata = new TableMetadata(
                new TableRef("dbo", "PurchaseOrder"),
                new BusinessKey("PurchaseOrder_PK", List.of(new ColumnRef("reference"))),
                List.of(),
                List.of(new ColumnRef("reference")),
                List.of(),
                List.of(new ColumnRef("status")));
        final TableComparisonResult expectedResult = new TableComparisonResult(
                metadata.table(),
                metadata.businessKey(),
                metadata.comparedColumns(),
                metadata.ignoredColumns(),
                List.of(),
                List.of(),
                List.of());

        final TableMetadataReader metadataReader = mock(TableMetadataReader.class);

        final TableComparisonExecutionStrategy unsupportedStrategy = mock(TableComparisonExecutionStrategy.class);
        when(unsupportedStrategy.supports(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(false);

        final TableComparisonExecutionStrategy fallbackStrategy = mock(TableComparisonExecutionStrategy.class);
        when(fallbackStrategy.supports(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(true);
        when(fallbackStrategy.compare(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(expectedResult);

        final Connection leftConnection = mock(Connection.class);
        final Connection rightConnection = mock(Connection.class);
        final TableComparisonRequest request = TableComparisonRequest.forTable("dbo", "PurchaseOrder");
        when(metadataReader.read(leftConnection, request)).thenReturn(metadata);

        final TableComparisonServiceDefault service = new TableComparisonServiceDefault(
                metadataReader,
                List.of(unsupportedStrategy, fallbackStrategy));

        final TableComparisonResult actual = service.compare(leftConnection, rightConnection, request);

        assertThat(actual).isEqualTo(expectedResult);
    }
}
