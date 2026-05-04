## 1. SQL diff-query strategy scaffolding

- [x] 1.1 Introduce a comparison execution strategy boundary so single-table comparison can choose database-side diff execution.
- [x] 1.2 Add domain model support for diff-kind markers from SQL rows (left-only, right-only, value-different).
- [x] 1.3 Wire strategy selection into datasource-based comparison orchestration without changing public API contracts.

## 2. Database-side diff query generation

- [x] 2.1 Implement SQL builder logic that composes one query with branches for left-only, right-only, and value-different rows.
- [x] 2.2 Implement key-column join and null-safe comparable-column difference predicates consistent with existing semantics.
- [x] 2.3 Project only required key and comparable columns plus diff-kind metadata, avoiding full-rowset materialization.
- [x] 2.4 Ensure deterministic ordering by business-key columns for stable downstream rendering and approvals.
- [x] 2.5 Enforce compatibility-level-100-safe SQL generation by excluding unsupported syntax and functions in the query builder.

## 3. Mapping and compatibility validation

- [x] 3.1 Map database-side diff query results into existing structured comparison result types without changing report payload semantics.
- [x] 3.2 Preserve ignore-column advisor behavior so ignored-column-only changes do not produce differences.
- [x] 3.3 Add fallback or guarded switch behavior to allow rollback to client-side diffing during rollout if regressions are detected.

## 4. Tests, fixtures, and performance checks

- [x] 4.1 Add or update integration tests covering left-only, right-only, value-different, and fully-matching-row exclusion scenarios.
- [x] 4.2 Update deterministic approval tests for text, JSON, YAML, and Excel outputs to confirm unchanged external semantics.
- [x] 4.3 Add a large-table benchmark or characterization test to verify reduced client memory usage and lower transferred row counts.
- [x] 4.4 Document indexing and query-plan expectations for business-key columns in developer-facing notes.
- [x] 4.5 Add integration coverage that executes generated SQL against a database configured with compatibility level 100.

## 5. SQL tracing support

- [x] 5.1 Add opt-in SQL tracing based on `datasource-proxy` DataSource wrappers.
- [x] 5.2 Route CLI and webapp DataSource creation through tracing-capable wrappers so comparison and metadata SQL is logged centrally.
- [x] 5.3 Document how to enable SQL tracing for verification runs.
