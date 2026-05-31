## Context

Column-level ignore behavior is already metadata-driven through SQL Server extended properties.
Manual table selection still relies on fixed eligibility rules and cannot be configured per table via metadata.
The requested change introduces table-level metadata exclusion while preserving visibility and explainability in the grid.

## Goals / Non-Goals

**Goals:**
- Read a table-level extended property during business table catalog preparation.
- Mark matched tables as ineligible for selection in the manual table grid.
- Show an explicit tooltip reason indicating metadata-driven exclusion.
- Keep command-driven and keyboard-driven selection paths aligned with eligibility guards.

**Non-Goals:**
- Remove metadata-excluded tables from the grid entirely.
- Change existing column-level ignore advisor semantics.
- Introduce new admin UI for configuring extended properties.

## Decisions

Use a dedicated table-level extended property key, aligned with existing naming patterns, to evaluate exclusion truthiness.
Apply exclusion during row eligibility computation so all selection entry points inherit the same disabled state.
Render the excluded row with the existing ineligible-row tooltip mechanism using a specific reason string for metadata exclusion.
Keep metadata-excluded rows visible so users can understand why a known table cannot be selected.
Add tests at selection-state and UI rendering levels to validate disabled behavior and tooltip output.

## Risks / Trade-offs

[Property name mismatch across environments] → Document the expected property key and add focused tests for truthy and falsy values.
[Confusion between hidden and disabled behavior] → Keep rows visible and provide a clear tooltip reason.
[Eligibility drift between manual and command-driven paths] → Centralize exclusion in shared eligibility evaluation before selection is applied.

## Migration Plan

Deploy with default behavior unchanged when the property is absent.
Teams can opt in table-by-table by adding the configured extended property with a truthy value.
Rollback is achieved by removing the property or setting it to a non-truthy value.

## Open Questions

Should the property key reuse `cfct.ignored` at table scope or use a dedicated table-specific key such as `cfct.table.ignored`?
Should tooltip text include the property key name or remain user-friendly and generic?
