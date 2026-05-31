## Why

Users can leave `Select all` active and then choose a specific command, which creates an ambiguous selection state.
Users can also choose a command while a comparison is still running, which should cancel the active run to avoid stale or conflicting execution state.

## What Changes

- Update command selection behavior so choosing an individual command always clears any active `Select all` state.
- Update command selection behavior so choosing an individual command cancels any in-progress comparison before applying the new selection.
- Ensure UI state, execution state, and progress indicators remain consistent after cancellation and reselection.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Command row selection semantics are updated to clear bulk-selection state and trigger run cancellation when required.
- `datasource-based-comparison-execution`: Active comparison lifecycle is updated to support user-initiated cancellation when command selection changes.

## Impact

The web application command selection and execution orchestration code paths are affected.
UI interaction tests and comparison execution flow tests will need updates for the new cancellation and reselection behavior.
