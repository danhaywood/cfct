## Context
The Refresh button is present beside baseline controls but has no dedicated keyboard shortcut.

## Goals / Non-Goals
**Goals:**
Add low-friction keyboard activation for Refresh.
Preserve current refresh semantics and selection rules.
Avoid conflict with existing Enter/Space behavior.

**Non-Goals:**
Changing compare accelerator behavior.
Changing browser-global reload behavior outside app focus context.

## Decisions
Handle keydown events at the selection panel level where existing Enter handling is already wired.
Trigger refresh when either `F5` or `Alt+R` is pressed.
Prevent default browser reload behavior for in-app `F5` handling when the shortcut is consumed.
Ignore shortcuts while login dialog is open or when user is typing in text/date inputs.
Reuse existing `refreshCommandCatalog()` path to avoid duplicate logic.

## Risks / Trade-offs
`F5` can conflict with browser expectations.
Mitigate by only consuming when app context is active and refresh action is eligible.

## Validation
Add MainView tests for `F5` and `Alt+R` invoking refresh.
Add tests that shortcuts do not trigger while typing in input fields.
Run targeted command-selection tests.
