## Why
Refreshing the command catalog currently clears selections and leaves users without an immediately actionable command row.
Users who want to compare recent successful work should be guided to the newest completed successful command after refresh.

## What Changes
Update refresh behavior to auto-select exactly one command row after reload.
Choose the most recent command with replay state `OK`.
Keep focus on the selected command row after refresh.
If no `OK` command exists, keep command selection empty and preserve existing focus fallback behavior.

## Capabilities
### Modified Capabilities
- `webapp-command-selection-grid`: Refresh behavior now auto-selects the latest completed successful command and keeps focus on that row.

## Impact
Affects `cfct-webapp` command refresh flow and command-grid focus/selection state management.
Affects command-grid unit/UI tests that currently assert refresh clears command selection.
No database schema changes are required.
