## Why

The footer status area currently shows `connectionStatusState` and `connectionStatusSummary`, but these values are no longer needed for user workflows.
Stale comparison progress text can remain visible after users change drawer selections or press Clear, which creates misleading status feedback.

## What Changes

- Remove `connectionStatusState` and `connectionStatusSummary` rendering from the main footer status panel.
- Keep `comparisonProgressSummary` as the single status message channel for active and terminal comparison feedback.
- Clear any previously rendered comparison progress status when users press Clear in the command section.
- Clear any previously rendered comparison progress status when users change command-grid or business-table selection parameters in the navigation drawer.
- Add explicit success and failure background styling for `comparisonProgressSummary` so terminal outcome is visually obvious.
- Update automated UI tests and snapshots to assert the new footer behavior.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-main-ui-layout`: Footer status content now excludes SQL connectivity state/summary and uses outcome-aware progress styling.
- `webapp-command-selection-grid`: Clear and command-side selection-parameter changes reset prior comparison progress reporting.
- `webapp-manual-table-selection`: Manual table-side selection-parameter changes reset prior comparison progress reporting.
- `webapp-playwright-connectivity-status`: Browser tests and snapshots assert the updated status footer behavior without connection status labels.

## Impact

This change affects `cfct-webapp` main view status rendering and selection event handlers.
This change affects Playwright UI assertions and baseline screenshots that currently expect connectivity status fields.
No API contract or persistence schema changes are expected.
