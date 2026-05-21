## Why

The current `Differences only` label is ambiguous because it filters compared tables, not individual columns.
Users also need a quick way to focus grid columns that contain actual value differences for a selected table.

## What Changes

- Rename the existing results-stage checkbox label from `Differences only` to `Diff tables only`.
- Add a new results-stage checkbox labeled `Diff columns only`.
- Make `Diff columns only` enabled only when a table with differences is selected in results.
- When `Diff columns only` is checked, show only logical fields whose values differ in at least one displayed row for the selected table.
- Keep row filtering, tab filtering, and download behaviors otherwise unchanged.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-differences-only-results-filter`: Clarify that the existing checkbox filters tables and rename its user-visible label.
- `webapp-comparison-results-tabs`: Add column-level difference filtering control and enablement rules in results controls.

## Impact

Affects results-stage UI labels, control-state logic, and per-tab grid column projection behavior.
Playwright and UI/component tests for results controls and column rendering will need updates.
