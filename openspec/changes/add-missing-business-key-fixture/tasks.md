## 1. Fixture structure

- [x] 1.1 Keep `dbo.PurchaseOrder` resources under `sql/fixtures/purchase-order/`.
- [x] 1.2 Add `sql/fixtures/purchase-order-without-business-key/` for `dbo.PurchaseOrderWithoutBusinessKey`.
- [x] 1.3 Add `sql/fixtures/ambiguous-business-key/` for `dbo.AmbiguousBusinessKey`.
- [x] 1.4 Remove obsolete `left-init.sql`, `right-init.sql`, and the sample-items approval output when no tests use them.

## 2. Missing business-key fixture

- [x] 2.1 Add `dbo.PurchaseOrderWithoutBusinessKey` with plausible purchase-order-related columns including `id`, `reference`, `status`, and `version`.
- [x] 2.2 Ensure `dbo.PurchaseOrderWithoutBusinessKey` intentionally has no unique index whose name ends with `_BK`.
- [x] 2.3 Add deterministic left-side data for `dbo.PurchaseOrderWithoutBusinessKey`.
- [x] 2.4 Add deterministic right-side data for `dbo.PurchaseOrderWithoutBusinessKey`.
- [x] 2.5 Keep the existing successful `dbo.PurchaseOrder` data unchanged.

## 3. Ambiguous business-key fixture

- [x] 3.1 Add `dbo.AmbiguousBusinessKey` with two unique indexes whose names end with `_BK`.
- [x] 3.2 Update the ambiguous-business-key error test to use fixture SQL rather than inline DDL.

## 4. Test coverage

- [x] 4.1 Update fixture-shape tests to verify `dbo.PurchaseOrderWithoutBusinessKey` exists in both logical databases.
- [x] 4.2 Verify that `dbo.PurchaseOrderWithoutBusinessKey` has no `_BK` unique index.
- [x] 4.3 Update the core comparison missing-business-key test to use `dbo.PurchaseOrderWithoutBusinessKey` instead of ad hoc test SQL.
- [x] 4.4 Verify the missing-business-key error identifies the table and the `_BK` convention.
- [x] 4.5 Remove sample-items-specific harness tests and helper output.

## 5. Validation

- [x] 5.1 Run the relevant unit and integration tests.
- [x] 5.2 Run OpenSpec validation for the change.
