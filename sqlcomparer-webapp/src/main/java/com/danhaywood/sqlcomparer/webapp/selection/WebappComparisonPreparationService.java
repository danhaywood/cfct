package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.request.MultiTableComparisonRequest;

import org.springframework.stereotype.Service;

@Service
public class WebappComparisonPreparationService {

    private final SelectionPlan selectionPlan;

    public WebappComparisonPreparationService(final SelectionPlan selectionPlan) {
        this.selectionPlan = selectionPlan;
    }

    public MultiTableComparisonRequest prepareRequest() {
        return MultiTableComparisonRequest.forTables(selectionPlan.resolveTables());
    }
}
