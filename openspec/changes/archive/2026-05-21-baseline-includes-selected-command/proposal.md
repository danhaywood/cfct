## Why

Using `Set baseline from selected command` currently excludes the selected command row from the visible command list.
This is confusing because users expect the command they picked as baseline to remain visible and become the first row in the filtered view.

## What Changes

- Adjust baseline filtering semantics so the selected baseline command is included in filtered command rows.
- Ensure command rows are filtered with inclusive baseline behavior for timestamp equality with the selected baseline command.
- Preserve ordering so the selected baseline command appears at the top of the resulting visible command list.
- Keep existing command filter composition behavior unchanged.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-command-selection-grid`: Baseline filtering now includes the selected baseline command and preserves deterministic top-row placement.

## Impact

Affects command-grid baseline filter behavior and related UI tests for command filtering.
No API, schema, or dependency changes are expected.
