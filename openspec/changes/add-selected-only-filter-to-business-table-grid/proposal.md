## Why

The current business table grid shows all business tables even when command-driven selection has already identified the most relevant subset.
This makes the normal workflow noisier and slows users who primarily compare tables touched by the selected commands.

## What Changes

- Add a `Selected only` checkbox control to the business table grid section.
- Default `Selected only` to checked so the grid initially shows only selected business tables.
- Apply the checkbox state as a live filter on table selection state.
- Allow users to uncheck `Selected only` to reveal all business tables and manually select additional tables for comparison.
- Preserve existing command-driven and manual row selection behavior while the selected-state filter is toggled.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-manual-table-selection`: Add selected-state filtering controls and default behavior for the manual business table grid.

## Impact

The primary impact is in the webapp selection drawer UI and its table-grid filtering logic.
This change affects selection-stage behavior in the Vaadin business table grid and its view-model state handling.
No API contract, persistence schema, or external dependency changes are expected.
