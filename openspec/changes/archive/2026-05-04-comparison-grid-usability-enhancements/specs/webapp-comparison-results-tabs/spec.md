## ADDED Requirements

### Requirement: Result grid hides MATCH rows by default with explicit opt-in
The comparison result grid SHALL exclude rows classified as `MATCH` by default when a comparison result tab is first shown.
The comparison stage SHALL provide a `Show MATCH rows` checkbox that allows users to include MATCH rows on demand.
The comparison stage SHALL apply the checkbox state to the active result view without requiring a new comparison execution.

#### Scenario: MATCH rows are hidden by default
- **WHEN** a comparison run completes and a table result tab is opened
- **THEN** rows classified as `MATCH` are not shown in the initial grid view

#### Scenario: User enables MATCH rows
- **WHEN** the user checks `Show MATCH rows`
- **THEN** MATCH rows become visible in the current result grid view

### Requirement: Result grid supports sortable columns and value filtering
The comparison result grid SHALL allow sorting on visible data columns in ascending and descending order.
The comparison result grid SHALL provide value-filter controls for narrowing visible rows by column value.
Sorting and filtering SHALL be combinable in a single active result view.

#### Scenario: User sorts a result column
- **WHEN** the user activates sorting on a visible result column
- **THEN** the grid rows reorder according to the selected sort direction

#### Scenario: User filters by value
- **WHEN** the user enters a filter value for a result column
- **THEN** only rows matching the filter criteria remain visible in the grid

#### Scenario: Sorting and filtering work together
- **WHEN** one or more column filters are active and a sort is applied
- **THEN** the grid shows the filtered subset ordered by the selected sort rule