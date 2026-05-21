## Why

Refreshing the command list currently keeps prior command and business-table selections.
Those retained selections can become stale against the refreshed dataset and cause accidental compare actions.

## What Changes

- Modify refresh behavior so activating Refresh clears command-grid and business-table selections before showing refreshed command rows.
- Clear any previously rendered comparison progress status report when Refresh is activated.
- Keep existing filter behavior and command reload behavior unchanged.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-command-selection-grid`: Refresh action now resets selection state and stale comparison status after reloading commands.

## Impact

Affects the webapp command-selection UI flow in the navigation drawer and related selection-state orchestration.
No API contract or dependency changes are expected.
