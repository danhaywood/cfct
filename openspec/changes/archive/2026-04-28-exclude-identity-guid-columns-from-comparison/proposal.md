## Why

Comparison noise currently includes technical identifiers that should not drive business difference outcomes.
Identity, GUID-named, and SQL Server `UNIQUEIDENTIFIER` columns are technical keys and should be excluded by default from value comparison.

## What Changes

- Update column partitioning rules so identity-backed columns are always ignored for value comparison, even when they are part of primary/business key structures.
- Update column partitioning rules so columns named `uuid` or `guid` are always ignored for value comparison.
- Update SQL Server metadata handling so columns with datatype `UNIQUEIDENTIFIER` are always ignored for value comparison.
- Preserve row matching via business-key index columns while separating key use from compared-value inclusion.
- Update fixtures and characterization expectations so identity/GUID-only differences do not produce row differences.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `core-single-table-comparison`: Change partitioning behavior to always ignore identity, `uuid`/`guid` name matches, and `UNIQUEIDENTIFIER` columns for value comparison.
- `purchase-order-comparison-fixture`: Update fixture-driven characterization expectations so identity-only differences are treated as ignored-value differences.

## Impact

Core comparison metadata and column-partition logic in API/impl modules will change, with corresponding tests updated.
Fixture-backed tests and expected comparison outcomes will be updated to reflect reduced technical-column noise.
