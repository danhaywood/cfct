package com.danhaywood.sqlcomparer.webapp.comparison;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;

import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class WebappComparisonExecutionService {

    private final MultiTableComparisonService comparisonService;

    public WebappComparisonExecutionService(final MultiTableComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    public MultiTableComparisonResult compare(
            final Connection leftConnection,
            final Connection rightConnection,
            final MultiTableComparisonRequest request) {
        return comparisonService.compare(leftConnection, rightConnection, request);
    }
}
