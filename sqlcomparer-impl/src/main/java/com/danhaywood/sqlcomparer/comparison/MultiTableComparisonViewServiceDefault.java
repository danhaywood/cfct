package com.danhaywood.sqlcomparer.comparison;

import com.danhaywood.sqlcomparer.model.ComparisonViewModelMapper;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonViewResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonViewService;

import java.sql.Connection;

public final class MultiTableComparisonViewServiceDefault implements MultiTableComparisonViewService {

    private final MultiTableComparisonService comparisonService;

    public MultiTableComparisonViewServiceDefault(final MultiTableComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @Override
    public MultiTableComparisonViewResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final MultiTableComparisonRequest request) {
        final MultiTableComparisonResult result = comparisonService.compare(leftConnection, rightConnection, request);
        return ComparisonViewModelMapper.toViewResult(result);
    }
}
