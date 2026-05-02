## Context

The UI now supports command-driven table selection and manual table selection together.
Users can accumulate selections in both grids while iterating on scope.
A single reset action is missing for returning to a clean state.

## Goals / Non-Goals

**Goals:**
- Provide a clear-selection action below the command grid.
- Clear both command and business table selections with one click.
- Disable the clear action when nothing is selected.

**Non-Goals:**
- Change command-to-table auto-selection semantics.
- Change compare execution behavior beyond reflecting empty selections.
- Add confirmation dialogs for the clear action.

## Decisions

Place a `Clear` button below the command selection grid in the drawer.
The clear handler resets command selection state and manual/programmatic business table selections.
The clear handler refreshes both data providers so checkbox UI reflects the cleared state immediately.
The clear button enabled state is derived from whether any command or business rows are selected.
Keep existing compare button test IDs and enablement semantics unchanged.

## Risks / Trade-offs

[Users may click clear accidentally] → Keep label explicit and disable when no selection exists.
[State divergence between model and grid checkboxes] → Recompute button state and refresh providers after clear.
[Future selection sources may be omitted from clear] → Centralize clear logic in one method in `MainView`.

## Migration Plan

Add clear button UI element below command grid with a stable test ID.
Implement a shared `clearAllSelections` flow in `MainView`.
Update selection state APIs if needed to support full reset.
Update unit and browser tests for enablement and full clear behavior.
Run targeted webapp tests to confirm no regressions.

## Open Questions

Whether a keyboard shortcut for clear should be added in a follow-up change.
