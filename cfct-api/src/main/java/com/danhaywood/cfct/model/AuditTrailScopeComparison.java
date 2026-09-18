package com.danhaywood.cfct.model;

import java.util.List;

public record AuditTrailScopeComparison(
        boolean hasDifferences,
        int appACount,
        int appBCount,
        List<AuditTrailCountDifference> differences) {

    public AuditTrailScopeComparison {
        differences = List.copyOf(differences);
    }
}
