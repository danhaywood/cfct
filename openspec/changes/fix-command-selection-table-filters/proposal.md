## Why

Selecting commands should immediately surface the corresponding selected business tables when `Selected only` is enabled.
Users currently report that the grid sometimes appears stale until they manually toggle `Selected only`, which breaks trust in command-driven selection.
The results controls also include redundant toggles that no longer match current filtering behavior.

## What Changes

- Make command-driven table auto-selection update the visible business table grid deterministically on initial command selection without requiring a `Selected only` toggle.
- Remove the `Show MATCH rows` checkbox from the results stage because MATCH rows are already filtered out by default behavior.
- Change the `Differences only` results filter default to unchecked so users initially see all compared tables.
- Preserve existing filtering composition behavior for results controls after these UI-default changes.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-manual-table-selection`: Tighten command-driven visibility synchronization so newly selected business rows are shown immediately under `Selected only`.
- `webapp-comparison-results-tabs`: Remove the explicit `Show MATCH rows` toggle from result controls while keeping MATCH rows excluded by default.
- `webapp-differences-only-results-filter`: Change initial `Differences only` checkbox state from checked to unchecked.

## Impact

This change affects webapp UI state management for command selection and business-table visibility synchronization.
This change affects results-stage control rendering and default initialization of comparison filters.
Expected code touch points are in `cfct-webapp` drawer selection orchestration and results controls/view-model wiring.
No API contract or external dependency changes are expected.
