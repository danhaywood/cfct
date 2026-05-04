## Context

The manual business table grid already supports command-driven programmatic selection and manual user selection.
In the current UI, all business tables remain visible by default, even after command selection identifies a smaller relevant subset.
Users typically start from selected commands and then compare touched tables, so the default all-rows view adds noise in the common path.
The change must preserve existing command-driven union semantics and manual override behavior while improving focus in the grid.

## Goals / Non-Goals

**Goals:**
- Add a `Selected only` control in the business table selection section.
- Default the control to checked so the grid initially filters to selected rows.
- Keep selection state authoritative, with filtering only affecting visibility.
- Allow users to disable the filter and manually select additional business tables.
- Keep compare readiness and command-driven recomputation behavior unchanged.

**Non-Goals:**
- Redesign command selection workflows or touched-table resolution logic.
- Change backend APIs, persistence models, or selection payload formats.
- Introduce advanced saved filter presets or per-user preference persistence for the checkbox state.

## Decisions

1. Introduce a dedicated UI boolean state for selected-only visibility filtering.
Rationale: Keeping filter state independent from selected-row state avoids accidental deselection when toggling visibility.
Alternative considered: deriving filter state from whether any commands are selected; rejected because users need explicit control regardless of command selection state.

2. Initialize selected-only state to `true` on first render and on full clear-reset actions.
Rationale: This aligns the default view with the common command-first workflow and keeps the reset experience predictable.
Alternative considered: initialize to `false`; rejected because it preserves the current noisy default and does not address the core usability issue.

3. Apply selected-only filtering as a live view predicate over the existing table collection.
Rationale: A view-level predicate reuses current grid data flow and minimizes risk to selection orchestration logic.
Alternative considered: rebuilding the data provider with separate selected/unselected datasets; rejected due to unnecessary complexity.

4. Preserve command-driven and manual selection updates even when rows are hidden by selected-only filtering.
Rationale: Selection is business state, while visibility is presentation state; hidden rows may become visible again when filter toggles.
Alternative considered: restricting updates to visible rows only; rejected because it would break deterministic union behavior and clear semantics.

## Risks / Trade-offs

[Users may miss unselected rows while selected-only is enabled] → Provide clear checkbox labeling and easy toggle placement near the table grid.
[Hidden rows changing selection due to command updates may feel surprising] → Keep checkbox state explicit and preserve deterministic recomputation tied to command selections.
[Default-on filter could hide all rows before any selection exists] → Ensure the unchecked path is immediate so users can reveal all rows without extra navigation.

## Migration Plan

Ship as a UI behavior update with no schema or API migration.
Validate with unit and UI tests covering default checked state, toggle behavior, and command-driven updates while filtered.
Rollback is low risk by reverting the UI state default and filter predicate wiring.

## Open Questions

No blocking open questions are identified for implementation.
