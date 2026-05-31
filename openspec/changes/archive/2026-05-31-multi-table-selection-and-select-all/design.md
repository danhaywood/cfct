## Context

The webapp already supports single-row table selection and `Space` toggling for the focused eligible row.
Users now need fast multi-table workflows similar to the command selection table behavior.
Disabled or ineligible rows must remain protected from selection across all interaction modes.

## Goals / Non-Goals

**Goals:**
Enable additive multi-selection with `Shift+Click` and `Shift+Space` in the manual table selection grid.
Add a select-all control that selects only selectable rows and leaves disabled rows unchanged.
Keep selection state deterministic when users mix single-select, shift-select, and bulk selection actions.

**Non-Goals:**
Changing table eligibility rules or how rows become disabled.
Adding cross-page persistence for selection outside the existing session state.
Changing comparison execution semantics beyond the selected-table set input.

## Decisions

Use a shared selection-state update path for keyboard, pointer, and select-all actions to keep behavior consistent.
Track an anchor row for shift interactions so range extension semantics remain predictable across shift-triggered events.
Ignore disabled rows during range and bulk selection instead of failing the action so users get maximum useful selection in one step.
Represent the select-all checkbox as a tri-state view of selectable rows, where checked means all selectable rows are selected and indeterminate means partial selection.
Keep the select-all control scoped to the current selectable dataset so behavior stays aligned with visible grid state and existing filters.
Alternative considered was a separate bulk action button, but a checkbox better communicates persistent all-selected state and supports indeterminate feedback.

## Risks / Trade-offs

[Shift range behavior differs from user expectation in edge focus cases] → Define anchor update rules and cover them with keyboard and mouse interaction tests.
[Bulk selection may appear inconsistent when many rows are disabled] → Add clear UI messaging through disabled-row affordances and ensure disabled rows never toggle.
[Tri-state checkbox can drift from row state if updates bypass central logic] → Route every selection mutation through one reducer-style update function and assert derived checkbox state in tests.

## Migration Plan

Implement behind the current manual table selection UI path without schema or API changes.
Update existing UI interaction tests and add new test coverage for shift keyboard, shift pointer, and select-all with disabled rows.
If regressions appear, rollback by disabling the new interaction handlers and hiding the select-all control while retaining prior single-selection behavior.

## Open Questions

Should select-all apply only to currently visible rows when additional filters are active, or to all selectable rows in the loaded catalog.
Should `Shift+Space` toggle only the focused row plus range fill behavior, or strictly mirror the current command table interaction semantics in all edge cases.
