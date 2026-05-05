# webapp-command-selection-grid Specification

## Purpose
TBD - created by archiving change add-webapp-command-selection-grid. Update Purpose after archive.
## Requirements
### Requirement: Webapp exposes a command catalog grid for selection
The webapp SHALL display command-log entries in a Vaadin Grid in the left navigation area.
The command grid SHALL allow selecting one or more command rows.
The command selection state SHALL be available as input to subsequent selection or comparison orchestration.
The command grid SHALL display visible command identity columns in the order `replayState`, `member`, `timestamp`, `interactionId`.
The command grid SHALL default to timestamp ascending row order.
The command grid SHALL support keyboard-first interaction when focused.
The command grid SHALL allow Space key toggling for the currently focused command row.
The command grid SHALL preserve useful Up/Down and Left/Right arrow key navigation behavior.

#### Scenario: Command grid lists selectable command entries
- **WHEN** the home page initializes command-selection data
- **THEN** users see a command grid in the left navigation area with one row per available command interaction

#### Scenario: User selects multiple commands
- **WHEN** a user selects two or more command rows
- **THEN** the webapp retains all selected command interaction identifiers in deterministic selection state

#### Scenario: Command grid column order includes replay state first
- **WHEN** the command grid is rendered
- **THEN** visible columns include replayState, member, timestamp, and interactionId
- **AND** the visible identity-column order is replayState first, then member, then timestamp, then interactionId

#### Scenario: Command grid defaults to ascending timestamp order
- **WHEN** the command grid is first rendered
- **THEN** command rows are ordered by timestamp ascending

#### Scenario: Space toggles focused command row
- **WHEN** keyboard focus is on a command row in the command grid and user presses Space
- **THEN** that command row selection toggles through the same state path as checkbox interaction

#### Scenario: Arrow key navigation remains useful
- **WHEN** keyboard focus is in the command grid
- **THEN** Up/Down and Left/Right keys move focus in a predictable grid-navigation manner without breaking row selection state

### Requirement: Command grid supports live filtering
The command grid SHALL provide filtering controls within a command-grid header row.
The command grid header row SHALL provide text filtering inputs for member and interactionId columns.
The command grid header row SHALL provide three replay-state filter checkboxes for `OK`, `PENDING`, and `FAILED`.
The replay-state checkboxes SHALL use compact labels `K`, `P`, and `F` mapped to `OK`, `PENDING`, and `FAILED` respectively.
The replay-state checkbox group SHALL be left-aligned within the replayState header filter cell.
Filtering SHALL narrow visible command rows without requiring a separate apply-filter action.
The command grid SHALL combine member, interactionId, and replay-state filters when one or more replay-state checkboxes are selected.
When no replay-state checkboxes are selected, replay-state filtering SHALL be inactive.

#### Scenario: User filters command rows by visible identity values
- **WHEN** a user enters filter text for member or interactionId in the command-grid header row
- **THEN** only matching command rows remain visible in the grid without pressing an apply button

#### Scenario: Replay-state filter checkboxes are shown in header row
- **WHEN** the command-grid header filter row is rendered
- **THEN** replay-state filter checkboxes for `K`, `P`, and `F` are visible in the replayState filter cell

#### Scenario: Single replay-state checkbox narrows command rows
- **WHEN** the user selects one replay-state checkbox
- **THEN** only command rows matching that replay state remain visible

#### Scenario: Multiple replay-state checkboxes combine with OR semantics
- **WHEN** the user selects two or more replay-state checkboxes
- **THEN** command rows matching any selected replay state remain visible

#### Scenario: Replay-state filter combines with text filters
- **WHEN** one or more replay-state checkboxes are selected and member or interactionId filters are also provided
- **THEN** only rows matching all active text filters and any selected replay state remain visible

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
The Clear control SHALL clear any previously rendered comparison progress status report.
The Clear control SHALL be disabled when no rows are selected in either grid.

#### Scenario: Clear button appears below command grid
- **WHEN** the drawer is rendered
- **THEN** a clear selection control is visible below the command selection grid

#### Scenario: Clear button clears command and table selections
- **WHEN** one or more command rows or business table rows are selected
- **AND** the user clicks Clear
- **THEN** command selection becomes empty
- **AND** business table selection becomes empty
- **AND** any prior comparison status report is cleared

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

### Requirement: Compare action remains unobstructed by business table selection content
The command and table selection layout SHALL reserve dedicated space for the `Compare` primary action.
The business table selection grid SHALL NOT overlap or visually obscure the `Compare` action row at any supported viewport size.
The `Compare` action SHALL remain fully visible and clickable while users interact with command and table selections.

#### Scenario: Business table grid does not overlap compare action
- **WHEN** the left navigation drawer renders command, business table, and compare controls
- **THEN** the business table selection grid ends before the compare action row
- **AND** no overlap exists between grid content and compare action controls

#### Scenario: Compare remains accessible during selection changes
- **WHEN** users scroll or resize within the selection area while table data is present
- **THEN** the compare action remains visible and actionable without being covered by selection-grid content

### Requirement: Command-grid selection parameter changes reset comparison status report
The webapp SHALL clear any previously rendered comparison progress status report when command-grid selection parameters change.
Command-grid selection parameters SHALL include command-row selection changes and command-grid filter changes.
The webapp SHALL preserve command and table selection behavior while clearing only stale comparison status reporting.

#### Scenario: Command selection change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user selects or deselects one or more command rows
- **THEN** the prior comparison status report is cleared

#### Scenario: Command filter change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user changes member, interactionId, or replay-state filter parameters
- **THEN** the prior comparison status report is cleared

