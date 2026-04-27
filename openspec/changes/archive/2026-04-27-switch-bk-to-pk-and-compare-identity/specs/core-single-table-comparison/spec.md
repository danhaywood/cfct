## MODIFIED Requirements

### Requirement: Core library discovers business-key metadata
The system SHALL discover the target table's business key from SQL Server metadata using a unique index whose name ends with the configured business-key suffix.
The default suffix SHALL be `_PK`.

#### Scenario: PK-suffixed unique index identifies row key
- **WHEN** the target table has exactly one unique index whose name ends with `_PK`
- **THEN** the core library uses that index's key columns as the row matching key

#### Scenario: Composite business key metadata is represented
- **WHEN** the PK-suffixed unique index contains more than one key column
- **THEN** the core library represents all key columns in ordinal order as the row matching key

#### Scenario: Missing business-key index fails clearly
- **WHEN** the target table has no unique index whose name ends with the configured business-key suffix
- **THEN** the comparison fails with an error that identifies the table and the missing business-key convention

#### Scenario: Ambiguous business-key indexes fail clearly
- **WHEN** the target table has more than one unique index whose name ends with the configured business-key suffix
- **THEN** the comparison fails with an error that identifies the table and the ambiguous indexes

### Requirement: Core library partitions columns for comparison
The system SHALL partition target-table columns into business-key columns, ignored columns, and compared columns.
Business-key columns SHALL be used for row matching.
Ignored columns SHALL be excluded from row matching and value comparison.
Compared columns SHALL include all remaining target-table columns.

#### Scenario: Default ignored columns exclude technical version only
- **WHEN** the target table contains columns named `id` and `version`
- **THEN** default comparison options exclude `version` from value comparison and include `id` in compared values unless explicitly ignored by caller options

#### Scenario: Business-key columns identify rows
- **WHEN** rows are read from the left and right tables
- **THEN** business-key column values are used to determine whether rows represent the same business row

#### Scenario: Non-key non-ignored columns are compared
- **WHEN** the target table contains columns that are neither business-key columns nor ignored columns
- **THEN** those columns are compared for matched rows

### Requirement: PurchaseOrder fixture characterizes first comparison behavior
The system SHALL verify the core single-table comparison behavior using the existing realistic `dbo.PurchaseOrder` fixture.
The verification SHALL remain focused on the library and SHALL NOT introduce CLI or web behavior.

#### Scenario: PurchaseOrder comparison uses reference as business key
- **WHEN** the core library compares `dbo.PurchaseOrder` in the left and right fixture databases
- **THEN** it uses `PurchaseOrder_PK(reference)` to match rows

#### Scenario: PurchaseOrder comparison includes identity by default
- **WHEN** the core library compares `dbo.PurchaseOrder` rows with matching references
- **THEN** differences in `id` values can produce row differences unless caller options explicitly ignore `id`

#### Scenario: PurchaseOrder comparison reports expected fixture differences
- **WHEN** the core library compares the left and right `dbo.PurchaseOrder` fixture data
- **THEN** the result reports the expected left-only row, right-only row, and differing domain values from the fixture
