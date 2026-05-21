## Context

The results stage already supports compared-table filtering and a checkbox that hides unchanged tables.
That existing checkbox is labeled `Differences only`, which can be confused with row-level or column-level filtering.
The result grid already computes per-field equality or difference to decide single shared columns versus `L:` and `R:` paired columns.

## Goals / Non-Goals

**Goals:**
- Make table-level filtering intent explicit by renaming the existing checkbox to `Diff tables only`.
- Introduce `Diff columns only` to hide unchanged logical fields for the active result tab.
- Gate `Diff columns only` so it is enabled only when the active table has differences.

**Non-Goals:**
- Changing table-tab difference detection semantics.
- Changing row-classification semantics or row-level filtering defaults.
- Changing export payload structure.

## Decisions

Keep table-level filtering behavior unchanged and apply only a label rename for that control.
Add an independent boolean UI state for `diffColumnsOnly` scoped to results exploration.
Compute active-table eligibility from the selected tab result metadata indicating whether differences exist.
Disable and uncheck `Diff columns only` when the selected tab has no differences or when no table tab is selected.
When enabled and checked, derive visible logical fields by scanning displayed comparison rows and retaining only fields with at least one unequal left/right value in that table result.
Preserve deterministic column ordering by applying filtering as a projection over the existing ordered field list.

## Risks / Trade-offs

[Risk] Column-difference derivation per tab may add compute overhead on large results. → Mitigation: compute once per selected tab and reuse until tab or row-filter context changes.
[Risk] Users may expect `Diff columns only` to include side-only structural fields. → Mitigation: document that visibility is based on value differences represented in row comparison output.
[Risk] Control-state transitions across tab switches may feel inconsistent. → Mitigation: define deterministic reset and disable rules tied to active-tab eligibility.
