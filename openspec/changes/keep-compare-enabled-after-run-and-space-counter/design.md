## Context

The selection drawer exposes a `Compare` button and a completed counter label during active runs.
Current behavior correctly disables `Compare` while execution is in progress, but post-run state can leave `Compare` disabled despite valid selected tables.
The completed counter is adjacent to the compare control and currently has slightly insufficient horizontal separation.

## Goals / Non-Goals

**Goals:**
- Ensure post-run `Compare` enablement is derived from current selection eligibility rather than terminal run state.
- Preserve in-progress run protection against repeated activation while execution is active.
- Add deterministic spacing between completed counter and `Compare` button.

**Non-Goals:**
- Redesigning compare-row structure beyond spacing adjustments.
- Changing row-completion semantics or footer status semantics.
- Changing execution orchestration behavior.

## Decisions

Model compare enablement from two predicates: run-active state and eligible-selected-table count.
Disable `Compare` only when run-active is true or eligible-selected-table count is zero.
On run completion, recompute enablement immediately from current selected eligible tables without requiring additional user interaction.
Apply explicit CSS gap or margin token between completed counter and `Compare` control in the shared compare-action row.
Keep spacing rule centralized in compare-row styling so layout remains stable across viewport sizes.

## Risks / Trade-offs

[Risk] Fast state transitions around run completion could briefly flicker enabled state. → Mitigation: update run terminal state and enablement in one UI state transaction.
[Risk] Spacing fix may vary by theme density. → Mitigation: use theme-consistent spacing token instead of hard-coded pixel magic where possible.
[Risk] Existing tests may encode old disablement behavior. → Mitigation: update tests to assert post-run enabled behavior when selections remain eligible.
