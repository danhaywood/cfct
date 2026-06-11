## Context

The command selection grid currently allows row-level selection while bulk `Select all` can remain active.
That creates an inconsistent model where the UI implies both all commands and a single command are selected.
The comparison runner can also remain active during command reselection, which risks outdated progress and result state being presented.
The change must coordinate UI selection state and comparison lifecycle state in one interaction path.

## Goals / Non-Goals

**Goals:**
- Make command row selection authoritative over bulk selection state.
- Cancel any active comparison before applying a newly selected command.
- Keep progress, busy flags, and result visibility synchronized with cancellation and reselection.
- Preserve existing behavior when no comparison is running.

**Non-Goals:**
- Redesign bulk selection UI labels or layout.
- Introduce queued comparison execution.
- Change backend comparison algorithms or output formats.

## Decisions

- Route command row selection through a single handler that first clears bulk selection state, then checks for and cancels active comparison, and finally applies the selected command.
  This keeps selection and execution transitions deterministic in one code path.
- Treat cancellation on command selection as user-initiated cancellation using the same cancellation mechanism as explicit cancel actions.
  This avoids duplicate cancellation logic and keeps progress/reset side effects consistent.
- Reset transient comparison UI state after cancellation before starting or allowing the next compare action.
  This prevents stale in-progress indicators and partial results from surviving selection changes.
- Add or update UI interaction tests to cover two key flows: selecting a command after `Select all`, and selecting a command during an active comparison.
  This ensures regression protection for both state transitions.

## Risks / Trade-offs

- [Risk] Selection events may fire rapidly and race with cancellation completion.
  → Mitigation: Guard handler with idempotent cancellation checks and ignore stale completion callbacks.
- [Risk] Reusing existing cancellation flow may clear more UI state than needed for quick reselection.
  → Mitigation: Validate and scope state reset to transient execution fields only.
- [Risk] Additional orchestration in the UI layer can increase coupling between grid and execution controller.
  → Mitigation: Keep the orchestration entry point narrow and delegate cancellation/execution details to existing services.
