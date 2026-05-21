## Why

After a comparison completes, the `Compare` button can become disabled even though eligible tables remain selected.
The completed counter label next to `Compare` also needs a small spacing adjustment to avoid slight visual overlap.

## What Changes

- Keep the `Compare` button enabled after a run completes when one or more eligible tables are still selected.
- Continue disabling `Compare` only when there are no eligible selected tables or while a run is actively executing.
- Add explicit horizontal spacing between the completed counter label and the `Compare` button so they do not visually overlap.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-main-ui-layout`: Clarify post-run `Compare` enablement behavior.
- `webapp-manual-table-selection`: Define minimum visual separation between compare-adjacent completed counter and compare button.

## Impact

Affects compare-action state transitions and compare-row layout spacing in the navigation drawer.
Tests that assert compare enablement and compare-adjacent progress label rendering will need updates.
