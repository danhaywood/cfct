## Context

The comparison stage already renders dynamic per-table tabs with an Excel-like grid.
As compared columns increase or result rows grow, current layout constraints can clip content or require awkward resizing to access off-screen values.
Users need predictable horizontal and vertical scrolling inside the results area so all compared data remains reachable.
This is a cosmetic usability change and must not alter comparison logic, table ordering, or cell-highlighting semantics.

## Goals / Non-Goals

**Goals:**
- Ensure wide comparison grids are navigable via horizontal scrolling.
- Ensure tall comparison grids are navigable via vertical scrolling within the results area.
- Preserve existing result-tab behavior, grid content semantics, and deterministic rendering.

**Non-Goals:**
- Changing how comparison rows/columns are computed.
- Introducing virtualized custom grid implementations beyond existing Vaadin capabilities.
- Redesigning the app shell or non-comparison areas.

## Decisions

- Use explicit overflow management on the comparison result container and grid wrapper.
  - Decision: wrap each table grid in a container that enforces `overflow-x: auto` and `overflow-y: auto` with bounded height.
  - Rationale: keeps scrolling behavior localized to results and avoids whole-page scroll side effects.
  - Alternative considered: rely only on default browser/body scrolling.
  - Why not chosen: makes headers/actions harder to keep in view and creates inconsistent interaction.

- Keep grid structural behavior unchanged while adjusting sizing constraints.
  - Decision: retain current deterministic columns and row rendering; tune width/height policies (for example, width-full with minimum content width and bounded visible height).
  - Rationale: meets usability goal without impacting comparison semantics.
  - Alternative considered: collapse columns or truncate values when width is limited.
  - Why not chosen: hides information and conflicts with detailed comparison use cases.

- Validate scrollbar behavior through UI-focused tests.
  - Decision: add/update tests to assert overflow-friendly container classes/attributes and representative wide/tall rendering scenarios.
  - Rationale: avoids regressions and makes cosmetic behavior verifiable.
  - Alternative considered: manual visual checks only.
  - Why not chosen: brittle and hard to maintain over future UI changes.

## Risks / Trade-offs

- [Risk] Nested scroll areas may reduce discoverability on some platforms.
  → Mitigation: keep scrollbar behavior conventional and confine it to the comparison panel.

- [Risk] Height constraints could unintentionally reduce visible rows for smaller datasets.
  → Mitigation: use responsive max-height rules and keep compact mode.

- [Trade-off] More explicit layout CSS increases coupling between view structure and style rules.
  → Mitigation: use semantic class names and add focused tests around expected overflow behavior.

## Migration Plan

No migration is required.
The change is UI layout/styling only.
Rollback is a normal code revert if scrolling behavior causes regressions.

## Open Questions

No blocking open questions.
If future UX feedback asks for sticky headers or synchronized scroll, that can be handled in a follow-up change.
