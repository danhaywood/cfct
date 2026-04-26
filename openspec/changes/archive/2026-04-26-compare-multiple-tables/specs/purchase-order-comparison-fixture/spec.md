## ADDED Requirements

### Requirement: Fixture set includes three good comparable tables
The SQL fixture set SHALL include three valid comparable table fixtures that each define a `_BK` unique index.
The fixtures SHALL be usable together in the left and right logical databases.

#### Scenario: PurchaseOrder remains a good comparable table
- **WHEN** the `purchase-order` fixture is initialized
- **THEN** `dbo.PurchaseOrder` has a unique `_BK` index and deterministic left and right data

#### Scenario: Supplier is a good comparable table
- **WHEN** the `supplier` fixture is initialized
- **THEN** `dbo.Supplier` has a unique `_BK` index and deterministic left and right data

#### Scenario: Product is a good comparable table
- **WHEN** the `product` fixture is initialized
- **THEN** `dbo.Product` has a unique `_BK` index and deterministic left and right data

### Requirement: Multi-table tests compare selected good fixtures
The integration tests SHALL use the good table fixtures to verify selected-table comparison.
The tests SHALL initialize three good comparable tables and compare two of them.

#### Scenario: Test initializes more tables than it compares
- **WHEN** the multi-table comparison integration test runs
- **THEN** it initializes three good comparable table fixtures

#### Scenario: Test compares selected subset
- **WHEN** the multi-table comparison integration test requests two table references
- **THEN** the comparison result includes those two tables and excludes the third initialized good table
