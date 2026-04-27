## MODIFIED Requirements

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
