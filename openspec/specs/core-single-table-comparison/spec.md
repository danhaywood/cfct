# core-single-table-comparison Specification

## Purpose
TBD - created by archiving change add-core-single-table-comparison. Update Purpose after archive.
## Requirements
### Requirement: Core library compares one named table
The system SHALL provide a core library API module containing the public contracts needed to compare one caller-specified SQL Server table between a left JDBC connection and a right JDBC connection.
The system SHALL provide implementation services outside the API module that implement those public contracts.
The core library MAY use Spring wiring patterns such as components, services, and configuration in implementation modules.
The API SHALL be independent of CLI and web-specific request concepts.

#### Scenario: Caller compares one table
- **WHEN** a caller provides left and right JDBC connections and a table reference
- **THEN** the core library compares only that referenced table

#### Scenario: Core API does not require CLI or web inputs
- **WHEN** a caller invokes the core comparison API
- **THEN** the caller is not required to provide command-line arguments, web request objects, or CLI-specific wrapper objects

#### Scenario: Core API contracts are available without CLI module
- **WHEN** a caller depends on the API module
- **THEN** the caller can use public comparison request and result contracts without depending on the CLI module

#### Scenario: Core services can be Spring-managed
- **WHEN** the core comparison implementation is consumed from a Spring application context
- **THEN** its comparison services can be wired as Spring-managed beans

### Requirement: Core library discovers business-key metadata
The system SHALL discover the target table's business key from SQL Server metadata using a unique index or unique constraint whose name ends with the configured business-key suffix.
The default suffix SHALL be `_PK`.
The suffix match SHALL be case-insensitive.
The discovery logic SHALL accept prefixed or compound object names as long as the full identifier ends with the configured suffix.

#### Scenario: PK-suffixed unique index identifies row key
- **WHEN** the target table has exactly one unique index whose name ends with `_PK`
- **THEN** the core library uses that index's key columns as the row matching key

#### Scenario: PK-suffixed unique constraint identifies row key
- **WHEN** the target table has exactly one unique constraint whose name ends with `_PK`
- **THEN** the core library uses that constraint's key columns as the row matching key

#### Scenario: Compound PK-suffixed object name identifies row key
- **WHEN** the target table has a unique index or unique constraint named `PurchaseOrder__reference__PK`
- **THEN** the core library accepts it as matching the configured business-key suffix and uses its key columns for row matching

#### Scenario: Composite business key metadata is represented
- **WHEN** the PK-suffixed unique index or unique constraint contains more than one key column
- **THEN** the core library represents all key columns in ordinal order as the row matching key

#### Scenario: Missing business-key object fails clearly
- **WHEN** the target table has no unique index or unique constraint whose name ends with the configured business-key suffix
- **THEN** the comparison fails with an error that identifies the table and the missing business-key convention

#### Scenario: Ambiguous business-key objects fail clearly
- **WHEN** the target table has more than one unique index or unique constraint whose name ends with the configured business-key suffix
- **THEN** the comparison fails with an error that identifies the table and the ambiguous business-key objects

### Requirement: Core library partitions columns for comparison
The system SHALL partition target-table columns into business-key columns, ignored columns, and compared columns.
Business-key columns SHALL be used for row matching.
Ignored columns SHALL be excluded from row matching and value comparison.
Compared columns SHALL include all remaining target-table columns.
The system SHALL determine ignored technical columns through composed `IgnoreColumnAdvisor` SPI implementations.
The core library SHALL consult an injected `List<IgnoreColumnAdvisor>` and treat a column as ignored when any advisor marks it ignored.
Built-in default advisors SHALL preserve current technical-column behavior when all default advisors are enabled.
Default technical behavior SHALL exclude identity-backed columns from compared columns.
Default technical behavior SHALL exclude columns named `uuid` or `guid` from compared columns using case-insensitive name matching.
Default technical behavior SHALL exclude columns with SQL Server datatype `UNIQUEIDENTIFIER` from compared columns.
Default technical behavior SHALL also support ignoring columns flagged by SQL Server extended property `cfct.ignored` with truthy values.
Built-in and custom advisor exclusions SHALL apply in addition to caller-provided ignored-column options.

#### Scenario: Default advisors exclude technical identifiers
- **WHEN** the target table contains columns named `id`, `version`, `guid`, and `uuid`
- **THEN** default comparison options exclude `version` and default advisors exclude identity-backed and guid/uuid technical columns from compared values

#### Scenario: Identity business-key column still matches rows
- **WHEN** a business-key index includes an identity-backed column
- **THEN** that column can still participate in row matching while remaining excluded from compared-value columns

#### Scenario: Uniqueidentifier datatype is excluded from compared values
- **WHEN** the target table contains a column with SQL Server datatype `UNIQUEIDENTIFIER`
- **THEN** that column is excluded from compared-value columns regardless of caller ignore options

#### Scenario: Non-key non-ignored business columns are compared
- **WHEN** the target table contains columns that are neither business-key columns nor ignored technical columns
- **THEN** those columns are compared for matched rows

#### Scenario: Any advisor can mark a column ignored
- **WHEN** one advisor in the injected advisor list marks a column ignored and others do not
- **THEN** the column is partitioned into ignored columns

#### Scenario: Extended-property advisor ignores marked column
- **WHEN** a column has SQL Server extended property `cfct.ignored` set to a truthy value
- **THEN** that column is partitioned into ignored columns

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
- **THEN** it uses `PurchaseOrder_PK(reference)` to match rows

#### Scenario: PurchaseOrder comparison excludes identity by default
- **WHEN** the core library compares `dbo.PurchaseOrder` rows with matching references
- **THEN** differences limited to identity values do not produce row differences unless additional compared business columns differ

#### Scenario: PurchaseOrder comparison reports expected fixture differences
- **WHEN** the core library compares the left and right `dbo.PurchaseOrder` fixture data
- **THEN** the result reports expected side-only and differing business-domain values while excluding technical identity/GUID-only noise

