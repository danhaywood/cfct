## Why

Users need to quickly capture command details from the command grid when raising tickets or bug reports.
Today this requires manual retyping or multiple copy steps, which is error-prone.

## What Changes

- Add an additional command-grid context-menu action to copy current row details to clipboard.
- Include both command member and command interaction GUID in copied content.
- Keep existing context-menu actions (such as baseline action) intact.
- Provide deterministic copied text format suitable for pasting into issue trackers.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-command-selection-grid`: Command-row context menu supports clipboard copy of member and command GUID.

## Impact

Affects command-grid context-menu behavior in the webapp UI and related browser/component tests.
No API or database schema changes are expected.
