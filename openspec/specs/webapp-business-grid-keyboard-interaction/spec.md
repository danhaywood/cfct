# webapp-business-grid-keyboard-interaction Specification

## Purpose
TBD - created by archiving change add-space-toggle-business-table-grid. Update Purpose after archive.
## Requirements
### Requirement: Webapp supports keyboard-first manual business-grid selection
The manual business table grid SHALL support toggling selection of the focused eligible row via Space key.
The grid SHALL preserve predictable arrow-key focus navigation behavior while keyboard selection is active.
Space-key selection changes SHALL use the same manual-selection state path as checkbox click changes.

#### Scenario: Space toggles focused eligible business row
- **WHEN** the manual business table grid has focus on an eligible table row and user presses Space
- **THEN** selection state for that focused row is toggled

#### Scenario: Space does not select ineligible business row
- **WHEN** the manual business table grid has focus on an ineligible table row and user presses Space
- **THEN** selection state for that row remains unchanged

#### Scenario: Arrow keys preserve predictable grid focus navigation
- **WHEN** the manual business table grid has focus and user presses Up/Down or Left/Right arrows
- **THEN** focus movement follows predictable grid-navigation semantics without breaking selection behavior

