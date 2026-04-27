package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;

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
