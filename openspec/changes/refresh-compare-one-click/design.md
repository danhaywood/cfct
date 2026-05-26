## Context

The command selection area already supports a Refresh control that reloads command rows, clears stale selections, auto-selects the latest `OK` command, and clears prior comparison progress.
Users currently need a second interaction to run compare after Refresh has restored a valid selection.
This change is a small workflow refinement in the existing webapp drawer behavior and compare orchestration path.

## Goals / Non-Goals

**Goals:**
- Make Refresh a one-click refresh-and-compare action when compare eligibility exists after refresh.
- Preserve existing latest-`OK` auto-selection behavior and disabled/guard behavior when compare is not eligible.
- Reuse the existing compare orchestration entrypoint instead of introducing a separate compare execution path.

**Non-Goals:**
- Changing compare business logic, table-diff semantics, or output rendering.
- Changing refresh keyboard shortcuts, baseline filtering semantics, or command-grid column/filter behavior.
- Introducing background retries or asynchronous queueing beyond existing compare execution flow.

## Decisions

Refresh will continue to run its existing state reset and command reload sequence before any compare attempt.
This keeps current safety behavior and avoids stale selection artifacts.

After refresh auto-selection completes, the UI will evaluate compare eligibility using the same enablement criteria already used by the Compare action.
If eligible, Refresh will invoke the same compare orchestration method currently used by Compare button and Enter default action.

If no `OK` command is available or no eligible business tables are selected after refresh, no compare will run.
This preserves predictable no-op behavior in non-runnable states.

Automated tests will be extended to assert both retained selection behavior and automatic compare trigger behavior.
This includes positive and negative paths so regressions in one-click flow are visible.

## Risks / Trade-offs

[Risk] Auto-triggered compare may surprise users expecting refresh-only behavior. → Mitigation: constrain trigger to existing compare-eligible state and document behavior in spec/tests.
[Risk] Triggering compare too early could race with selection propagation. → Mitigation: run compare only after refresh selection pipeline finishes and compare enablement is re-evaluated.
[Risk] Duplicate orchestration paths could diverge. → Mitigation: invoke the same compare entrypoint used by existing Compare action.
