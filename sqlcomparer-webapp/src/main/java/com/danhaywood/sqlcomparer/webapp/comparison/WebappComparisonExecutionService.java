package com.danhaywood.sqlcomparer.webapp.comparison;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

@Service
public class WebappComparisonExecutionService {

    private final MultiTableComparisonService comparisonService;
    private final WebappDataSources dataSources;

    public WebappComparisonExecutionService(
            final MultiTableComparisonService comparisonService,
            final WebappDataSources dataSources) {
        this.comparisonService = comparisonService;
        this.dataSources = dataSources;
    }

    public MultiTableComparisonResult compare(final MultiTableComparisonRequest request) {
        return comparisonService.compare(dataSources.left(), dataSources.right(), request);
    }
}
