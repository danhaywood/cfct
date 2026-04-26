## ADDED Requirements

### Requirement: Fixture includes table without business-key index
The purchase order comparison fixture SHALL include a realistic table that intentionally lacks any unique index whose name ends with `_BK`.
The table SHALL be available in both left and right logical databases so missing-business-key error handling can be tested against the shared fixture.

#### Scenario: Negative fixture table exists in both databases
- **WHEN** the purchase order fixture schema is initialized for the left and right logical databases
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
