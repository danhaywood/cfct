# webapp-command-selection-grid Specification

## Purpose
TBD - created by archiving change add-webapp-command-selection-grid. Update Purpose after archive.
## Requirements
### Requirement: Webapp exposes a command catalog grid for selection
The webapp SHALL display command-log entries in a Vaadin Grid in the left navigation area.
The command grid SHALL allow selecting one or more command rows.
The command selection state SHALL be available as input to subsequent selection or comparison orchestration.
The command grid SHALL display visible command identity columns in the order `timestamp`, `member`, `interactionId`.

#### Scenario: Command grid lists selectable command entries
- **WHEN** the home page initializes command-selection data
- **THEN** users see a command grid in the left navigation area with one row per available command interaction

#### Scenario: User selects multiple commands
- **WHEN** a user selects two or more command rows
- **THEN** the webapp retains all selected command interaction identifiers in deterministic selection state

#### Scenario: Command grid column order prioritizes recency context
- **WHEN** the command grid is rendered
- **THEN** visible columns include timestamp, member, and interactionId
- **AND** the visible identity-column order is timestamp first, then member, then interactionId

### Requirement: Command grid supports live filtering
The command grid SHALL provide filtering inputs for visible command identity columns.
Filtering SHALL narrow visible command rows without requiring a separate apply-filter action.

#### Scenario: User filters command rows by visible identity values
- **WHEN** a user enters filter text for a command identity field
- **THEN** only matching command rows remain visible in the grid without pressing an apply button

### Requirement: Command selection drives business table auto-selection
The webapp SHALL evaluate touched business tables for the currently selected commands.
The webapp SHALL update business table selections to include the resolved touched tables.
The webapp SHALL use union semantics across all selected commands.
The webapp SHALL allow users to press Enter to execute compare immediately after command-driven table selection has enabled compare.

#### Scenario: Selecting one command auto-selects touched business tables
- **WHEN** a user selects a command row in the command grid
- **THEN** the webapp resolves touched tables for that command interaction
- **AND** matching business table rows in the manual table grid are selected

#### Scenario: Selecting multiple commands unions touched business tables
- **WHEN** a user selects multiple command rows
- **THEN** the webapp resolves touched tables for each selected interaction
- **AND** the manual table grid selection contains the union of matching business tables

#### Scenario: Deselecting commands updates downstream table selection
- **WHEN** a previously selected command row is deselected
- **THEN** the webapp recomputes touched-table union for remaining selected commands
- **AND** the manual table grid reflects the recomputed union

#### Scenario: Clearing command selection clears command-driven table selection
- **WHEN** the user clears all command selections
- **THEN** no command-driven table selection remains active in the manual table grid

#### Scenario: Enter executes compare after command-driven selection enables compare
- **WHEN** command-driven table selection has produced at least one eligible selected table and the user presses Enter
- **THEN** compare execution starts using the currently selected eligible tables

### Requirement: Clear control resets command and business selections
The webapp SHALL render a Clear control below the command selection grid.
The Clear control SHALL clear selected rows in the command grid and the business table grid.
The Clear control SHALL be disabled when no rows are selected in either grid.

#### Scenario: Clear button appears below command grid
- **WHEN** the drawer is rendered
- **THEN** a clear selection control is visible below the command selection grid

#### Scenario: Clear button clears command and table selections
- **WHEN** one or more command rows or business table rows are selected
- **AND** the user clicks Clear
- **THEN** command selection becomes empty
- **AND** business table selection becomes empty

#### Scenario: Clear button disablement reflects empty state
- **WHEN** no command rows and no business table rows are selected
- **THEN** the Clear control is disabled
- **AND** compare action remains disabled until new eligible table selections exist

### Requirement: Command grid supports member ID filter before interaction ID filter
The webapp SHALL provide a member ID filter control in the command selection section.
The webapp SHALL render the member ID filter control before the interaction ID filter control.
The command grid SHALL apply both member ID and interaction ID filters when provided.

#### Scenario: Member filter appears before interaction filter
- **WHEN** the drawer command section is rendered
- **THEN** the member ID filter control appears before the interaction ID filter control

#### Scenario: Member filter narrows command rows
- **WHEN** the user enters a member ID fragment in the member filter
- **THEN** only command rows whose member ID contains the fragment are shown

#### Scenario: Member and interaction filters combine
- **WHEN** the user enters both member ID and interaction ID filters
- **THEN** only rows matching both criteria are shown

#### Scenario: Clearing filters restores command rows
- **WHEN** the user clears member and interaction filters
- **THEN** all command rows are shown again

