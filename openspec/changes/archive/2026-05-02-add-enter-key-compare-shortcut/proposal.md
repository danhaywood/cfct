## Why

Keyboard-driven users can select commands and business tables without touching the mouse but must still navigate to and click the Compare button.
Making Enter trigger compare when comparison is available reduces friction and aligns with common default-action behavior.

## What Changes

- Treat Compare as the default action in the main selection stage when at least one eligible table is selected.
- Trigger the same compare execution path when the user presses Enter in the main selection workflow context.
- Preserve existing disabled behavior so Enter does nothing when compare is not currently enabled.
- Add browser-level tests for Enter-triggered compare behavior and guard tests for the disabled state.

## Capabilities

### New Capabilities

- `webapp-compare-default-action`: Defines default-action semantics for Compare when keyboard users press Enter.

### Modified Capabilities

- `webapp-main-ui-layout`: Extend compare-action requirements to include Enter as a keyboard accelerator when compare is enabled.
- `webapp-command-selection-grid`: Clarify that command-driven table selection can be followed immediately by Enter-triggered compare when selection enables compare.

## Impact

This change affects keyboard event handling and action wiring in `cfct-webapp` main view.
This change affects webapp unit and Playwright tests covering compare activation paths.
No core comparison API or CLI behavior changes are expected.
