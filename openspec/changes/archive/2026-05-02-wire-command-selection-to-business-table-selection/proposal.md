## Why

The command selection grid and business table selection grid currently operate independently.
Users must manually infer and select the affected business tables after choosing commands.
This creates avoidable effort and increases the risk of running comparisons with incomplete table coverage.

## What Changes

- Wire command selection to automatic table selection in the business table grid.
- When one or more commands are selected, resolve touched business tables and select those rows in the grid below.
- Keep manual business table selection available so users can add or remove tables after auto-selection.
- Define deterministic behavior for selection updates when commands are selected, deselected, or cleared.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Add command-driven table selection side effects.
- `webapp-manual-table-selection`: Support programmatic selection updates sourced from command footprint resolution.

## Impact

This change affects webapp selection state orchestration and comparison preparation wiring.
This change reuses existing touched-table resolver behavior and maps resolver output onto visible business table rows.
Unit and browser tests will be updated to verify command-to-table auto-selection behavior.
