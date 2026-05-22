## Why
Refreshing command data is currently mouse-driven.
Keyboard users need a fast accelerator to trigger Refresh without moving focus.

## What Changes
Add keyboard accelerator support for the command Refresh action.
Support `F5` and `Alt+R` as equivalent triggers for the existing Refresh control.
Keep existing Refresh behavior unchanged beyond trigger mechanism.

## Capabilities
### Modified Capabilities
- `webapp-command-selection-grid`: Command Refresh can be triggered by keyboard accelerators.

## Impact
Affects `cfct-webapp` command selection keyboard event handling and associated UI tests.
No backend or schema changes are required.
