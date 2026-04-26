## Context

The `PurchaseOrder` fixture currently provides the positive path for business-key discovery through `PurchaseOrder_BK(reference)`.
The core single-table comparison work also needs a stable negative fixture for a table that looks plausible but intentionally cannot be compared by the `_BK` convention.
Using a shared fixture table is better than creating ad hoc invalid tables inside individual tests because the scenario becomes visible in the fixture contract.

This change is a refinement of the test fixtures and test coverage.
It does not introduce new comparison behavior beyond verifying that the missing-business-key path is exercised against a realistic table.

## Goals / Non-Goals

**Goals:**

- Add a negative fixture table without a `_BK` unique index.
- Keep the table realistic enough to resemble a purchase-order-related table.
- Ensure the fixture is created in both left and right logical databases.
- Use the fixture to test the core library's missing-business-key error path.
- Keep the existing positive `dbo.PurchaseOrder` fixture unchanged.

**Non-Goals:**

- Do not change successful purchase order comparison behavior.
- Do not add CLI or web error handling.
- Do not introduce configurable error formatting.
- Do not archive or depend on ad hoc test-only SQL that is invisible to the fixture specification.

## Decisions

### Add the negative table to the shared fixture schema

The invalid table should be created by the same purchase-order fixture schema resource as the positive `dbo.PurchaseOrder` table.
That makes the fixture self-contained and ensures both logical databases have the same positive and negative comparison examples.

Alternative considered: create the invalid table directly inside the error-handling test.
This was rejected because it would hide the scenario in test code rather than documenting it as part of the fixture contract.

### Use a clear table name

The table should be named `dbo.PurchaseOrderWithoutBusinessKey` or an equally explicit name.
The name should make clear that the missing `_BK` index is intentional rather than an omission.

Alternative considered: use a generic table such as `dbo.NoBusinessKey`.
This was rejected because the fixture is purchase-order oriented and the negative table should remain in that domain.

### Omit only the BK-suffixed index

The negative table should still have plausible columns such as `id`, `reference`, `status`, and `version`.
It should intentionally omit a unique index whose name ends in `_BK`.
This lets the test isolate the missing business-key convention rather than failing because the table is otherwise malformed.

## Risks / Trade-offs

- The fixture may become cluttered with negative cases → Keep this table small and focused on the missing `_BK` scenario.
- A future developer may add a `_BK` index accidentally → Name the table explicitly and verify the missing-index behavior in tests.
- The core comparison capability is currently an active change rather than an archived main spec → Keep this change focused on fixture extension and test coverage so it can be implemented after or alongside the core comparison work.
