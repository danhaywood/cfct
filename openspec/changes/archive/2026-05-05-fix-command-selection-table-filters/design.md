## Context

The webapp drawer currently supports command-driven selection that programmatically selects business tables.
When `Selected only` is enabled, the visible table set is derived from selection state and active row filters.
A timing/order gap in selection and filter recomputation can leave newly selected rows hidden until users toggle `Selected only`.
The results stage still exposes `Show MATCH rows` even though MATCH rows are now treated as filtered-out-by-default behavior.
The `Differences only` control currently initializes checked, which hides unchanged tabs on first render.

## Goals / Non-Goals

**Goals:**
- Ensure command selection recomputation and business-grid visibility refresh happen in one deterministic update path.
- Remove `Show MATCH rows` from the rendered results controls and related state wiring.
- Initialize `Differences only` as unchecked while preserving its filtering semantics when toggled.
- Keep keyboard and selection behavior unchanged outside these specific defaults and control removals.

**Non-Goals:**
- No redesign of command-grid or business-grid layouts.
- No change to comparison engine classification logic for MATCH, DIFFERENT, or side-only rows.
- No API payload or backend contract changes.

## Decisions

- Use a single source of truth for business-table visibility derived from current selected command IDs, resolved touched-table union, and `Selected only` flag in one recompute transaction.
  This avoids split updates where selection state changes before visibility projection.
  Alternative considered was forcing a second explicit grid refresh after selection mutation, but this is more brittle and can regress on future state refactors.
- Remove the `Show MATCH rows` checkbox and its UI state from the results controls model.
  MATCH rows remain excluded by existing default row predicate behavior.
  Alternative considered was leaving the control disabled, but removing it avoids user confusion and dead controls.
- Change `Differences only` initial state to `false` in results view-model initialization.
  Existing filter predicate composition with table-name filtering remains unchanged.
  Alternative considered was a persisted per-user preference, but that adds storage scope and migration work not needed for this fix.

## Risks / Trade-offs

- [Risk] Tightening recompute ordering could introduce subtle selection flicker during rapid command multi-select changes.
  → Mitigation: Cover with UI-level tests for single-select, multi-select, and deselect flows while `Selected only` is checked.
- [Risk] Removing `Show MATCH rows` may break tests or docs that still assert control presence.
  → Mitigation: Update integration/UI tests and docs in the same change.
- [Risk] Defaulting `Differences only` to unchecked can increase visible tabs for large runs.
  → Mitigation: Keep the toggle available and verify filtering remains performant.
