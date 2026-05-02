## Context

The main drawer workflow already enables and disables Compare based on selected eligible tables.
Keyboard users can reach selection controls quickly but currently need extra navigation to trigger compare.
The webapp already has keyboard handlers in the command-grid area, so Enter behavior must avoid conflict with existing key handling and form inputs.

## Goals / Non-Goals

**Goals:**
- Execute compare through the existing compare action path when Enter is pressed and compare is enabled.
- Scope Enter handling to the main selection workflow so text-field editing and login flow behavior remain intact.
- Keep behavior deterministic for unit and Playwright assertions.

**Non-Goals:**
- Adding global keyboard shortcuts outside the selection workflow is out of scope.
- Changing comparison orchestration, output formats, or progress rendering is out of scope.
- Reworking focus management beyond what is needed for reliable Enter activation is out of scope.

## Decisions

Use a single Enter keyboard handler in `MainView` that delegates to the same method used by the Compare button click path.
Gate Enter activation on the same conditions as the Compare button enabled state so disabled compare remains a no-op.
Ignore Enter events originating from login-modal and filter text-input contexts to avoid accidental compare execution while typing.
Add test hooks and assertions that prove Enter invokes compare only in eligible state and does not invoke compare when disabled.

## Risks / Trade-offs

[Enter handler conflicts with text input controls] → Filter by event target and bypass handling for text-field/editor contexts.
[Duplicate compare invocation from rapid key repeat] → Reuse existing compare-button disablement during execution as an execution guard.
[Shortcut discoverability remains low] → Document Enter behavior in README user-facing workflow notes.
