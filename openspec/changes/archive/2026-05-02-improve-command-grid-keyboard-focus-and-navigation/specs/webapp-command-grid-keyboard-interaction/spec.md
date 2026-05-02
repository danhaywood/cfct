## ADDED Requirements

### Requirement: Webapp supports keyboard-first command-grid operation after login
The webapp SHALL place focus on the command selection grid after successful login.
The focused command grid SHALL support Space to toggle selection of the currently focused row.
The focused command grid SHALL support arrow-key navigation for moving focus predictably across command rows and relevant cells.

#### Scenario: Focus moves to command grid after login
- **WHEN** a user completes login successfully
- **THEN** keyboard focus is placed on the command selection grid without requiring an extra click

#### Scenario: Space toggles focused command selection
- **WHEN** the command grid has focus on a command row and user presses Space
- **THEN** selection state for that focused command row is toggled

#### Scenario: Arrow keys navigate focused command entries
- **WHEN** the command grid has focus and user presses Up/Down or Left/Right arrows
- **THEN** focus movement follows predictable grid navigation semantics while preserving selection interaction behavior
