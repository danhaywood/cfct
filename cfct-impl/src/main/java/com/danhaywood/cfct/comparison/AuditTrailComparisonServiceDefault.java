package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.AuditTrailComparisonResult;
import com.danhaywood.cfct.model.AuditTrailCountDifference;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;
import com.danhaywood.cfct.model.AuditTrailScopeComparison;
import com.danhaywood.cfct.model.AuditTrailSemanticKey;
import com.danhaywood.cfct.service.AuditTrailComparisonService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class AuditTrailComparisonServiceDefault implements AuditTrailComparisonService {

    private static final String OBJECT_VERSION_MEMBER_IDENTIFIER = "objectVersion";

    @Override
    public AuditTrailComparisonResult compare(
            final List<AuditTrailEntryDescriptor> appAForeground,
            final List<AuditTrailEntryDescriptor> appBForeground,
            final List<AuditTrailEntryDescriptor> appABackground,
            final List<AuditTrailEntryDescriptor> appBBackground) {
        final AuditTrailScopeComparison foreground = compareScope(appAForeground, appBForeground);
        final AuditTrailScopeComparison background = compareScope(appABackground, appBBackground);
        return new AuditTrailComparisonResult(
                foreground.hasDifferences() || background.hasDifferences(),
                AuditTrailComparisonResult.SEMANTIC_KEY_COUNTS,
                foreground,
                background);
    }

    private static AuditTrailScopeComparison compareScope(
            final List<AuditTrailEntryDescriptor> appA,
            final List<AuditTrailEntryDescriptor> appB) {
        final List<AuditTrailEntryDescriptor> eligibleAppA = eligibleEntries(appA);
        final List<AuditTrailEntryDescriptor> eligibleAppB = eligibleEntries(appB);
        final Map<AuditTrailSemanticKey, Counts> counts = new TreeMap<>();
        eligibleAppA.forEach(entry -> counts.computeIfAbsent(keyOf(entry), ignored -> new Counts()).appACount++);
        eligibleAppB.forEach(entry -> counts.computeIfAbsent(keyOf(entry), ignored -> new Counts()).appBCount++);
        final List<AuditTrailCountDifference> differences = counts.entrySet().stream()
                .filter(entry -> entry.getValue().appACount != entry.getValue().appBCount)
                .map(entry -> new AuditTrailCountDifference(
                        entry.getKey().target(),
                        entry.getKey().memberIdentifier(),
                        entry.getValue().appACount,
                        entry.getValue().appBCount))
                .toList();
        return new AuditTrailScopeComparison(
                !differences.isEmpty(),
                eligibleAppA.size(),
                eligibleAppB.size(),
                differences);
    }

    private static List<AuditTrailEntryDescriptor> eligibleEntries(
            final List<AuditTrailEntryDescriptor> entries) {
        return entries.stream()
                .filter(entry -> !OBJECT_VERSION_MEMBER_IDENTIFIER.equals(entry.memberIdentifier()))
                .toList();
    }

    private static AuditTrailSemanticKey keyOf(final AuditTrailEntryDescriptor entry) {
        return new AuditTrailSemanticKey(entry.target(), entry.memberIdentifier());
    }

    private static final class Counts {
        private int appACount;
        private int appBCount;
    }
}
