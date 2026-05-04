## Context

The command selection section supports command-driven table auto-selection and currently exposes filter controls outside the grid.
Users now need the command table to show replay state directly, default to chronological ordering, and place filter controls inside the table similar to comparison result grids.
The change should preserve current selection semantics while improving scanability and filter ergonomics.

## Goals / Non-Goals

**Goals:**
- Default command-grid ordering to timestamp ascending.
- Add `replayState` as a visible command-grid column.
- Render command identity columns in the order `replayState`, `member`, `timestamp`, `interactionId`.
- Add replay-state filtering using a checkbox and conditional dropdown with values `PENDING`, `OK`, and `FAILED`.
- Move command filter controls into a command-grid header filter row.
- Combine replay-state filtering with member and interaction filters in one predicate path.

**Non-Goals:**
- Change command selection persistence or selection semantics.
- Modify command footprint resolution or business-table auto-selection behavior.
- Introduce backend query or API contract changes for filtering.

## Decisions

1. Apply default timestamp ascending sort on the command grid at render time.
Rationale: A deterministic default order keeps chronological analysis consistent without requiring extra user action.
Alternative considered: rely on user-triggered sorting each time; rejected because it adds unnecessary friction.

2. Add `replayState` as the first visible command identity column.
Rationale: Replay triage needs state visibility before deeper identity fields.
Alternative considered: append replay state after interaction ID; rejected because it reduces scanning efficiency for status-first workflows.

3. Append command filter controls to a header row within the command grid.
Rationale: This aligns interaction patterns with comparison tables and keeps filter context colocated with columns.
Alternative considered: retain external filter fields; rejected because it fragments scanning and filtering behavior.

4. Keep replay-state filtering as opt-in via checkbox with conditional dropdown.
Rationale: Explicit enablement preserves existing behavior when replay-state filtering is not needed.
Alternative considered: always-on dropdown with empty sentinel value; rejected because opt-in is clearer for users and tests.

5. Keep selection state independent from visibility filtering.
Rationale: Filter changes should not mutate selected commands.
Alternative considered: clear filtered-out selections; rejected because it is destructive and surprising.

## Risks / Trade-offs

[Timestamp values might be string-sorted incorrectly if format changes] → Keep timestamp formatting stable and cover default-order expectations in tests.
[Header-row filtering can increase grid visual density] → Keep controls compact and use clear placeholders and labels.
[Users may forget replay-state filtering is enabled] → Keep checkbox visible next to replay-state selector in the header row.

## Migration Plan

Deploy as a UI-level behavior update with no schema or API migration.
Update unit and browser-level tests for command column order, inline header filters, replay-state filtering, and default ordering.
Rollback is straightforward by removing inline filter row changes and restoring previous command-grid layout and ordering defaults.

## Open Questions

No blocking open questions are currently identified.
