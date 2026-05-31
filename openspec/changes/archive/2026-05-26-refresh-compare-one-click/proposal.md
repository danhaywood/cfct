## Why

Refresh already helps users recover to a safe command selection state by auto-selecting the most recent `OK` command.
Users still must perform a second action to run compare, which adds friction to a frequent refresh-then-compare workflow.

## What Changes

- Update Refresh behavior so it still auto-selects the most recent `OK` command and then immediately triggers compare when compare eligibility is satisfied.
- Ensure the compare launch path used by Refresh is the same orchestration path as the existing Compare action.
- Keep existing guard behavior so no compare run is triggered when no eligible `OK` command or selectable business tables are available.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Refresh becomes a one-click refresh-and-compare operation while retaining latest-`OK` command auto-selection behavior.

## Impact

The left-drawer command selection workflow and refresh action handling will change.
Comparison orchestration wiring between refresh, command selection, and compare execution will be updated.
UI and integration tests covering refresh and compare behavior will need updates.
