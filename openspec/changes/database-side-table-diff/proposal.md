## Why

Some compared tables are large enough that fetching both full result sets into the client becomes slow and memory intensive.
The comparer only needs non-matching and value-different rows, so materializing all matching rows is unnecessary overhead.

## What Changes

- Move single-table diff computation from client-side row comparison to database-side set operations.
- Build one composed SQL diff query per comparison that returns only left-only, right-only, and value-different rows.
- Keep existing output semantics for missing rows and differing values while reducing data transfer and client memory usage.
- Preserve deterministic ordering and existing downstream formatting for text, JSON, YAML, and Excel outputs.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `core-single-table-comparison`: Change requirements so row-level diffing is executed in SQL and only non-matching or value-different rows are returned to the client.
- `datasource-based-comparison-execution`: Change requirements so comparison SQL can compose cross-side set logic that is executed against source and target datasources.

## Impact

Comparison query generation and execution paths in the core comparison modules will change.
Database CPU usage for comparison queries may increase while network transfer and JVM heap usage should decrease.
No external API shape changes are expected, but performance characteristics and execution plans will change for large tables.
