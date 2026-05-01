## Context

Fixture data now includes command-log rows, audit-trail rows, and logical-type-to-table mappings.
Current services still require explicit table selection and do not derive table scope from interaction identifiers.
We need a reusable repository service that resolves touched physical tables from one or more command `interactionId` values.

## Goals / Non-Goals

**Goals:**
- Provide a repository interface that accepts one or more command interaction identifiers.
- Resolve touched tables by traversing command to audit linkage and parsing `AuditTrailEntry.target` values.
- Map parsed logical type names through `util.LogicalTypeTableMapping` and return deterministic de-duplicated qualified table names.
- Keep behavior stable for mixed inputs where some identifiers or targets are invalid or unmapped.

**Non-Goals:**
- Change fixture schemas or seed strategy in this change.
- Introduce UI workflows or CLI flags that consume this repository yet.
- Implement command selection heuristics beyond the explicit interaction identifiers passed by callers.

## Decisions

Create a dedicated repository in `sqlcomparer-impl` that queries audit entries for provided command interaction identifiers.
Extract logical type names from target values by splitting on the first `:` and taking the prefix only when the format is valid.
Resolve logical type names to qualified table names using `util.LogicalTypeTableMapping` and aggregate results into a sorted set.
Ignore malformed targets, missing audit rows, and unmapped logical types while still returning valid results for the rest of the input.
Expose the repository through a small service-facing contract so future selection workflows can consume it without coupling to SQL details.

## Risks / Trade-offs

[Target parsing assumptions drift from upstream format] → Keep parsing conservative and cover malformed inputs with tests.
[Multiple table mappings inflate result size unexpectedly] → Return a distinct sorted set and document one-to-many expansion behavior.
[Large interactionId batches cause heavy SQL IN clauses] → Start with moderate batch support and refactor to temp-table strategy if profiling requires it.

## Migration Plan

Implement repository and tests behind existing module boundaries without changing external APIs.
Run integration tests against current SQL Server fixture to validate end-to-end resolution behavior.
Adopt this repository in higher-level workflows in a follow-up change.

## Open Questions

Should the repository expose provenance metadata such as which interaction or logical type produced each table.
Should unresolved logical types be surfaced as warnings in the return type or logging policy.
