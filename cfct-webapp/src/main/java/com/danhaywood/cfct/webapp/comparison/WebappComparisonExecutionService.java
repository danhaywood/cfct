package com.danhaywood.cfct.webapp.comparison;

import com.danhaywood.cfct.model.ComparisonViewModelMapper;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.model.MultiTableComparisonViewResult;
import com.danhaywood.cfct.request.ComparisonOptions;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressListener;
import com.danhaywood.cfct.service.MultiTableComparisonReportFormatter;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

@Service
public class WebappComparisonExecutionService {

    private final MultiTableComparisonService comparisonService;
    private final MultiTableComparisonReportFormatter reportFormatter;
    private final WebappDataSourceConfiguration dataSourceConfiguration;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;

    public WebappComparisonExecutionService(
            final MultiTableComparisonService comparisonService,
            final MultiTableComparisonReportFormatter reportFormatter,
            final WebappDataSourceConfiguration dataSourceConfiguration,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder) {
        this.comparisonService = comparisonService;
        this.reportFormatter = reportFormatter;
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.authenticatedContextHolder = authenticatedContextHolder;
    }

    public ComparisonExecutionOutcome compare(final MultiTableComparisonRequest request) {
        return compare(request, ComparisonProgressListener.NO_OP);
    }

    public ComparisonExecutionOutcome compare(
            final MultiTableComparisonRequest request,
            final ComparisonProgressListener progressListener) {
        return compare(request, progressListener, authenticatedContextHolder.required());
    }

    public ComparisonExecutionOutcome compare(
            final MultiTableComparisonRequest request,
            final ComparisonProgressListener progressListener,
            final AuthenticatedConnectionContext authenticatedContext) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(authenticatedContext);
        final ComparisonOptions options = new ComparisonOptions(
                request.options().businessKeyIndexSuffix(),
                request.options().ignoredColumnNames(),
                progressListener,
                request.options().maxParallelComparisons());
        final MultiTableComparisonResult rawResult = comparisonService.compare(
                dataSources.left(),
                dataSources.right(),
                new MultiTableComparisonRequest(request.tables(), options));
        final MultiTableComparisonViewResult viewResult = ComparisonViewModelMapper.toViewResult(rawResult);
        final String json = reportFormatter.renderJson(rawResult);
        final String yaml = reportFormatter.renderYaml(rawResult);
        final byte[] excel = reportFormatter.renderExcel(rawResult);
        return new ComparisonExecutionOutcome(rawResult, viewResult, json, yaml, excel);
    }

    public record ComparisonExecutionOutcome(
            MultiTableComparisonResult rawResult,
            MultiTableComparisonViewResult viewResult,
            String json,
            String yaml,
            byte[] excel) {
    }
}
