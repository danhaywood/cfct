## MODIFIED Requirements

### Requirement: Webapp exposes a command catalog grid for selection
The webapp SHALL display command-log entries in a Vaadin Grid in the left navigation area.
The command grid SHALL allow selecting one or more command rows.
The command selection state SHALL be available as input to subsequent selection or comparison orchestration.
The command grid SHALL display visible command identity columns in the order `timestamp`, `member`, `interactionId`.
The command grid SHALL support keyboard-first interaction when focused.
The command grid SHALL allow Space key toggling for the currently focused command row.
The command grid SHALL preserve useful Up/Down and Left/Right arrow key navigation behavior.

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

#### Scenario: Space toggles focused command row
- **WHEN** keyboard focus is on a command row in the command grid and user presses Space
- **THEN** that command row selection toggles through the same state path as checkbox interaction

#### Scenario: Arrow key navigation remains useful
- **WHEN** keyboard focus is in the command grid
- **THEN** Up/Down and Left/Right keys move focus in a predictable grid-navigation manner without breaking row selection state
