## Context

The command drawer section currently exposes one filter input for interaction ID.
The command grid already includes a Member column, so users expect to filter by member values too.
The filter input layout now has room for an additional member filter above interaction ID.

## Goals / Non-Goals

**Goals:**
- Add member ID filtering to the command grid.
- Render filter controls in this order: member ID first, interaction ID second.
- Keep command selection and touched-table wiring stable while filters are applied.

**Non-Goals:**
- Change command grid columns.
- Change command sorting semantics.
- Change command checkbox selection semantics.

## Decisions

Extend command filter state matching to include member ID criteria.
Use conjunction semantics so a row must match both non-empty filters.
Add a new member filter text field above interaction ID in the drawer command section.
Preserve existing test IDs and add a dedicated test ID for the new member filter control.

## Risks / Trade-offs

[Two filters may confuse users if labels are unclear] → Use explicit placeholders for member ID and interaction ID.
[Filter logic regression could hide rows unexpectedly] → Add unit tests for single-filter and combined-filter behavior.
[Layout drift on small viewports] → Keep controls full width and validate via browser test.

## Migration Plan

Update command filter method signatures to accept member and interaction criteria.
Add member filter field and wire both value change listeners to shared filter application logic.
Update unit tests and browser tests to assert ordering and behavior.
Run targeted webapp tests to confirm no regression.

## Open Questions

Whether a free-text filter across all command columns should replace separate fields in a future change.
