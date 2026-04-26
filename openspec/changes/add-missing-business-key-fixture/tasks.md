## 1. Fixture schema

- [x] 1.1 Extend the purchase-order fixture schema with `dbo.PurchaseOrderWithoutBusinessKey`.
- [x] 1.2 Give the negative fixture table plausible purchase-order-related columns including `id`, `reference`, `status`, and `version`.
- [x] 1.3 Ensure the negative fixture table intentionally has no unique index whose name ends with `_BK`.
- [x] 1.4 Ensure the fixture remains re-runnable and creates the negative table in both logical databases.

## 2. Fixture data

- [x] 2.1 Add deterministic left-side data for `dbo.PurchaseOrderWithoutBusinessKey` if data is needed by the error-path test.
- [x] 2.2 Add deterministic right-side data for `dbo.PurchaseOrderWithoutBusinessKey` if data is needed by the error-path test.
- [x] 2.3 Keep the existing successful `dbo.PurchaseOrder` data unchanged.

## 3. Test coverage

- [x] 3.1 Update fixture-shape tests to verify `dbo.PurchaseOrderWithoutBusinessKey` exists in both logical databases.
- [x] 3.2 Verify that `dbo.PurchaseOrderWithoutBusinessKey` has no `_BK` unique index.
- [x] 3.3 Update the core comparison missing-business-key test to use `dbo.PurchaseOrderWithoutBusinessKey` instead of ad hoc test SQL.
- [x] 3.4 Verify the missing-business-key error identifies the table and the `_BK` convention.

## 4. Validation

- [x] 4.1 Run the relevant unit and integration tests.
- [x] 4.2 Run OpenSpec validation for the change.
