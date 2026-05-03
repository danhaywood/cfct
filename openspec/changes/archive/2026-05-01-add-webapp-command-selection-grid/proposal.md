## Why

The webapp currently lets users manually select physical tables but it does not expose command-log rows as a first-class selection surface.
We need a command selection grid so users can pick one or more interactions and drive table scope from command/audit footprint data directly in the left navigation workflow.

## What Changes

- Add a command-log grid in the left navigation panel above the existing table-selection grid.
- Allow users to select one or more command rows from that grid.
- Add filtering controls so users can narrow command rows without a separate apply action.
- Add top spacing above the command grid block so the left panel has clearer visual rhythm.
- Keep existing table grid behavior available below the command grid.

## Capabilities

### New Capabilities
- `webapp-command-selection-grid`: Show command-log rows in a filterable selectable grid in the left navigation area.

### Modified Capabilities
- `webapp-main-ui-layout`: Extend left-panel layout to place the new command grid above the existing table grid with explicit spacer treatment.
- `webapp-manual-table-selection`: Clarify that manual table grid remains present below command selection and retains its current filtering and selection behavior.

## Impact

This change affects Vaadin webapp UI composition and selection-state orchestration in `cfct-webapp`.
It may add service calls in `cfct-impl` to load command-grid rows.
It does not introduce breaking API changes.
