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

