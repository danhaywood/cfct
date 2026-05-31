## Why

Pressing Enter currently triggers Compare by default, which can start an expensive comparison when users intended to refresh command options first.
Making Refresh the default action reduces accidental comparisons and aligns Enter behavior with the safer preparatory action.

## What Changes

- Change the default Enter-triggered action in the command selection workflow from Compare to Refresh.
- Update UI behavior so the primary/default button state is assigned to Refresh instead of Compare.
- Preserve explicit Compare behavior when users click Compare directly.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-compare-default-action`: Change the default button and Enter key submission behavior so Refresh is the default action instead of Compare.

## Impact

- Affected UI module(s) that define default button/action handling in the webapp command selection screen.
- Affected keyboard submission behavior tied to Enter.
- Potentially affected UI tests that assert default action or Enter-triggered Compare behavior.
