## Context

The command grid already supports live filtering and baseline-based narrowing.
Operators may add commands from another process while users keep the webapp open.
Without an explicit refresh control, the in-memory command list can drift from the latest database state.
The requested UX places refresh near baseline controls so users can refresh and immediately adjust baseline fields in one area.

## Goals / Non-Goals

**Goals:**
- Provide a visible Refresh control for command-list reload.
- Position Refresh on the baseline-controls row, to the left of the two baseline fields.
- Reload command rows from the backing data source without full-page navigation.

**Non-Goals:**
- No automatic polling or periodic background refresh.
- No change to command sorting defaults or existing filter semantics.
- No change to compare execution logic beyond consuming refreshed command rows.

## Decisions

Add a command-list Refresh button in the command selection section header row that contains baseline controls.
This keeps the control discoverable in the same area users already use for command-range narrowing.

Trigger the existing command query pipeline when Refresh is clicked and replace the grid items with the returned dataset.
This reuses current retrieval behavior and minimizes implementation risk.

Keep current filter control values after refresh and reapply them to the refreshed dataset.
This avoids surprising users by clearing active filtering context.

Preserve command selections that still exist in the refreshed dataset and drop selections for rows no longer present.
This keeps selection state consistent with the current data reality.

## Risks / Trade-offs

[Refresh introduces temporary UI ambiguity during reload] → Show a clear loading state and prevent duplicate rapid refresh clicks.
[Selection drift after reload] → Reconcile selections against refreshed row identities and remove missing rows deterministically.
[Layout crowding near baseline controls] → Keep compact button sizing and verify responsive behavior in drawer width constraints.

## Migration Plan

Implement the new refresh control and wire it to command-list reload in the webapp command selection view.
Add or update component tests for placement, reload behavior, and selection reconciliation.
Add or update Playwright coverage for end-to-end refresh visibility and command-row update behavior.

## Open Questions

Should a toast or inline status message confirm successful refresh and row-count change.
Should refresh keep keyboard focus on the button or return focus to the grid after reload.
