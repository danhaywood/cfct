package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.ComparisonViewModelMapper;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.MultiTableComparisonViewResult;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.service.MultiTableComparisonViewService;

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
