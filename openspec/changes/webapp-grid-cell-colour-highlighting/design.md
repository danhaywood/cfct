## Context

The webapp already renders per-table comparison results in an Excel-like Vaadin Grid with deterministic columns for equal and differing fields.
Row-level status cues exist, but users still need to inspect values manually to identify exactly which cells represent differences or side-only absence.
Excel export already communicates this visually with colour, so matching that style in the web UI improves consistency across output channels.
The implementation must preserve current column semantics and remain compatible with existing tabs/filter/download behavior.

## Goals / Non-Goals

**Goals:**
- Add deterministic cell-level visual highlighting for missing and differing values in the comparison grid.
- Reuse a small, explicit set of CSS class names driven by row/cell semantics so behavior is easy to test.
- Keep existing row classification, column generation, and result ordering behavior unchanged.

**Non-Goals:**
- Redesigning the overall layout or replacing Vaadin Grid.
- Changing comparison logic or how equal/different values are computed.
- Introducing user-configurable colour palettes in this change.

## Decisions

- Use semantic CSS classes applied by grid cell renderers instead of inline style values.
  - Decision: emit classes such as `cmp-cell-diff`, `cmp-cell-left-only`, and `cmp-cell-right-only` from value renderers.
  - Rationale: semantic classes keep style centralized in theme CSS and make UI assertions stable.
  - Alternative considered: set background colors inline in Java renderer code.
  - Why not chosen: harder to maintain, less reusable, and more brittle in tests.

- Derive cell class from existing row comparison model without changing domain contracts.
  - Decision: map current row-side and per-column difference metadata to class assignment in the webapp presentation layer.
  - Rationale: avoids cross-module API churn and keeps change scoped to web rendering.
  - Alternative considered: add extra flags into shared model objects.
  - Why not chosen: unnecessary coupling for a cosmetic concern.

- Keep equal shared-value cells unhighlighted and only mark attention-worthy cells.
  - Decision: apply highlight classes only for missing-value and differing-value states.
  - Rationale: reduces visual noise and aligns with the stated objective of spotting anomalies quickly.
  - Alternative considered: also style equal cells with a neutral fill.
  - Why not chosen: lowers contrast for true differences and may clutter dense grids.

## Risks / Trade-offs

- [Risk] Theme/CSS specificity conflicts could prevent highlight classes from taking effect.
  → Mitigation: use scoped selectors in the webapp theme and verify with component/Playwright checks.

- [Risk] Class assignment logic can drift from renderer behavior when columns evolve.
  → Mitigation: add tests that assert class presence for representative side-only and differing scenarios.

- [Trade-off] Colour-based cues can be less accessible for some users.
  → Mitigation: preserve existing textual structure (`L:`/`R:` columns and values) so meaning is not colour-only.

## Migration Plan

No migration is required.
The change is runtime UI rendering and theme styling only.
Rollback is a normal code revert if styling introduces regressions.

## Open Questions

No blocking open questions.
A follow-up can refine the exact colour palette if product feedback requests stronger contrast or accessibility adjustments.
