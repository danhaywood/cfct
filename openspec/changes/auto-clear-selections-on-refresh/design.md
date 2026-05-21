## Context

The command selection area already supports Refresh to reload command rows from the database-backed source.
The UI also maintains command selections, table selections, and comparison status across interactions.
Retaining those states after Refresh can leave stale selections that no longer represent current command data.

## Goals / Non-Goals

**Goals:**
- Ensure Refresh produces a clean post-refresh selection state.
- Prevent stale comparison state from persisting after data reload.
- Keep current filtering and reload mechanics intact.

**Non-Goals:**
- Changing how command rows are fetched.
- Redesigning the drawer layout or filter controls.
- Altering compare eligibility rules outside refresh-triggered reset behavior.

## Decisions

Refresh handling will perform a deterministic reset sequence in the UI state layer.
The sequence will reload commands, clear command selections, clear business-table selections, and clear comparison progress/reporting state.
The reset will be implemented in existing refresh event handling so behavior stays centralized.
No new persistence or backend interfaces are required because all affected state is in-memory UI/session state.

## Risks / Trade-offs

[Risk] Users may expect selection persistence across refresh for convenience. → Mitigation: keep Clear behavior and refresh semantics documented and consistent with stale-state prevention.
[Risk] Reset ordering bugs could temporarily show stale controls. → Mitigation: apply reset in one handler and validate via UI tests for selection and status states after refresh.
[Risk] Refresh with active filters might produce confusing empty views. → Mitigation: preserve existing filters while clearing only selections and status.
