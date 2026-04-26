## Why

The core comparison behavior must fail clearly when a requested table has no `_BK` unique index, but the reusable SQL fixtures currently only model the positive `PurchaseOrder_BK` case.
Adding a deliberately invalid fixture table gives this error path a stable, realistic test target instead of relying on ad hoc test SQL.

## What Changes

- Organize SQL fixtures so each table-oriented fixture has its own subdirectory under `src/test/resources/sql/fixtures/`.
- Add a `purchase-order-without-business-key` fixture for `dbo.PurchaseOrderWithoutBusinessKey`, a realistic table with no unique index ending in `_BK`.
- Add an `ambiguous-business-key` fixture for the ambiguous `_BK` index error path so tests do not create DDL inline.
- Remove the obsolete `sample_items` fixture resources and test-only approval output if they are no longer used.
- Add or update tests so the core single-table comparison verifies metadata errors against shared fixture resources rather than ad hoc test SQL.
- Do not change the successful `dbo.PurchaseOrder` fixture data or its `PurchaseOrder_BK(reference)` convention.
- Do not add CLI or web error handling in this change.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `purchase-order-comparison-fixture`: Adds table-specific fixture directories, including a negative fixture table that intentionally lacks a `_BK` unique index so missing-business-key error handling can be tested realistically.

## Impact

- Updates SQL fixture resources under the test resources tree.
- Removes obsolete `sample_items` smoke-fixture resources and tests if they are no longer used.
- Adds or adjusts integration tests for the core comparison library's missing and ambiguous business-key error paths.
- Leaves production comparison behavior and successful purchase order comparison semantics unchanged.
