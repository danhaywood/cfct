package com.danhaywood.sqlcomparer.webapp.comparison;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonViewResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonViewService;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

@Service
public class WebappComparisonExecutionService {

    private final MultiTableComparisonViewService comparisonService;
    private final WebappDataSources dataSources;

    public WebappComparisonExecutionService(
            final MultiTableComparisonViewService comparisonService,
            final WebappDataSources dataSources) {
        this.comparisonService = comparisonService;
        this.dataSources = dataSources;
    }

    public MultiTableComparisonViewResult compare(final MultiTableComparisonRequest request) {
        return comparisonService.compare(dataSources.left(), dataSources.right(), request);
    }
}
