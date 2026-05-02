# extended-properties-ignore-column-advisor Specification

## Purpose
TBD - created by archiving change add-extended-properties-ignore-column-advisor. Update Purpose after archive.
## Requirements
### Requirement: Extended-property advisor can ignore columns by SQL Server metadata
The system SHALL provide an ignore-column advisor that reads SQL Server column-level extended properties.
The advisor SHALL inspect metadata attribute `cfct.ignored` for each candidate column.
The advisor SHALL treat configured truthy values as an ignore decision.
Truthy evaluation SHALL be case-insensitive.
The advisor SHALL be independently enable/disable configurable and SHALL default to enabled.

#### Scenario: Truthy extended property marks column ignored
- **WHEN** a column has extended property `cfct.ignored` with truthy value
- **THEN** the advisor marks the column as ignored

#### Scenario: Missing or non-truthy extended property does not ignore
- **WHEN** a column has no `cfct.ignored` property or has a non-truthy value
- **THEN** the advisor does not mark the column ignored

