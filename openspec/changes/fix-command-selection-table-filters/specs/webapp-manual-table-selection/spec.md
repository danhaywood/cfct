## MODIFIED Requirements

### Requirement: Manual table grid accepts command-driven programmatic selections
The manual table grid SHALL accept programmatic selection updates from command footprint resolution.
Programmatic updates SHALL only affect rows that are present in the visible business table catalog.
Programmatic updates SHALL not fail when touched tables are unmapped or absent from the catalog.
When `Selected only` is checked, command-driven programmatic selections SHALL update visible grid rows so newly selected matches become visible.
When `Selected only` is checked, initial command selection and subsequent command selection changes SHALL refresh selection and visibility in the same update cycle.
Users SHALL NOT need to toggle `Selected only` to make newly command-selected business rows visible.
When command-driven recomputation deselects rows while `Selected only` is checked, those rows SHALL no longer remain visible.

#### Scenario: Command-driven selections apply only to visible business rows
- **WHEN** command footprint resolution returns a touched table set
- **THEN** only matching business table rows that exist in the manual table grid are selected
- **AND** unmatched touched tables are ignored

#### Scenario: Manual filtering remains stable with command-driven selections
- **WHEN** command-driven table selections are active
- **AND** the user applies or clears business table filters
- **THEN** selected-state consistency is preserved for the underlying selected table set

#### Scenario: Compare readiness reflects command-driven selected tables
- **WHEN** command-driven updates select one or more eligible business table rows
- **THEN** compare readiness is evaluated using the updated selected table set

#### Scenario: Command-driven selected rows stay visible with selected-only enabled
- **WHEN** `Selected only` is checked
- **AND** command selection changes produce a new touched-table union
- **THEN** rows that are newly selected by the command-driven update are visible in the business table grid

#### Scenario: Initial command selection shows selected rows without selected-only toggle
- **WHEN** `Selected only` is checked
- **AND** the user selects a command row for the first time in the current session state
- **THEN** corresponding selected business rows become visible immediately
- **AND** the user does not need to uncheck and re-check `Selected only`

#### Scenario: Command-driven deselected rows are hidden with selected-only enabled
- **WHEN** `Selected only` is checked
- **AND** command deselection recomputation removes previously selected business rows
- **THEN** those rows are no longer visible in the business table grid
