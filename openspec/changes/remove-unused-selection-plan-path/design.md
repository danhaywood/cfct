## Context

The webapp currently includes a SelectionPlan-based configuration path (`cfct.webapp.selection-plan.explicit.tables`) and related service wiring.
Production comparison execution is driven by manual table selection and command-driven inference in the UI, not by SelectionPlan preparation services.
This leaves an unused runtime path that is still documented and tested.

## Goals / Non-Goals

**Goals:**
- Remove unused SelectionPlan runtime wiring and related configuration surface.
- Keep active table-selection behavior unchanged for deployers and users.
- Simplify docs and config references to only cover active runtime paths.
- Align specs with actual behavior.

**Non-Goals:**
- No redesign of manual table selection UX.
- No change to command-driven table inference behavior.
- No change to comparison algorithms, output formats, or datasource semantics.

## Decisions

### Decision: Remove SelectionPlan artifacts rather than keeping dormant abstractions.
Remove `SelectionPlan`, `ExplicitSelectionPlan`, `ExplicitSelectionPlanProperties`, `SelectionPlanConfiguration`, and `WebappComparisonPreparationService`.
This avoids maintaining dead code and prevents future confusion over inactive configuration.
Alternative considered: keep classes but mark as deprecated.
Rejected because deprecated dead code still adds maintenance and documentation burden.

### Decision: Remove `cfct.webapp.selection-plan.explicit.tables` from active config and docs.
Delete the property from default webapp configuration and all user-facing references.
Alternative considered: keep property as no-op compatibility key.
Rejected because silent no-op settings are misleading for deployers.

### Decision: Update specs to reflect active table-selection paths only.
Modify `vaadin-webapp-configuration` and `demo-scripts-and-docs` requirements to remove SelectionPlan-based expectations.
Alternative considered: defer spec updates.
Rejected because behavior/docs/spec drift would continue.

## Risks / Trade-offs

- [Hidden runtime dependency on removed beans] → Search all main-source references and run module tests before merge.
- [External deployers still set removed property] → Add release/readme migration note stating property removal.
- [Spec/doc mismatch during transition] → Update spec deltas and README in the same change.

## Migration Plan

Remove SelectionPlan classes and tests tied only to that path.
Update webapp defaults and README/config reference to remove the property.
Update OpenSpec deltas and sync to main specs.
Run `cfct-webapp` and related module tests to confirm active behavior is unchanged.

## Open Questions

None.
