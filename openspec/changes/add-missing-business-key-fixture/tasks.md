## 1. Fixture schema

- [ ] 1.1 Extend the purchase-order fixture schema with `dbo.PurchaseOrderWithoutBusinessKey`.
- [ ] 1.2 Give the negative fixture table plausible purchase-order-related columns including `id`, `reference`, `status`, and `version`.
- [ ] 1.3 Ensure the negative fixture table intentionally has no unique index whose name ends with `_BK`.
- [ ] 1.4 Ensure the fixture remains re-runnable and creates the negative table in both logical databases.

## 2. Fixture data

- [ ] 2.1 Add deterministic left-side data for `dbo.PurchaseOrderWithoutBusinessKey` if data is needed by the error-path test.
- [ ] 2.2 Add deterministic right-side data for `dbo.PurchaseOrderWithoutBusinessKey` if data is needed by the error-path test.
- [ ] 2.3 Keep the existing successful `dbo.PurchaseOrder` data unchanged.

## 3. Test coverage

- [ ] 3.1 Update fixture-shape tests to verify `dbo.PurchaseOrderWithoutBusinessKey` exists in both logical databases.
- [ ] 3.2 Verify that `dbo.PurchaseOrderWithoutBusinessKey` has no `_BK` unique index.
- [ ] 3.3 Update the core comparison missing-business-key test to use `dbo.PurchaseOrderWithoutBusinessKey` instead of ad hoc test SQL.
- [ ] 3.4 Verify the missing-business-key error identifies the table and the `_BK` convention.

## 4. Validation

- [ ] 4.1 Run the relevant unit and integration tests.
- [ ] 4.2 Run OpenSpec validation for the change.
