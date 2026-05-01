## Context

The command selection grid is now part of the left navigation flow and supports multi-selection and filtering.
Users need faster chronological scanning of command activity while still seeing member and interaction identifiers.
Current presentation does not prioritize recency and can slow selection for command-driven workflows.

## Goals / Non-Goals

**Goals:**
- Display command timestamp as a visible command-grid column.
- Order visible command columns as timestamp, member, interactionId.
- Keep selection and filtering interactions deterministic after the column order change.

**Non-Goals:**
- Change command selection semantics or comparison orchestration.
- Introduce new backend queries or schema changes.
- Redesign left navigation layout sections beyond command-grid column presentation.

## Decisions

Use existing timestamp text already loaded in command catalog entries as the displayed timestamp value.
Render command grid columns in explicit order: timestamp, member, interactionId after the selection checkbox column.
Keep filtering live and align it with visible identity fields by supporting member and interactionId filters while keeping timestamp readable and sortable.
Update unit and browser-level assertions to lock expected column order and timestamp visibility.

## Risks / Trade-offs

[Timestamp format is hard to scan] → Keep ISO-like sortable string format and use consistent rendering in grid.
[Column reorder breaks existing browser selectors] → Anchor tests on data-testid and explicit header text checks.
[Additional column width reduces drawer density] → Use auto-width and preserve horizontal layout constraints in current drawer width.

## Migration Plan

Implement the column display and ordering update in the command-grid builder.
Update tests that validate command-grid content and filtering.
Run webapp test suites to confirm no regressions.

## Open Questions

Whether timestamp should be localized in display or remain raw UTC/local persisted format.
