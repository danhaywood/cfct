package com.danhaywood.sqlcomparer.webapp.comparison;

import com.danhaywood.sqlcomparer.model.ComparisonViewModelMapper;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.model.MultiTableComparisonViewResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonReportFormatter;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

@Service
public class WebappComparisonExecutionService {

    private final MultiTableComparisonService comparisonService;
    private final MultiTableComparisonReportFormatter reportFormatter;
    private final WebappDataSources dataSources;

    public WebappComparisonExecutionService(
            final MultiTableComparisonService comparisonService,
            final MultiTableComparisonReportFormatter reportFormatter,
            final WebappDataSources dataSources) {
        this.comparisonService = comparisonService;
        this.reportFormatter = reportFormatter;
        this.dataSources = dataSources;
    }

    public ComparisonExecutionOutcome compare(final MultiTableComparisonRequest request) {
        final MultiTableComparisonResult rawResult = comparisonService.compare(dataSources.left(), dataSources.right(), request);
        final MultiTableComparisonViewResult viewResult = ComparisonViewModelMapper.toViewResult(rawResult);
        final String json = reportFormatter.renderJson(rawResult);
        final byte[] excel = reportFormatter.renderExcel(rawResult);
        return new ComparisonExecutionOutcome(rawResult, viewResult, json, excel);
    }

    public record ComparisonExecutionOutcome(
            MultiTableComparisonResult rawResult,
            MultiTableComparisonViewResult viewResult,
            String json,
            byte[] excel) {
    }
}
