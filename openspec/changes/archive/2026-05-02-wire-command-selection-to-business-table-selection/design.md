## Context

The webapp already loads commands into the command grid and business tables into the manual table grid.
The backend already provides command-audit touched-table resolution for SQL Server.
The missing piece is orchestration that applies command footprint results to table selection state in the UI workflow.

## Goals / Non-Goals

**Goals:**
- Automatically select business table rows that correspond to touched tables for currently selected commands.
- Keep behavior stable for manual filtering, manual selection toggles, and compare readiness logic.
- Ensure deterministic updates when command selection changes.

**Non-Goals:**
- Introduce new resolver semantics beyond existing touched-table resolution.
- Change command grid visual structure.
- Remove user control over manual table selection.

## Decisions

Introduce a command-driven selection update path in comparison preparation orchestration.
On command selection change, collect selected interaction IDs and resolve touched tables for that command set.
Intersect resolved touched tables with currently visible and eligible business table rows.
Apply selected state updates in `ManualTableSelectionState` using table identity keys.
When no commands are selected, clear command-driven selections and retain only explicit manual selections according to existing behavior rules.
Treat unresolved or unmapped touched tables as no-op for grid selection so behavior remains robust.

## Risks / Trade-offs

[Frequent command toggles can trigger repeated resolver calls] → Cache or debounce interaction-set resolution in a follow-up if needed.
[Auto-selection may surprise users] → Keep visible table checkboxes and allow manual overrides after auto-selection.
[Resolver output may include tables filtered from the business grid] → Apply selection only to rows present in the business table catalog.

## Migration Plan

Add orchestration logic in webapp selection preparation service that couples command selections to table selections.
Extend table selection state APIs if needed to support programmatic selection updates by table reference set.
Update unit tests for selection state and service wiring.
Update browser test expectations to validate end-to-end command-to-table auto-selection behavior.
Run relevant webapp tests and verify no regressions in manual-only flows.

## Open Questions

Whether manual unchecking of an auto-selected table should persist while the originating command remains selected.
Whether this should default to union semantics across selected commands or support alternate modes in a future change.
