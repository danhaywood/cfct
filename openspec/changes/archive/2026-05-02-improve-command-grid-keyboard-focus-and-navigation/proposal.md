## Why

Keyboard-centric users currently need extra mouse interaction after login to start selecting commands.
Explicit post-login focus and predictable keyboard controls improve speed, accessibility, and operator ergonomics.

## What Changes

- After successful login, set initial focus to the command selection grid in the left navigation area.
- Enable Space key behavior to toggle selection for the currently focused command row.
- Ensure Up/Down arrow keys move row focus in the command grid predictably.
- Ensure Left/Right arrow keys behave appropriately for grid navigation context without breaking selection behavior.
- Add deterministic keyboard-focused tests for focus, toggle, and arrow interactions.

## Capabilities

### New Capabilities
- `webapp-command-grid-keyboard-interaction`: Defines keyboard-first focus and row-selection interaction behavior for command selection.

### Modified Capabilities
- `webapp-command-selection-grid`: Extend command grid requirements with keyboard focus, Space toggle, and arrow-key navigation semantics.
- `webapp-login-connection-auth`: Extend post-login transition requirements to place focus into command selection grid.

## Impact

This change affects Vaadin command-grid interaction handling in `cfct-webapp` main view.
This change affects login-success flow orchestration by setting deterministic post-login focus target.
This change affects webapp unit/browser tests that assert interaction behavior in command selection.
No core comparison engine or API contract changes are expected.
