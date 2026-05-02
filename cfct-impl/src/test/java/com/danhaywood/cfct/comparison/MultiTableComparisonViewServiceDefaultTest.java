package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnDifference;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.ComparisonRowStatus;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.RowDifference;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.MultiTableComparisonService;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MultiTableComparisonViewServiceDefaultTest {

    @Test
    void mapsComparisonResultsToViewRowsWithDeterministicStatuses() {
        final ColumnRef name = new ColumnRef("name");
        final RowKey matchKey = new RowKey(List.of("SUP-001"));
        final RowKey diffKey = new RowKey(List.of("SUP-002"));
        final RowKey onlyLeft = new RowKey(List.of("SUP-LEFT"));
        final RowKey onlyRight = new RowKey(List.of("SUP-RIGHT"));

        final TableComparisonResult tableResult = new TableComparisonResult(
                new TableRef("dbo", "Supplier"),
                new BusinessKey("Supplier_PK", List.of(name)),
                List.of(name),
                List.of(),
                List.of(onlyLeft),
                List.of(onlyRight),
                List.of(new RowDifference(diffKey,
                        Map.of(name, "Supplier Two L"),
                        Map.of(name, "Supplier Two R"),
                        List.of(new ColumnDifference(name, "Supplier Two L", "Supplier Two R")))),
                Map.of(onlyLeft, Map.of(name, "Supplier Left")),
                Map.of(onlyRight, Map.of(name, "Supplier Right")),
                Map.of(matchKey, Map.of(name, "Supplier One")));

        final MultiTableComparisonService comparisonService = mock(MultiTableComparisonService.class);
        final Connection left = mock(Connection.class);
        final Connection right = mock(Connection.class);
        final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(List.of(new TableRef("dbo", "Supplier")));
        when(comparisonService.compare(left, right, request)).thenReturn(new MultiTableComparisonResult(List.of(tableResult)));

        final MultiTableComparisonViewServiceDefault service = new MultiTableComparisonViewServiceDefault(comparisonService);

        final var result = service.compare(left, right, request);

        assertThat(result.tableResults()).hasSize(1);
        final var rows = result.tableResults().getFirst().rows();
        assertThat(rows).extracting(row -> row.status())
                .containsExactly(ComparisonRowStatus.MATCH, ComparisonRowStatus.DIFFERENT, ComparisonRowStatus.ONLY_IN_LEFT, ComparisonRowStatus.ONLY_IN_RIGHT);
        assertThat(rows.getFirst().leftValues()).isEqualTo(rows.getFirst().rightValues());
    }
}
