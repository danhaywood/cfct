## ADDED Requirements

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
