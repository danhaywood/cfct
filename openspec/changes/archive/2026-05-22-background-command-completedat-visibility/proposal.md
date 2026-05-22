## Why

Commands with replay state `UNDEFINED` currently hide meaningful background-progress state from users.
Users need to see when background commands finish, and they need grid columns that expose completion timing.

## What Changes

- Extend command catalog entry representation and command grid columns to include `completedAt` immediately after `timestamp`.
- Map `UNDEFINED` replay-state rendering to `BGRND:PEND` when `completedAt` is empty.
- Map `UNDEFINED` replay-state rendering to `BGRND:DONE` when `completedAt` is populated.
- Preserve existing rendering for `OK`, `PENDING`, and `FAILED` command states.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Command grid identity columns and replay-state presentation are updated to surface background completion state via `completedAt` and derived labels.

## Impact

Affects `cfct-webapp` command catalog query mapping, command entry model, and command grid rendering.
Affects command-grid unit/UI tests that assert column order and replay-state presentation.
No API contract or dependency changes are expected.
