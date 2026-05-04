## Context

Current comparison logic retrieves row sets from both sides and computes row-level differences in JVM memory.
This approach scales poorly for large tables because matching rows dominate transfer volume and heap consumption.
The change must preserve current diff semantics and output contracts while shifting heavy comparison work to SQL.
Both sides are SQL Server datasources in current supported workflows, and the comparer already has metadata about key and comparable columns.
The target database runs at compatibility level 100, so generated SQL must stay within SQL Server 2008 language features.

## Goals / Non-Goals

**Goals:**
- Return only diff rows from query execution, specifically left-only, right-only, and value-different rows.
- Preserve existing classification and rendering behavior in result writers.
- Reduce network I/O and client memory use for large-table comparisons.
- Keep behavior deterministic through stable ordering over key columns.

**Non-Goals:**
- Changing external CLI or API contracts.
- Adding vendor-specific optimizations beyond SQL Server-compatible SQL needed for correctness and performance.
- Redesigning output formats or approval-file workflows.

## Decisions

1. Build a single composed diff query using CTEs and set branches.
Rationale: one round-trip and one execution plan is simpler to reason about and reduces repeated scans compared with multi-query orchestration.
Alternative considered: multiple separate queries for missing-left, missing-right, and differing-values.
Alternative rejected: easier branch-level tuning but incurs more round-trips and repeated filter logic.

2. Use `EXCEPT` style set comparisons for left-only and right-only detection where column shape allows it.
Rationale: set operators express existence differences succinctly and let SQL Server optimize anti-semi logic.
Alternative considered: `NOT EXISTS` anti-joins.
Alternative rejected: equivalent correctness but often more verbose in generated SQL for composite keys.

3. Use inner join on key columns with predicate on non-key comparable columns to detect value differences.
Rationale: explicit key join preserves identity semantics while non-key predicates isolate changed payload values.
Alternative considered: row-hash comparison.
Alternative rejected: hash collision risk and reduced debuggability of per-column differences.

4. Tag each branch with a diff kind marker consumed by existing mapper logic.
Rationale: keeps downstream rendering stable and avoids reinterpreting row provenance post-query.
Alternative considered: infer kind after fetch from null patterns.
Alternative rejected: brittle and harder to maintain with nullable business columns.

5. Introduce a strategy boundary in the comparison engine for database-side vs client-side diff execution.
Rationale: allows safe fallback and targeted testing without invasive branching through call sites.
Alternative considered: direct replacement of current engine.
Alternative rejected: higher rollout risk and harder rollback.

6. Restrict generated SQL to compatibility-level-100-safe constructs.
Rationale: target environments with compatibility level 100 reject newer syntax and functions.
The query builder SHALL use CTEs, joins, `UNION ALL`, `EXCEPT`, `CASE`, `COALESCE`/`ISNULL`, and standard predicates only.
The query builder SHALL avoid `OFFSET/FETCH`, `IIF`, `TRY_CONVERT`, `CONCAT`, `STRING_AGG`, and other post-2008 features.
Alternative considered: enabling higher compatibility level as a prerequisite.
Alternative rejected: environment constraint is fixed for this change and cannot be assumed mutable.

## Risks / Trade-offs

[Large execution plans on very wide tables] → Mitigation: project only required key and comparable columns and avoid `SELECT *`.
[SQL dialect edge cases for null comparison semantics] → Mitigation: normalize null-safe equality predicates per column type and add fixture tests.
[Potential regressions in row classification mapping] → Mitigation: retain explicit diff-kind marker and add golden output regression tests.
[Higher DB CPU consumption] → Mitigation: document indexing expectations on key columns and monitor query duration in test harness.
[Generated SQL accidentally uses post-2008 syntax] → Mitigation: add compatibility-level-100 integration coverage and SQL-shape assertions in query-builder tests.

## Migration Plan

Implement the strategy behind a feature flag or internal execution switch defaulted on for supported datasource combinations.
Run existing comparison fixtures and large-table benchmark scenarios against both strategies.
If regressions appear, switch back to client-side strategy while keeping new SQL builder isolated for iterative fixes.
After validation, remove temporary fallback hooks only when confidence is established.

## Open Questions

Should left-only and right-only branches include all comparable columns or only key plus changed payload columns for minimal transfer.
Do we need per-branch query hints for problematic plans in very large production-like datasets while still remaining compatibility-level-100-safe.
Should cross-database qualification be centralized in one SQL builder utility to avoid drift across future features.
