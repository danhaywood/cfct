## Context

The current automation flow derives tables from the newest successful command and fails when no eligible business tables are resolved.
That failure is too strong for safe/query actions because they can legitimately touch no business tables and should produce a successful no-differences report.

The current JSON report shape uses a top-level `hasDifferences` flag and a top-level `tables` array of table result objects.
Automation clients need a shape that separately answers two questions: which tables were compared, and which compared tables had differences.

## Goals / Non-Goals

**Goals:**

- Return a successful JSON report for automation requests that resolve no eligible business tables.
- Extend the JSON report shape with `differingTables` and `comparedTables`.
- Keep detailed row-level payloads only for differing tables so automation output remains focused.
- Make empty comparisons explicit with `hasDifferences: false`, `differingTables: []`, and `comparedTables: []`.
- Preserve deterministic ordering and existing table-detail field names inside differing table entries.

**Non-Goals:**

- Do not change YAML or Excel output in this change.
- Do not change the core comparison algorithm for non-empty table selections.
- Do not change manual UI comparison behavior.
- Do not add persisted automation result state or asynchronous job handling.

## Decisions

### Decision: Replace top-level JSON `tables` with `differingTables`

The JSON renderer will move detailed table result objects from `tables` to `differingTables` and filter that array to table results whose summary has differences.
This matches the automation use case where consumers primarily want actionable differences.

Alternative considered: keep `tables` and add `comparedTables` only.
That would be less breaking, but it keeps an ambiguous name whose contents have been interpreted inconsistently.

### Decision: Add `comparedTables` as an identity list

The JSON renderer will add `comparedTables` as a deterministic list of table identity objects for every table actually compared.
Each entry should use the same nested table identity shape as table result entries, for example `{ "table": { "schema": "dbo", "name": "Supplier" } }`.

Alternative considered: duplicate full table result details in `comparedTables`.
That would make clean-table reports larger and blur the distinction between inventory and actionable differences.

### Decision: Model automation no-table outcomes as empty comparison reports

When automation command-driven table resolution returns no eligible touched business tables, the service should return a renderer-compatible empty comparison outcome instead of throwing an exception.
The empty JSON should represent a successful no-op comparison, not a server failure.

Alternative considered: return `204 No Content`.
That would not provide the explicit JSON shape needed by automation clients.

## Risks / Trade-offs

- Existing JSON consumers that read top-level `tables` will break → Mark the JSON contract change as breaking and update README/tests/approvals.
- Filtering details to only differing tables can hide clean-table summaries → Keep `comparedTables` as the complete inventory of compared tables.
- Empty automation output may mask a configuration mistake → Preserve failures for command discovery errors and execution errors; only no eligible touched business tables becomes a successful empty comparison.
- YAML and JSON shapes diverge → Limit the change to JSON because the request is automation-oriented and the automation endpoint returns JSON.
