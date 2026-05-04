## MODIFIED Requirements

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
