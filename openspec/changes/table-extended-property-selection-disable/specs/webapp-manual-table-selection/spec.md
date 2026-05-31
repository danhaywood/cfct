## ADDED Requirements

### Requirement: Manual table grid supports metadata-driven table exclusion
The webapp SHALL evaluate a configured SQL Server table-level extended property for each business table candidate.
The webapp SHALL treat configured truthy values as a table-ineligible decision.
The webapp SHALL keep metadata-excluded tables visible in the manual table grid.
The webapp SHALL render metadata-excluded tables as non-selectable in the same manner as other ineligible rows.
The webapp SHALL expose an eligibility tooltip reason that explains the table is excluded by metadata configuration.
Command-driven programmatic selection SHALL NOT select metadata-excluded tables.
Keyboard-driven selection interactions SHALL NOT select metadata-excluded tables.

#### Scenario: Truthy table-level extended property disables row selection
- **WHEN** a business table has the configured table-level extended property with a truthy value
- **THEN** the table row is shown as disabled and cannot be selected

#### Scenario: Metadata-excluded table shows explanatory tooltip
- **WHEN** a user hovers or focuses an ineligible row disabled by table-level metadata
- **THEN** the tooltip explains the table is excluded by metadata configuration

#### Scenario: Metadata-excluded table is ignored by command-driven selection
- **WHEN** command footprint resolution includes a table disabled by table-level metadata
- **THEN** that table remains unselected in the manual table grid
