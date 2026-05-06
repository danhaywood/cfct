## Context

The current multi-table comparison flow processes selected tables serially and emits progress aligned to request order.
The current webapp compare feedback model is primarily footer-oriented and does not map completion state back to individual table rows.
This change spans core comparison orchestration, progress event semantics, and webapp selection-state visualization.
The implementation must preserve deterministic final result ordering for downstream renderers and exports even when execution is concurrent.
The implementation must avoid stale UI cues when users clear selections or start a new selection workflow after a run.

## Goals / Non-Goals

**Goals:**
- Execute selected table comparisons concurrently with a bounded worker pool to reduce wall-clock duration for multi-table runs.
- Keep final multi-table result payloads deterministic in selected-table order even when completion events arrive out of order.
- Publish per-table completion progress suitable for immediate UI updates as each table finishes.
- Surface row-level completion background cues and a live `completed of total` counter near Compare.
- Reset visual progress cues consistently on Clear and on selection-parameter changes.

**Non-Goals:**
- Introducing cancellation of in-flight table comparisons once execution has started.
- Changing single-table comparison semantics, diff algorithms, or output schemas.
- Reworking the full layout of the selection drawer beyond required progress affordances.

## Decisions

- Use a fixed-size executor-based concurrency model for table comparisons.
  Rationale: It provides explicit back-pressure and predictable resource usage compared to unbounded async fan-out.
  Alternative considered: Parallel streams or unbounded completable futures were rejected due to weaker control over JDBC concurrency and observability.
- Separate execution-order from result-order by storing per-table futures keyed by table identity and assembling final results in original selection order.
  Rationale: This preserves deterministic downstream behavior while allowing completion-order progress updates.
  Alternative considered: Returning completion-order results was rejected because it would break existing assumptions in reports and tests.
- Treat completion and failure events as asynchronous completion notifications that increment completed count monotonically regardless of selection index.
  Rationale: The UI counter and row highlighting require true completion-time updates.
  Alternative considered: Retaining strict request-order completion notification was rejected because it hides early finishes and delays feedback.
- Model webapp progress cues as ephemeral per-run state keyed by selected table identity with `not-started`, `in-progress`, and `completed` markers.
  Rationale: Explicit state makes row class-name rendering and reset behavior deterministic.
  Alternative considered: Deriving row state only from footer messages was rejected because it cannot support stable per-row styling.
- Centralize reset logic in a single compare-visual-state reset path invoked by Clear and by selection-parameter change handlers.
  Rationale: A shared reset path prevents drift where one workflow leaves stale cues behind.
  Alternative considered: Duplicated ad hoc resets in each event handler were rejected due to regression risk.

## Risks / Trade-offs

- [Higher concurrent JDBC load could increase transient lock contention] → Mitigation: Bound worker count and keep pool size configurable with conservative default.
- [Asynchronous completion ordering can make tests flaky if assertions assume sequence] → Mitigation: Assert monotonic counts and set membership rather than strict completion order, while still asserting deterministic final result order.
- [UI theme/background cue may conflict with existing row styling and accessibility contrast] → Mitigation: Use theme-consistent class tokens and verify contrast in component-level tests.
- [Stale progress state could leak into subsequent selection runs] → Mitigation: Invoke shared reset hook on Clear and every selection-parameter mutation before enabling new compare actions.
