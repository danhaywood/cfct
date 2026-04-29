## Context

The webapp already provides the correct controls for selection, comparison execution, filtering, and output download.
However, the current placement mixes global actions (download full comparison output) with table-scoped exploration controls (filter/grid), and left-side Compare positioning appears visually lower than adjacent top navigation elements.
This change is purely cosmetic and should improve alignment and visual grouping while preserving current behavior.

## Goals / Non-Goals

**Goals:**
- Align the Compare action in the left drawer with the top navigation/hamburger-row visual rhythm using predictable top spacing.
- Reposition JSON/Excel download controls in the right comparison stage so they appear above filter/grid content and are right-aligned as global actions.
- Keep filter input visually associated with tab/grid exploration rather than global export actions.

**Non-Goals:**
- Changing compare execution logic, selection logic, or download payload generation.
- Introducing new actions or permissions.
- Redesigning broader AppLayout navigation structure.

## Decisions

- Introduce semantic layout containers for action grouping in the results stage.
  - Decision: split current action area into a top export-actions row (right-aligned) and a content-actions row for filter control.
  - Rationale: clarifies scope: export is global to current comparison run; filter is local to result browsing.
  - Alternative considered: keep one row and add spacing only.
  - Why not chosen: does not communicate action hierarchy clearly.

- Use CSS-based spacing for Compare alignment tied to top-bar rhythm.
  - Decision: apply a semantic class around the Compare action block with top margin approximating the toolbar toggle row height.
  - Rationale: low-risk visual adjustment without changing component structure.
  - Alternative considered: hardcode pixel offsets directly in Java style chains.
  - Why not chosen: less maintainable and harder to tune responsively.

- Preserve responsive behavior introduced for result grids.
  - Decision: ensure new action-row structure does not break existing responsive/overflow rules for tab content and grids.
  - Rationale: avoids regressions while refining visual hierarchy.
  - Alternative considered: defer responsiveness interactions to later.
  - Why not chosen: layout-only changes can still regress responsive behavior if ignored.

## Risks / Trade-offs

- [Risk] Added spacing could look too large/small across themes or viewport sizes.
  → Mitigation: use token-based spacing (`var(--lumo-size-*)`/`var(--lumo-space-*)`) and verify on representative sizes.

- [Risk] Refactoring result action containers could break existing tests/selectors.
  → Mitigation: keep stable test ids and update tests explicitly for new grouping.

- [Trade-off] Slightly more layout markup/classes in MainView.
  → Mitigation: use semantic names and keep action behavior unchanged.

## Migration Plan

No migration is required.
This is a UI layout and styling update only.
Rollback is a normal code revert if users prefer previous placement.

## Open Questions

No blocking open questions.
If needed later, top spacing can be tuned after user feedback from real viewport usage.
