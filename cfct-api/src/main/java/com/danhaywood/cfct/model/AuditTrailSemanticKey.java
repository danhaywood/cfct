package com.danhaywood.cfct.model;

import java.util.Comparator;

public record AuditTrailSemanticKey(String target, String memberIdentifier)
        implements Comparable<AuditTrailSemanticKey> {

    private static final Comparator<String> TEXT_ORDER = Comparator.nullsFirst(String::compareTo);

    @Override
    public int compareTo(final AuditTrailSemanticKey other) {
        final int targetComparison = TEXT_ORDER.compare(target, other.target);
        return targetComparison != 0
                ? targetComparison
                : TEXT_ORDER.compare(memberIdentifier, other.memberIdentifier);
    }
}
