## Why

The command list can become stale when new commands are inserted by another process after the page has loaded.
Users currently need a full page refresh to see newly available commands.
This slows comparison workflows and can interrupt in-progress filter setup.

## What Changes

- Add a Refresh button that reloads the command list from the database on demand.
- Place the Refresh button on the same row as the baseline controls, positioned to the left of the two baseline fields.
- Rebind the command grid to the refreshed command dataset without requiring a full page reload.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Add an explicit command-list refresh control and placement requirement alongside baseline controls.

## Impact

This change affects the command selection UI layout and command data-loading flow in the webapp module.
This change affects UI and Playwright tests that validate command-grid filtering controls and interaction flows.
No API contract or database schema changes are expected.
