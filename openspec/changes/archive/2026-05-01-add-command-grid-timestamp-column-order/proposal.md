## Why

The command grid currently omits explicit timestamp display and does not present columns in the most useful execution-triage order.
We need timestamp-first ordering so users can quickly scan recent command activity and then identify member and interaction details.

## What Changes

- Extend the webapp command selection grid to display command timestamp values.
- Set command grid visible column order to `timestamp`, `member`, `interactionId`.
- Preserve existing command selection behavior and filtering behavior while aligning filters to the visible identity fields.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Update command-grid presentation requirements to include timestamp column and deterministic visible column order.

## Impact

This change affects command-grid UI rendering and related webapp tests in `sqlcomparer-webapp`.
No backend schema changes or API contract changes are required.
