## 1. SQL diff-query strategy scaffolding

- [ ] 1.1 Introduce a comparison execution strategy boundary so single-table comparison can choose database-side diff execution.
- [ ] 1.2 Add domain model support for diff-kind markers from SQL rows (left-only, right-only, value-different).
- [ ] 1.3 Wire strategy selection into datasource-based comparison orchestration without changing public API contracts.

## 2. Database-side diff query generation

- [ ] 2.1 Implement SQL builder logic that composes one query with branches for left-only, right-only, and value-different rows.
- [ ] 2.2 Implement key-column join and null-safe comparable-column difference predicates consistent with existing semantics.
- [ ] 2.3 Project only required key and comparable columns plus diff-kind metadata, avoiding full-rowset materialization.
- [ ] 2.4 Ensure deterministic ordering by business-key columns for stable downstream rendering and approvals.
- [ ] 2.5 Enforce compatibility-level-100-safe SQL generation by excluding unsupported syntax and functions in the query builder.

## 3. Mapping and compatibility validation

- [ ] 3.1 Map database-side diff query results into existing structured comparison result types without changing report payload semantics.
- [ ] 3.2 Preserve ignore-column advisor behavior so ignored-column-only changes do not produce differences.
- [ ] 3.3 Add fallback or guarded switch behavior to allow rollback to client-side diffing during rollout if regressions are detected.

## 4. Tests, fixtures, and performance checks

- [ ] 4.1 Add or update integration tests covering left-only, right-only, value-different, and fully-matching-row exclusion scenarios.
- [ ] 4.2 Update deterministic approval tests for text, JSON, YAML, and Excel outputs to confirm unchanged external semantics.
- [ ] 4.3 Add a large-table benchmark or characterization test to verify reduced client memory usage and lower transferred row counts.
- [ ] 4.4 Document indexing and query-plan expectations for business-key columns in developer-facing notes.
- [ ] 4.5 Add integration coverage that executes generated SQL against a database configured with compatibility level 100.
