# core-multi-table-comparison Specification

## Purpose
TBD - created by archiving change compare-multiple-tables. Update Purpose after archive.
## Requirements
### Requirement: Core library compares a caller-specified set of tables
The system SHALL provide a core library API that compares a caller-specified collection of SQL Server tables between a left JDBC connection and a right JDBC connection.
The API SHALL compare exactly the requested tables.

#### Scenario: Caller compares selected tables
- **WHEN** a caller provides left and right JDBC connections and a collection of table references
- **THEN** the core library compares each requested table

#### Scenario: Unrequested tables are not compared
- **WHEN** comparable tables exist in the databases but are not included in the request
- **THEN** the core library does not compare those unrequested tables

#### Scenario: Empty table set is rejected
- **WHEN** a caller requests a multi-table comparison with no table references
- **THEN** the core library fails with a clear validation error

### Requirement: Multi-table comparison reuses single-table comparison
The system SHALL compare each requested table using the same metadata discovery, business-key matching, ignored-column handling, and row-difference behavior as single-table comparison.

#### Scenario: Each table uses its own PK index
- **WHEN** a multi-table comparison includes multiple tables
- **THEN** each table comparison discovers and uses that table's own `_PK` unique index

#### Scenario: Each table uses updated default ignored columns
- **WHEN** a requested table contains technical columns such as `id` and `version`
- **THEN** that table comparison includes `id` by default and excludes only configured ignored columns such as `version` unless caller options override defaults

#### Scenario: Table metadata errors fail clearly
- **WHEN** one requested table cannot be compared because its metadata is invalid
- **THEN** the multi-table comparison fails with an error that identifies the failing table
### Requirement: Core library reports structured multi-table results
The system SHALL return a structured multi-table comparison result.
The result SHALL contain one single-table comparison result for each requested table.

#### Scenario: Result contains one entry per requested table
- **WHEN** a caller compares two table references
- **THEN** the multi-table result contains two table comparison results

#### Scenario: Result preserves request order
- **WHEN** a caller provides table references in a specific order
- **THEN** the multi-table result preserves that table order

### Requirement: Core library renders deterministic multi-table reports
The system SHALL provide a deterministic text renderer for structured multi-table comparison results.
The renderer SHALL be suitable for Approval tests and future CLI output.

#### Scenario: Report renders table sections in result order
- **WHEN** a multi-table comparison result is rendered as text
- **THEN** the report contains one section per table in the result order

#### Scenario: Report includes each table's single-table details
- **WHEN** a multi-table comparison result is rendered as text
- **THEN** each table section includes the same comparison context and row-difference details as a single-table report

