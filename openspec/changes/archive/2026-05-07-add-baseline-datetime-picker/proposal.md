## Why

Entering baseline timestamps manually is slow and error-prone, especially for precise values with date and time components.
A date/time picker will make baseline filter input faster, more consistent, and easier for users.

## What Changes

- Replace baseline free-text entry with a date/time picker control in the command selection section.
- Keep baseline filtering semantics unchanged: only commands strictly after baseline remain visible.
- Keep baseline context-menu action so selecting a command can still set the baseline value.
- Keep command filtering live, without requiring an explicit apply action.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Update baseline timestamp input requirements to support date/time picker interaction while preserving existing filter behavior.

## Impact

This change affects baseline filter UI rendering and baseline value parsing/normalization in the webapp command selection panel.
This change affects unit and Playwright tests that assert baseline filter behavior and command-grid filtering flows.
No external API or dependency changes are expected.
