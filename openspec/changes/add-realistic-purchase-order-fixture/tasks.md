## 1. Fixture SQL resources

- [x] 1.1 Add a reusable `PurchaseOrder` schema fixture that creates `dbo.PurchaseOrder` with identical structure for both logical databases.
- [x] 1.2 Define `id` as an identity-backed primary key and define `reference` as a required domain column.
- [x] 1.3 Add a unique index named `PurchaseOrder_BK` over the `reference` column.
- [x] 1.4 Add representative purchase order columns for order date, status, supplier reference, currency, and monetary amounts.
- [x] 1.5 Add a `version` column using `DATETIME2` and avoid SQL Server `timestamp` or `rowversion`.

## 2. Fixture data

- [x] 2.1 Add left-side purchase order data covering matching rows, differing rows, left-only rows, and future ignored-value examples.
- [x] 2.2 Add right-side purchase order data with the same business-key scenarios and deterministic values.
- [x] 2.3 Ensure fixture data can be re-run safely or is initialized in a predictable clean database state.

## 3. Harness verification

- [x] 3.1 Add or update integration test setup so the realistic fixture can be initialized through the existing SQL Server harness.
- [x] 3.2 Verify that both logical databases contain `dbo.PurchaseOrder` with the intended row counts and business references.
- [x] 3.3 Verify that `PurchaseOrder_BK` exists as a unique index over `reference`.
- [x] 3.4 Verify that `version` is `DATETIME2` and that no SQL Server `timestamp` or `rowversion` column is used.
- [x] 3.5 Keep tests limited to fixture loading and shape verification, without implementing comparison logic.

## 4. Validation

- [x] 4.1 Run the relevant integration tests in a Docker-enabled environment.
- [x] 4.2 Run OpenSpec validation for the change.
