## Why

The core comparison behavior must fail clearly when a requested table has no `_BK` unique index, but the reusable SQL fixtures currently only model the positive `PurchaseOrder_BK` case.
Adding a deliberately invalid fixture table gives this error path a stable, realistic test target instead of relying on ad hoc test SQL.

## What Changes

- Extend the realistic purchase order fixture resources with a table that has no unique index ending in `_BK`.
- Name the table to make its purpose clear, such as `dbo.PurchaseOrderWithoutBusinessKey`.
- Give the table a realistic shape with an identity primary key and reference-like domain column, but intentionally omit the `_BK` unique index.
- Add or update tests so the core single-table comparison verifies the missing-business-key error against this fixture table.
- Do not change the successful `dbo.PurchaseOrder` fixture or its `PurchaseOrder_BK(reference)` convention.
- Do not add CLI or web error handling in this change.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `purchase-order-comparison-fixture`: Adds a negative fixture table that intentionally lacks a `_BK` unique index so missing-business-key error handling can be tested realistically.

## Impact

- Updates SQL fixture resources under the test resources tree.
- Adds or adjusts integration tests for the core comparison library's missing-business-key error path.
- Leaves production comparison behavior and successful purchase order comparison semantics unchanged.
