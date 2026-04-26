## ADDED Requirements

### Requirement: Core library compares one named table
The system SHALL provide a core library API that compares one caller-specified SQL Server table between a left JDBC connection and a right JDBC connection.
The API SHALL be independent of CLI, web, and Spring-specific request concepts.

#### Scenario: Caller compares one table
- **WHEN** a caller provides left and right JDBC connections and a table reference
- **THEN** the core library compares only that referenced table

#### Scenario: Core API does not require CLI or web inputs
- **WHEN** a caller invokes the core comparison API
- **THEN** the caller is not required to provide command-line arguments, web request objects, or Spring-specific wrapper objects

### Requirement: Core library discovers business-key metadata
The system SHALL discover the target table's business key from SQL Server metadata using a unique index whose name ends with the configured business-key suffix.
The default suffix SHALL be `_BK`.

#### Scenario: BK-suffixed unique index identifies row key
- **WHEN** the target table has exactly one unique index whose name ends with `_BK`
- **THEN** the core library uses that index's key columns as the row matching key

#### Scenario: Composite business key metadata is represented
- **WHEN** the BK-suffixed unique index contains more than one key column
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

#### Scenario: Default ignored columns are excluded
- **WHEN** the target table contains columns named `id` and `version`
- **THEN** the default comparison options exclude those columns from value comparison

#### Scenario: Business-key columns identify rows
- **WHEN** rows are read from the left and right tables
- **THEN** business-key column values are used to determine whether rows represent the same business row

#### Scenario: Non-key non-ignored columns are compared
- **WHEN** the target table contains columns that are neither business-key columns nor ignored columns
- **THEN** those columns are compared for matched rows

### Requirement: Core library reports structured row differences
The system SHALL return a structured table comparison result.
The result SHALL distinguish rows only in the left table, rows only in the right table, and matched rows with differing compared column values.

#### Scenario: Row exists only on left
- **WHEN** a business-key value exists in the left table but not the right table
- **THEN** the result records that row as only in left

#### Scenario: Row exists only on right
- **WHEN** a business-key value exists in the right table but not the left table
- **THEN** the result records that row as only in right

#### Scenario: Matched row has differing values
- **WHEN** a business-key value exists on both sides and at least one compared column value differs
- **THEN** the result records the row key and each differing column with left and right values

#### Scenario: Matched row differs only in ignored values
- **WHEN** a business-key value exists on both sides and differences are limited to ignored columns
- **THEN** the result does not record a row difference for that business-key value

### Requirement: Core library renders deterministic text reports
The system SHALL provide a deterministic text renderer for structured single-table comparison results.
The renderer SHALL be suitable for Approval tests and future CLI output.

#### Scenario: Report includes comparison context
- **WHEN** a table comparison result is rendered as text
- **THEN** the report includes the table name, business-key index, business-key columns, compared columns, and ignored columns

#### Scenario: Report includes row difference sections
- **WHEN** a table comparison result contains rows only in left, rows only in right, or differing matched rows
- **THEN** the report renders those groups in a deterministic order

#### Scenario: Empty differences render deterministically
- **WHEN** a table comparison result contains no missing rows and no differing matched rows
- **THEN** the report renders a deterministic no-differences indication

### Requirement: PurchaseOrder fixture characterizes first comparison behavior
The system SHALL verify the core single-table comparison behavior using the existing realistic `dbo.PurchaseOrder` fixture.
The verification SHALL remain focused on the library and SHALL NOT introduce CLI or web behavior.

#### Scenario: PurchaseOrder comparison uses reference as business key
- **WHEN** the core library compares `dbo.PurchaseOrder` in the left and right fixture databases
- **THEN** it uses `PurchaseOrder_BK(reference)` to match rows

#### Scenario: PurchaseOrder comparison ignores id and version
- **WHEN** the core library compares `dbo.PurchaseOrder` rows with matching references
- **THEN** differences in `id` and `version` do not produce row differences

#### Scenario: PurchaseOrder comparison reports expected fixture differences
- **WHEN** the core library compares the left and right `dbo.PurchaseOrder` fixture data
- **THEN** the result reports the expected left-only row, right-only row, and differing domain values from the fixture
