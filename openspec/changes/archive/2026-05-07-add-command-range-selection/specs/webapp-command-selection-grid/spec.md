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
The command grid SHALL support contiguous range selection using anchor-and-extend interaction.
The command grid SHALL treat the most recent non-Shift row-selection intent as the range anchor.
The command grid SHALL select all visible rows between anchor and target, inclusive, when users perform Shift-click on a target row.
The command grid SHALL apply contiguous range selection deterministically against the current visible row order after active sorting and filtering.

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

#### Scenario: Shift-click selects an inclusive contiguous range
- **WHEN** a user has an existing range anchor row in the command grid
- **AND** the user Shift-clicks a different target row
- **THEN** the command grid selects all visible command rows between anchor and target inclusively

#### Scenario: Shift-click range uses current filtered and sorted order
- **WHEN** command-grid filters or sorting are active
- **AND** the user performs Shift-click range selection
- **THEN** interval bounds are resolved from the current visible row ordering
