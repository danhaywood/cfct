package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.AuditTrailComparisonResult;
import com.danhaywood.cfct.model.AuditTrailCountDifference;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuditTrailComparisonServiceDefaultTest {

    private final AuditTrailComparisonServiceDefault service = new AuditTrailComparisonServiceDefault();

    @Test
    void reportsMatchingForegroundAndEmptyBackgroundAsEqual() {
        final AuditTrailEntryDescriptor appA = entry("foreground", 0, "customer.Customer:1", "name");
        final AuditTrailEntryDescriptor appB = entry("foreground", 9, "customer.Customer:1", "name");

        final AuditTrailComparisonResult result = service.compare(
                List.of(appA), List.of(appB), List.of(), List.of());

        assertThat(result.hasDifferences()).isFalse();
        assertThat(result.mode()).isEqualTo("semantic-key-counts");
        assertThat(result.foreground().appACount()).isEqualTo(1);
        assertThat(result.foreground().appBCount()).isEqualTo(1);
        assertThat(result.foreground().differences()).isEmpty();
        assertThat(result.background().differences()).isEmpty();
    }

    @Test
    void reportsMissingExtraAndDuplicateCountsInDeterministicOrder() {
        final List<AuditTrailEntryDescriptor> appA = List.of(
                entry("a", 0, "z.Type:1", "second"),
                entry("a", 1, "a.Type:1", "first"),
                entry("a", 2, "a.Type:1", "first"));
        final List<AuditTrailEntryDescriptor> appB = List.of(
                entry("b", 42, "z.Type:1", "third"),
                entry("b", 43, "a.Type:1", "first"));

        final AuditTrailComparisonResult result = service.compare(
                appA, appB, List.of(), List.of());

        assertThat(result.hasDifferences()).isTrue();
        assertThat(result.foreground().differences()).containsExactly(
                new AuditTrailCountDifference("a.Type:1", "first", 2, 1),
                new AuditTrailCountDifference("z.Type:1", "second", 1, 0),
                new AuditTrailCountDifference("z.Type:1", "third", 0, 1));
    }

    @Test
    void excludesObjectVersionOnlyActivityFromBothScopesAndTotals() {
        final AuditTrailComparisonResult result = service.compare(
                List.of(entry("root", 0, "customer.Customer:1", "objectVersion")),
                List.of(),
                List.of(),
                List.of(
                        entry("right-child", 0, "customer.Customer:1", "objectVersion"),
                        entry("right-child", 1, "customer.Customer:2", "objectVersion")));

        assertThat(result.hasDifferences()).isFalse();
        assertThat(result.foreground().hasDifferences()).isFalse();
        assertThat(result.foreground().appACount()).isZero();
        assertThat(result.foreground().appBCount()).isZero();
        assertThat(result.foreground().differences()).isEmpty();
        assertThat(result.background().hasDifferences()).isFalse();
        assertThat(result.background().appACount()).isZero();
        assertThat(result.background().appBCount()).isZero();
        assertThat(result.background().differences()).isEmpty();
    }

    @Test
    void excludesObjectVersionFromMixedTotalsButReportsEligibleDifferences() {
        final List<AuditTrailEntryDescriptor> appA = List.of(
                entry("a", 0, "customer.Customer:1", "objectVersion"),
                entry("a", 1, "customer.Customer:1", "name"),
                entry("a", 2, "customer.Customer:1", "name"));
        final List<AuditTrailEntryDescriptor> appB = List.of(
                entry("b", 0, "customer.Customer:1", "objectVersion"),
                entry("b", 1, "customer.Customer:2", "objectVersion"),
                entry("b", 2, "customer.Customer:1", "name"),
                entry("b", 3, "customer.Customer:1", "status"));

        final AuditTrailComparisonResult result = service.compare(
                appA, appB, List.of(), List.of());

        assertThat(result.hasDifferences()).isTrue();
        assertThat(result.foreground().appACount()).isEqualTo(2);
        assertThat(result.foreground().appBCount()).isEqualTo(2);
        assertThat(result.foreground().differences()).containsExactly(
                new AuditTrailCountDifference("customer.Customer:1", "name", 2, 1),
                new AuditTrailCountDifference("customer.Customer:1", "status", 0, 1));
    }

    @Test
    void objectVersionExclusionUsesExactMemberIdentifier() {
        final AuditTrailComparisonResult result = service.compare(
                List.of(entry("a", 0, "customer.Customer:1", "ObjectVersion")),
                List.of(),
                List.of(),
                List.of());

        assertThat(result.hasDifferences()).isTrue();
        assertThat(result.foreground().appACount()).isEqualTo(1);
        assertThat(result.foreground().appBCount()).isZero();
        assertThat(result.foreground().differences()).containsExactly(
                new AuditTrailCountDifference("customer.Customer:1", "ObjectVersion", 1, 0));
    }

    @Test
    void keepsForegroundAndBackgroundScopesIndependentAndIgnoresChildIds() {
        final AuditTrailEntryDescriptor foregroundA = entry("root", 0, "customer.Customer:1", "name");
        final AuditTrailEntryDescriptor foregroundB = entry("root", 0, "customer.Customer:1", "name");
        final AuditTrailEntryDescriptor backgroundA = entry("left-child", 0, "customer.Customer:1", "status");
        final AuditTrailEntryDescriptor backgroundB = entry("right-child", 3, "customer.Customer:1", "status");

        final AuditTrailComparisonResult result = service.compare(
                List.of(foregroundA),
                List.of(foregroundB),
                List.of(backgroundA, backgroundA),
                List.of(backgroundB));

        assertThat(result.foreground().hasDifferences()).isFalse();
        assertThat(result.background().hasDifferences()).isTrue();
        assertThat(result.background().differences()).containsExactly(
                new AuditTrailCountDifference("customer.Customer:1", "status", 2, 1));
    }

    private static AuditTrailEntryDescriptor entry(
            final String interactionId,
            final int sequence,
            final String target,
            final String memberIdentifier) {
        return new AuditTrailEntryDescriptor(interactionId, sequence, target, memberIdentifier);
    }
}
