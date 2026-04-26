# purchase-order-comparison-fixture Specification

## Purpose
TBD - created by archiving change add-realistic-purchase-order-fixture. Update Purpose after archive.
## Requirements
### Requirement: Fixture defines a realistic PurchaseOrder schema
The system SHALL provide a SQL Server test fixture that creates a `dbo.PurchaseOrder` table with a realistic purchase order shape in both logical databases.
The left and right fixture schemas SHALL be identical.

#### Scenario: Fixture creates PurchaseOrder in both databases
- **WHEN** the realistic purchase order fixture is initialized for the left and right logical databases
- **THEN** each database contains a `dbo.PurchaseOrder` table with the same schema

#### Scenario: Fixture includes surrogate identity primary key
- **WHEN** the `dbo.PurchaseOrder` table is created
- **THEN** it includes an `id` column defined as an identity-backed primary key

#### Scenario: Fixture includes representative purchase order columns
- **WHEN** the `dbo.PurchaseOrder` table is created
- **THEN** it includes representative domain columns for purchase order comparison such as reference, order date, status, supplier reference, currency, and monetary amounts

### Requirement: Fixture marks the business key using a BK-suffixed index
The fixture SHALL identify the purchase order business key through a unique index whose name ends with `_BK`.
The indexed business key column SHALL be named `reference`.

#### Scenario: Business key index exists
- **WHEN** the `dbo.PurchaseOrder` table is created
- **THEN** it has a unique index named `PurchaseOrder_BK` over the `reference` column

#### Scenario: Business key keeps domain column name
- **WHEN** fixture consumers inspect the business key column
- **THEN** the column is named `reference` rather than being named with the `_BK` suffix

### Requirement: Fixture uses DATETIME2 for version values
The fixture SHALL include a `version` column that uses SQL Server `DATETIME2` for timestamp-like application version values.
The fixture MUST NOT use SQL Server `timestamp` or `rowversion` for this column.

#### Scenario: Version column maps to JDBC timestamp semantics
- **WHEN** the `dbo.PurchaseOrder` table is created
- **THEN** the `version` column is defined using `DATETIME2`

#### Scenario: SQL Server rowversion is not used
- **WHEN** fixture consumers inspect the `dbo.PurchaseOrder` table definition
- **THEN** no `timestamp` or `rowversion` column is used for purchase order versioning

### Requirement: Fixture data anticipates future row comparison scenarios
The fixture SHALL seed left and right purchase order data that can support future row comparison tests without implementing comparison behavior in this change.
The data SHALL include cases for equal rows, differing domain values, rows present only on one side, and rows where only surrogate or technical values differ.

#### Scenario: Fixture includes matching business references
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases with the same domain values

#### Scenario: Fixture includes differing domain values
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases with different comparable domain values

#### Scenario: Fixture includes side-specific rows
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference exists only in the left database and at least one purchase order reference exists only in the right database

#### Scenario: Fixture includes future ignored-value example
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases where the meaningful domain values match but surrogate identity or version values differ

### Requirement: Fixtures are organized by table scenario
The SQL test fixtures SHALL organize each table-oriented fixture in its own subdirectory under `src/test/resources/sql/fixtures/`.
Tests SHALL use these fixture resources rather than creating comparison-table DDL inline.

#### Scenario: Positive purchase order fixture has its own directory
- **WHEN** tests initialize the successful `dbo.PurchaseOrder` scenario
- **THEN** they use resources from `sql/fixtures/purchase-order/`

#### Scenario: Missing business-key fixture has its own directory
- **WHEN** tests initialize the missing business-key scenario
- **THEN** they use resources from `sql/fixtures/purchase-order-without-business-key/`

#### Scenario: Ambiguous business-key fixture has its own directory
- **WHEN** tests initialize the ambiguous business-key scenario
- **THEN** they use resources from `sql/fixtures/ambiguous-business-key/`

### Requirement: Fixture includes table without business-key index
The purchase order comparison fixture SHALL include a realistic table that intentionally lacks any unique index whose name ends with `_BK`.
The table SHALL be available in both left and right logical databases so missing-business-key error handling can be tested against shared fixture resources.

#### Scenario: Negative fixture table exists in both databases
- **WHEN** the `purchase-order-without-business-key` fixture schema is initialized for the left and right logical databases
- **THEN** each database contains a `dbo.PurchaseOrderWithoutBusinessKey` table

#### Scenario: Negative fixture table has no BK-suffixed unique index
- **WHEN** fixture consumers inspect `dbo.PurchaseOrderWithoutBusinessKey`
- **THEN** the table has no unique index whose name ends with `_BK`

#### Scenario: Negative fixture table remains realistic
- **WHEN** fixture consumers inspect `dbo.PurchaseOrderWithoutBusinessKey`
- **THEN** the table includes plausible purchase-order-related columns such as `id`, `reference`, `status`, and `version`

#### Scenario: Missing business-key error can use fixture table
- **WHEN** the core comparison library is asked to compare `dbo.PurchaseOrderWithoutBusinessKey`
- **THEN** it can exercise the missing-business-key error path using the shared fixture rather than ad hoc test SQL

### Requirement: Obsolete sample-items fixture is removed
The test resources SHALL remove the original `sample_items` smoke fixture once realistic table-specific fixtures cover initialization and comparison test scenarios.

#### Scenario: Sample-items resources are not used
- **WHEN** the test suite is inspected
- **THEN** no test depends on `left-init.sql`, `right-init.sql`, or `dbo.sample_items`

