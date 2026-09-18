package com.danhaywood.cfct.model;

public record AuditTrailComparisonResult(
        boolean hasDifferences,
        String mode,
        AuditTrailScopeComparison foreground,
        AuditTrailScopeComparison background) {

    public static final String SEMANTIC_KEY_COUNTS = "semantic-key-counts";
}
