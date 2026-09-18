package com.danhaywood.cfct.model;

public record AuditTrailCountDifference(
        String target,
        String memberIdentifier,
        int appACount,
        int appBCount) {
}
