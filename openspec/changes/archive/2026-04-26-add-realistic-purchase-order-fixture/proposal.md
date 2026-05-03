## Why

The SQL Server harness currently uses a minimal `sample_items` fixture that proves database isolation but does not represent the shape of data that cfct will eventually compare.
A realistic `PurchaseOrder` fixture will capture the expected production-style conventions before comparison behavior is implemented.

## What Changes

- Add a realistic SQL Server test fixture for `dbo.PurchaseOrder` with identical schema in the left and right logical databases.
- Model the table with an identity surrogate primary key named `id`.
- Model a business key column named `reference` and expose it through a unique index named with the `_BK` suffix, such as `PurchaseOrder_BK`.
- Add representative domain columns for purchase order data, such as dates, status, supplier reference, currency, and monetary amounts.
- Add a `version` column using `DATETIME2`, suitable for JDBC mapping to `java.sql.Timestamp`.
- Populate left and right fixture data with realistic rows that future comparator work can use to exercise matching, missing rows, differing values, and ignored technical values.
- Do not implement business-key discovery, data comparison, schema comparison, or diff rendering in this change.

## Capabilities

### New Capabilities

- `purchase-order-comparison-fixture`: Defines the realistic SQL Server `PurchaseOrder` fixture used as a foundation for future row-comparison behavior.

### Modified Capabilities

- None.

## Impact

- Adds SQL fixture resources under the test resources tree.
- Adds or updates integration tests only to prove that the fixture loads into both logical databases and exposes the intended schema/data shape.
- Does not change application runtime APIs, production dependencies, or comparison behavior.
