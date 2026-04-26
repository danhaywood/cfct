## Why

The core library can compare one named table, but callers will soon need to compare a selected set of tables without taking on orchestration themselves.
Adding a set-of-tables API is the next library-level step before any CLI or web layer is added.

## What Changes

- Add a core library API that accepts a collection of table references and compares each requested table between left and right JDBC connections.
- Reuse the existing single-table comparison behavior for each table rather than duplicating metadata discovery or row comparison logic.
- Return a structured multi-table result that preserves one table result per requested table.
- Provide deterministic rendering for multi-table comparison results.
- Extend the SQL fixtures to include three good comparable tables, each with a `_BK` unique index.
- Add integration coverage that initializes the good table fixtures and compares two selected tables from the available three.
- Keep this as an explicit selected-table API; do not auto-discover all comparable tables yet.
- Do not add CLI or webapp behavior in this change.

## Capabilities

### New Capabilities

- `core-multi-table-comparison`: Provides the core library capability for comparing a caller-specified set of SQL Server tables between two JDBC connections.

### Modified Capabilities

- `purchase-order-comparison-fixture`: Extends the fixture set with three good comparable table fixtures so multi-table comparison can be tested against selected tables.

## Impact

- Adds production Java types and services for multi-table comparison orchestration and structured results.
- Adds or extends report rendering for multiple table results.
- Adds SQL fixture resources for additional good comparable tables.
- Adds integration tests that compare two selected tables from a larger fixture set.
- Leaves single-table comparison semantics unchanged.
