## Context

The command selection area currently uses a free-text baseline timestamp field above the command grid.
Users must enter ISO timestamps manually, which is cumbersome and causes avoidable formatting errors.
Baseline filtering already supports strict "after baseline" semantics and integrates with command-grid filters and context-menu baseline assignment.
The design should improve entry ergonomics without changing filter meaning or downstream selection behavior.

## Goals / Non-Goals

**Goals:**
- Provide an explicit date/time picker UI for baseline input.
- Preserve existing baseline filter semantics and live filtering behavior.
- Preserve context-menu "set baseline from selected command" workflow.
- Keep command-filter change side effects unchanged, including progress-status reset behavior.

**Non-Goals:**
- No changes to command sorting, replay-state filters, or member/interaction filters.
- No changes to command-driven business-table selection semantics.
- No timezone conversion feature additions beyond current local timestamp handling.

## Decisions

Use a Vaadin date/time picker control for baseline input instead of free-form text entry.
This provides guided input and calendar/time selection while still allowing direct editing where supported.
Alternative considered was keeping text input with helper formatting hints, but guided entry better reduces invalid values.

Continue storing baseline as a `LocalDateTime` in selection state and keep strict "timestamp is after baseline" filtering.
This minimizes logic change and preserves compatibility with existing command timestamp values.
Alternative considered was converting to instant/timezone-aware comparisons, but this is outside current scope and data model.

Retain the context-menu baseline action and map selected command timestamp into picker value.
This keeps existing quick-baseline workflow and aligns picker and contextual selection paths.
Alternative considered was removing context-menu baseline once picker exists, but retaining both supports faster workflows.

Keep filter application eager so picker changes update visible rows immediately without apply buttons.
This preserves current live-filter UX and avoids introducing a second interaction model.
Alternative considered was deferred apply for performance, but current command-grid scale does not require that trade-off.

## Risks / Trade-offs

[Picker value format mismatch with existing timestamp strings] → Normalize conversion in one parsing/formatting path and add tests for picker and context-menu assignment.
[Regression in baseline clear behavior] → Preserve explicit null baseline handling and verify full-row restoration tests.
[Locale/date-time input differences across browsers] → Use Playwright checks against behavior outcomes (visible rows) rather than browser-specific typed string assumptions.

## Migration Plan

Implement picker-based baseline field in webapp UI and adapt baseline assignment/conversion logic.
Update existing tests and add picker-specific unit and Playwright coverage.
If regressions appear, roll back by restoring text-field baseline input while retaining current filtering logic.

## Open Questions

Should the picker expose seconds and milliseconds explicitly or align to minute-level interaction while still preserving strict comparison semantics.
