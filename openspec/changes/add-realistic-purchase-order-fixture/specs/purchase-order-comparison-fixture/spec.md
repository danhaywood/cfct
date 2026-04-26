## ADDED Requirements

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
