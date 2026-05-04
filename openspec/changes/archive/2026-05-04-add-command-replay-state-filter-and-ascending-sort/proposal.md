## Why

The command grid needs to support chronological investigation and replay triage workflows in the same place.
Users also need replay state visible in the grid and filter controls embedded directly in the table so filtering feels consistent with comparison grids.

## What Changes

- Change command grid default ordering so timestamp is shown in ascending order.
- Extend command grid visible columns to include `replayState`.
- Render command identity columns in the order `replayState`, `member`, `timestamp`, `interactionId`.
- Add a replay-state filter checkbox and dropdown with values `PENDING`, `OK`, and `FAILED`.
- Move command filtering controls into the command table header row rather than a separate filter row.
- Apply replay-state filtering together with existing member and interaction filters.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Update command ordering defaults and add replay-state filter controls and behavior.

## Impact

This change affects webapp command selection UI behavior, including default sort, filter controls, and command-grid filtering predicates.
It impacts command-grid view-model logic and webapp tests that assert command ordering and filter interactions.
No API, persistence, or external dependency changes are expected.
