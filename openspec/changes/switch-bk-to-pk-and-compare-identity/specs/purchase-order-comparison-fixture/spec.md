## MODIFIED Requirements

### Requirement: Fixture marks the business key using a PK-suffixed index
The fixture SHALL identify the purchase order business key through a unique index whose name ends with `_PK`.
The indexed business key column SHALL be named `reference`.

#### Scenario: Business key index exists
- **WHEN** the `dbo.PurchaseOrder` table is created
- **THEN** it has a unique index named `PurchaseOrder_PK` over the `reference` column

#### Scenario: Business key keeps domain column name
- **WHEN** fixture consumers inspect the business key column
- **THEN** the column is named `reference` rather than being named with the `_PK` suffix

### Requirement: Fixture data anticipates future row comparison scenarios
The fixture SHALL seed left and right purchase order data that can support future row comparison tests without implementing comparison behavior in this change.
The data SHALL include cases for equal rows, differing domain values, rows present only on one side, rows where only version values differ, and rows where identity values differ.

#### Scenario: Fixture includes matching business references
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases with the same domain values

#### Scenario: Fixture includes differing domain values
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases with different comparable domain values

#### Scenario: Fixture includes side-specific rows
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference exists only in the left database and at least one purchase order reference exists only in the right database

#### Scenario: Fixture includes version-only difference example
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases where domain values and identity values match but `version` values differ

#### Scenario: Fixture includes identity-difference example
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases where domain values match but identity values differ

### Requirement: Fixture includes table without business-key index
The purchase order comparison fixture SHALL include a realistic table that intentionally lacks any unique index whose name ends with `_PK`.
The table SHALL be available in both left and right logical databases so missing-business-key error handling can be tested against shared fixture resources.

#### Scenario: Negative fixture table exists in both databases
- **WHEN** the `purchase-order-without-business-key` fixture schema is initialized for the left and right logical databases
- **THEN** each database contains a `dbo.PurchaseOrderWithoutBusinessKey` table

#### Scenario: Negative fixture table has no PK-suffixed unique index
- **WHEN** fixture consumers inspect `dbo.PurchaseOrderWithoutBusinessKey`
- **THEN** the table has no unique index whose name ends with `_PK`

#### Scenario: Negative fixture table remains realistic
- **WHEN** fixture consumers inspect `dbo.PurchaseOrderWithoutBusinessKey`
- **THEN** the table includes plausible purchase-order-related columns such as `id`, `reference`, `status`, and `version`

#### Scenario: Missing business-key error can use fixture table
- **WHEN** the core comparison library is asked to compare `dbo.PurchaseOrderWithoutBusinessKey`
- **THEN** it can exercise the missing-business-key error path using the shared fixture rather than ad hoc test SQL

### Requirement: Fixture set includes three good comparable tables
The SQL fixture set SHALL include three valid comparable table fixtures that each define a `_PK` unique index.
The fixtures SHALL be usable together in the left and right logical databases.

#### Scenario: PurchaseOrder remains a good comparable table
- **WHEN** the `purchase-order` fixture is initialized
- **THEN** `dbo.PurchaseOrder` has a unique `_PK` index and deterministic left and right data

#### Scenario: Supplier is a good comparable table
- **WHEN** the `supplier` fixture is initialized
- **THEN** `dbo.Supplier` has a unique `_PK` index and deterministic left and right data

#### Scenario: Product is a good comparable table
- **WHEN** the `product` fixture is initialized
- **THEN** `dbo.Product` has a unique `_PK` index and deterministic left and right data
