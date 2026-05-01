## Context

The left navigation panel currently exposes one selection surface, the manual table grid, plus a compare action row.
Recent backend work can resolve touched tables from selected command interactions.
The webapp now needs a command-focused selector that sits above the existing table grid so users can choose interactions before or alongside manual table choices.

## Goals / Non-Goals

**Goals:**
- Add a filterable selectable command grid in the left navigation area.
- Position the command grid above the existing table grid with explicit spacer treatment.
- Keep manual table-selection behavior intact below the new command selector.
- Preserve deterministic selection state for one-or-more command selections.

**Non-Goals:**
- Replace the existing table grid.
- Redesign right-side comparison result tabs in this change.
- Add advanced command-query builder semantics beyond simple grid filtering.

## Decisions

Introduce a dedicated command-grid section component in the left panel and place it before the manual table grid section.
Model command selection as multi-select rows so one or more interactions can be selected at once.
Provide in-grid or header filters for visible command identity fields and apply filtering immediately on input change.
Add a spacer wrapper above the command section to align with existing navbar/action-row vertical rhythm.
Keep command-grid data loading behind a webapp service abstraction so the view does not embed SQL concerns.

## Risks / Trade-offs

[Left navigation becomes visually dense] → Add explicit spacer and section labeling to preserve scanability.
[Large command logs degrade grid responsiveness] → Start with server-side data provider paging and lightweight default columns.
[Dual selection surfaces confuse users] → Keep command grid above table grid and preserve consistent filter and selection affordances.

## Migration Plan

Add command-grid UI and service wiring behind existing webapp module boundaries.
Extend webapp tests to cover section ordering, filtering, and multi-row selection.
Roll out with table grid unchanged so fallback manual selection remains available.

## Open Questions

Whether command selection should automatically preselect touched tables in this same change or a follow-up.
Which command columns should be visible by default versus hidden behind details.
