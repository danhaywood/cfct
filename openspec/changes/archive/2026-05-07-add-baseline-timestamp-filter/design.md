## Context

The webapp command-selection workflow already supports row selection and header-based filtering for member, interaction ID, and replay state.
Users now need a durable baseline cutoff so they can focus on newer commands without repeatedly reapplying multiple filters.
The baseline must be visible near the command grid, directly editable, and quickly set from a known command row.

## Goals / Non-Goals

**Goals:**
- Provide an optional baseline timestamp field above the command selection grid.
- Allow manual editing of the baseline timestamp value.
- Provide a command-grid context menu action that sets the baseline from the selected row timestamp.
- Ensure command rows shown in the grid are restricted to timestamps after the baseline when a baseline is present.
- Keep current behavior unchanged when no baseline is set.

**Non-Goals:**
- Changing command persistence schema or replay-state semantics.
- Adding server-side user preference persistence for baseline values.
- Introducing timezone-conversion UX beyond the timestamp format already used by the grid.

## Decisions

Use a single baseline value in the command-grid view model and treat it as nullable optional state.
Use the same timestamp type already used in command row data to avoid format drift between display and filter logic.
Render the baseline input directly above the grid so filter intent remains colocated with command selection actions.
Apply baseline filtering in the same in-memory filtering pipeline as existing member, interaction ID, and replay-state filters so combined filters stay deterministic.
Add a row context-menu action that copies the selected row timestamp into baseline state and immediately re-evaluates filters.
Filter semantics use strict `>` comparison so the baseline row itself is excluded, matching the requirement to list only commands after the baseline timestamp.

## Risks / Trade-offs

[Timestamp parsing ambiguity] → Mitigation: accept only the existing command timestamp format and show validation feedback for invalid edits.
[User confusion about hidden rows] → Mitigation: keep baseline field visible at all times and allow quick clearing to restore full list.
[Context-menu discoverability] → Mitigation: label action explicitly as setting baseline from selected command timestamp.
[Edge-case timezone mismatch] → Mitigation: use the same normalized timestamp representation across row rendering and filter comparison.
