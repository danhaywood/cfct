# webapp-command-grid-keyboard-interaction Specification

## Purpose
TBD - created by archiving change improve-command-grid-keyboard-focus-and-navigation. Update Purpose after archive.
## Requirements
### Requirement: Webapp supports keyboard-first command-grid operation after login
The webapp SHALL place focus on the command selection grid after successful login.
The focused command grid SHALL support Space to toggle selection of the currently focused row.
The focused command grid SHALL support arrow-key navigation for moving focus predictably across command rows and relevant cells.
The focused command grid SHALL support Shift+Space to perform contiguous range selection from anchor to focused row.
The focused command grid SHALL initialize or rebase range anchor from the latest non-Shift selection intent.

#### Scenario: Focus moves to command grid after login
- **WHEN** a user completes login successfully
- **THEN** keyboard focus is placed on the command selection grid without requiring an extra click

#### Scenario: Space toggles focused command selection
- **WHEN** the command grid has focus on a command row and user presses Space
- **THEN** selection state for that focused command row is toggled

#### Scenario: Arrow keys navigate focused command entries
- **WHEN** the command grid has focus and user presses Up/Down or Left/Right arrows
- **THEN** focus movement follows predictable grid navigation semantics while preserving selection interaction behavior

#### Scenario: Shift plus Space selects contiguous range from anchor
- **WHEN** the command grid has a range anchor and keyboard focus on a different command row
- **AND** the user presses Shift+Space
- **THEN** all visible command rows between anchor and focused row are selected inclusively

#### Scenario: Shift plus Space without anchor behaves as focused selection
- **WHEN** no range anchor exists and command-grid focus is on a command row
- **AND** the user presses Shift+Space
- **THEN** the focused row is selected and becomes the range anchor

