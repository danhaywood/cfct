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

### Keep each table-oriented fixture in its own directory

Each table-oriented fixture should live under its own subdirectory of `src/test/resources/sql/fixtures/`.
For example, `purchase-order`, `purchase-order-without-business-key`, and `ambiguous-business-key` should each own their schema and data resources.
This keeps fixture purpose obvious and prevents one table fixture from silently creating unrelated tables.

Alternative considered: create the invalid table inside the existing purchase-order schema resource.
This was rejected because it would make the `purchase-order` fixture responsible for multiple test scenarios.

Alternative considered: create invalid tables directly inside error-handling tests.
This was rejected because it would hide fixture DDL in test code rather than documenting it as reusable test data.

### Use a clear table name

The table should be named `dbo.PurchaseOrderWithoutBusinessKey` or an equally explicit name.
The name should make clear that the missing `_BK` index is intentional rather than an omission.

Alternative considered: use a generic table such as `dbo.NoBusinessKey`.
This was rejected because the fixture is purchase-order oriented and the negative table should remain in that domain.

### Omit only the BK-suffixed index

The negative table should still have plausible columns such as `id`, `reference`, `status`, and `version`.
It should intentionally omit a unique index whose name ends in `_BK`.
This lets the test isolate the missing business-key convention rather than failing because the table is otherwise malformed.

### Remove obsolete sample-items smoke fixture

The original `sample_items` fixture was useful for proving basic left/right initialization before realistic fixtures existed.
Now that the table-specific SQL fixtures cover initialization, shape, and comparison scenarios, the old `left-init.sql`, `right-init.sql`, and approval file should be removed if no test uses them.

## Risks / Trade-offs

- The fixture tree may become cluttered with negative cases → Keep each table fixture in a clearly named subdirectory.
- A future developer may add a `_BK` index accidentally → Name the table explicitly and verify the missing-index behavior in tests.
- Removing `sample_items` may hide a basic harness smoke scenario → Retain direct database connectivity tests and rely on realistic fixture initialization tests for script execution coverage.
- The core comparison capability is currently an active change rather than an archived main spec → Keep this change focused on fixture extension and test coverage so it can be implemented after or alongside the core comparison work.
