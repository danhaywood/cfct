## 1. Audit Comparison Model and Service

- [ ] 1.1 Add API models for audit descriptors, semantic keys, per-scope count differences, and the combined foreground/background result.
- [ ] 1.2 Add a service contract that compares audit descriptors by `(target, memberIdentifier)` occurrence counts.
- [ ] 1.3 Implement deterministic foreground and background count comparison, including duplicate and empty-group handling.
- [ ] 1.4 Add unit tests for matching groups, missing keys, extra keys, duplicate-count differences, scope isolation, and deterministic ordering.

## 2. SQL Server Audit Record Resolution

- [ ] 2.1 Add a SQL Server repository that retrieves canonical audit descriptors for a database side and supplied interaction identifiers.
- [ ] 2.2 Ensure empty identifier collections short-circuit without issuing invalid SQL and all populated queries remain parameterized.
- [ ] 2.3 Extend the purchase-order fixture so `AuditTrailEntry` exposes the canonical audited-member column and representative duplicate records.
- [ ] 2.4 Add integration tests for foreground retrieval and independent left/right completed-background retrieval.

## 3. Automation Orchestration and JSON

- [ ] 3.1 Invoke audit resolution for the shared foreground identifier and each side's completed background descendant identifiers during automation refresh.
- [ ] 3.2 Keep the existing four-way union of left/right foreground/background business-table footprints unchanged.
- [ ] 3.3 Add `auditTrailComparison` to successful automation JSON without changing top-level business `hasDifferences` semantics.
- [ ] 3.4 Include audit comparison in no-business-footprint responses and latest-command startup refreshes.
- [ ] 3.5 Add automation service and controller tests for clean, divergent, duplicate, background-ID-mismatched, and empty-footprint audit results.

## 4. Verification and Documentation

- [ ] 4.1 Update JSON approval files and automation API examples with deterministic audit comparison output.
- [ ] 4.2 Run CFCT unit and integration test suites and resolve regressions.
- [ ] 4.3 Document canonical audit compatibility-view columns, semantic count behavior, business-footprint union behavior, and the deferred post-value phase.
