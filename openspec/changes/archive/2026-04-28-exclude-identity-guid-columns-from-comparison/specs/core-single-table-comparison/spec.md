## MODIFIED Requirements

### Requirement: Core library partitions columns for comparison
The system SHALL partition target-table columns into business-key columns, ignored columns, and compared columns.
Business-key columns SHALL be used for row matching.
Ignored columns SHALL be excluded from row matching and value comparison.
Compared columns SHALL include all remaining target-table columns.
The system SHALL always exclude identity-backed columns from compared columns.
The system SHALL always exclude columns named `uuid` or `guid` from compared columns using case-insensitive name matching.
The system SHALL always exclude columns with SQL Server datatype `UNIQUEIDENTIFIER` from compared columns.
Built-in technical-column exclusions SHALL apply in addition to caller-provided ignored-column options.

#### Scenario: Default ignored columns exclude technical identifiers
- **WHEN** the target table contains columns named `id`, `version`, `guid`, and `uuid`
- **THEN** default comparison options exclude `version` and built-in rules exclude identity-backed and guid/uuid technical columns from compared values

#### Scenario: Identity business-key column still matches rows
- **WHEN** a business-key index includes an identity-backed column
- **THEN** that column can still participate in row matching while remaining excluded from compared-value columns

#### Scenario: Uniqueidentifier datatype is excluded from compared values
- **WHEN** the target table contains a column with SQL Server datatype `UNIQUEIDENTIFIER`
- **THEN** that column is excluded from compared-value columns regardless of caller ignore options

#### Scenario: Non-key non-ignored business columns are compared
- **WHEN** the target table contains columns that are neither business-key columns nor ignored technical columns
- **THEN** those columns are compared for matched rows

### Requirement: PurchaseOrder fixture characterizes first comparison behavior
The system SHALL verify the core single-table comparison behavior using the existing realistic `dbo.PurchaseOrder` fixture.
The verification SHALL remain focused on the library and SHALL NOT introduce CLI or web behavior.

#### Scenario: PurchaseOrder comparison uses reference as business key
- **WHEN** the core library compares `dbo.PurchaseOrder` in the left and right fixture databases
- **THEN** it uses `PurchaseOrder_PK(reference)` to match rows

#### Scenario: PurchaseOrder comparison excludes identity by default
- **WHEN** the core library compares `dbo.PurchaseOrder` rows with matching references
- **THEN** differences limited to identity values do not produce row differences unless additional compared business columns differ

#### Scenario: PurchaseOrder comparison reports expected fixture differences
- **WHEN** the core library compares the left and right `dbo.PurchaseOrder` fixture data
- **THEN** the result reports expected side-only and differing business-domain values while excluding technical identity/GUID-only noise
