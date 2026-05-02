## Why

Users need a quick way to reset staged selections during exploration.
The current flow requires manually unchecking command rows and business table rows.
That manual reset is slower and can leave stale selections in one grid.

## What Changes

Add a Clear button below the command selection grid.
When clicked, clear all selected command rows.
When clicked, clear all selected business table rows.
Disable the Clear button when no rows are selected in either grid.
Keep compare enablement and downstream behavior consistent with an empty selection state.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Add a clear-selection action control and disablement rules.
- `webapp-manual-table-selection`: Support bulk clear behavior initiated from command-section controls.

## Impact

This change affects drawer controls and selection-state orchestration in `MainView`.
Unit and browser tests must verify clear behavior across both grids.
