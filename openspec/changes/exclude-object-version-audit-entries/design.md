## Context

CFCT currently compares every retrieved audit descriptor by the semantic key `(target, memberIdentifier)` and reports each input list's size as its scope total.
Causeway's JDO and JPA persistence paths can emit different `objectVersion` audit activity for otherwise equivalent work.
Because `objectVersion` is technical optimistic-locking state rather than meaningful business behavior, those records should not influence either structural equality or the displayed totals.

## Goals / Non-Goals

**Goals:**

- Exclude exact `objectVersion` audit-member records from foreground and background semantic comparison.
- Compute each scope's `appACount` and `appBCount` from the same filtered records used to build semantic-key histograms.
- Preserve all existing comparison behavior for other audit members.
- Keep business-table comparison and its result semantics unchanged.

**Non-Goals:**

- Do not change business-table column partitioning or its existing ignored-column advisors.
- Do not add configurable audit-member exclusion rules in this change.
- Do not compare audit values, timestamps, row identifiers, or transaction identifiers.
- Do not normalize arbitrary member identifier casing or patterns.

## Decisions

- Filter `objectVersion` in `AuditTrailComparisonServiceDefault` before both histogram construction and total calculation.
  - Rationale: the comparison service owns the semantic definition of eligible audit records and can enforce it consistently for all readers and callers.
  - Alternative: add a SQL predicate in `AuditTrailEntryReaderSqlServer`.
  - Rejected because that would hide the rule in one storage adapter and would not protect direct or future service callers.
- Match the member identifier exactly as `objectVersion`.
  - Rationale: Causeway supplies this canonical property identifier, and an exact rule avoids suppressing distinct business members with different casing or names.
- Derive totals from filtered lists rather than subtracting excluded counts afterward.
  - Rationale: one eligible-record set then drives totals, semantic-key counts, differences, and `hasDifferences`, preventing inconsistent output.
- Keep the response model and comparison mode unchanged.
  - Rationale: this is a semantic eligibility refinement, not a new result shape or mode.

## Risks / Trade-offs

- [Risk] Consumers may observe lower audit totals after upgrading.
  → Mitigation: document that totals now represent eligible audit records and add deterministic tests for mixed and objectVersion-only inputs.
- [Risk] A genuine business property named exactly `objectVersion` would also be excluded.
  → Mitigation: keep the rule exact and deliberately scoped to the known Causeway technical member.
- [Risk] Filtering only at comparison time still retrieves excluded rows from SQL Server.
  → Mitigation: prefer a single authoritative semantic rule over duplicated filtering; optimize retrieval later only if measurements justify it.
