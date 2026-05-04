## Context

The README currently documents setup and execution flows but does not present all database prerequisites in one obvious location.
Deployers discover missing requirements late, usually after webapp login validation errors or table-selection failures.
Existing specs already define the required target system objects and `_PK` comparability rules, so this change is documentation alignment rather than behavior change.
Deployers also need a single copy/paste-friendly reference of supported properties in `application.yml` structure.

## Goals / Non-Goals

**Goals:**
- Add a short, skimmable README section that states required target-database system tables or views.
- Add README guidance that comparable business tables must provide a `_PK`-suffixed unique index or unique constraint.
- Add an `application.yml`-formatted config reference section that lists supported CFCT runtime properties used by deployers.
- Position the new sections where users encounter them before running fixture or comparison commands.

**Non-Goals:**
- No changes to validation logic, metadata discovery, or comparison execution.
- No changes to configuration keys, CLI options, or webapp UI behavior.
- No expansion into deep SQL modeling guidance beyond the two prerequisite rules.

## Decisions

### Decision: Add a dedicated “Database requirements” subsection in README for deployers.
Use a dedicated subsection instead of scattering notes across setup steps.
This keeps prerequisites discoverable and reduces repeated troubleshooting.
Alternative considered: inline notes near each command.
Inline notes were rejected because they are easy to miss and become repetitive.

### Decision: List exact required objects by schema-qualified name.
Document `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping` explicitly.
This matches existing validation behavior and avoids ambiguity about naming.
Alternative considered: describe required objects generically.
Generic wording was rejected because it does not help users diagnose missing-object errors quickly.

### Decision: Describe `_PK` rule in terms of unique index or unique constraint.
Document that compared tables must expose a unique index or unique constraint with a name ending in `_PK`.
This mirrors comparison-key discovery rules and keeps README language consistent with existing specs.
Alternative considered: mention only primary keys.
Primary-key-only wording was rejected because CFCT supports both unique indexes and unique constraints under the `_PK` naming rule.

### Decision: Add a configuration reference in `application.yml` format.
Present the property reference as YAML hierarchy rather than only flattened dot-notation keys.
This matches how Spring Boot config is commonly maintained in real deployments and supports direct copy/edit workflows.
Alternative considered: table of dot-notation keys only.
Dot-notation-only format was rejected because it is less usable for deployers composing full config files.

## Risks / Trade-offs

- [README drift from implementation rules] → Keep wording aligned with current OpenSpec requirements and avoid introducing new semantics.
- [Section becomes too long or verbose] → Keep the new section short with bullet points and direct examples only.
- [Users misread requirement scope] → State clearly that system-object checks apply to target-database validation and `_PK` applies to compared business tables.
- [Config reference diverges from actual supported properties] → Derive the YAML example from committed defaults and existing documented runtime options.

## Migration Plan

Update README text in place.
Run a quick documentation pass to confirm names and rule wording match current specs.
No runtime deployment or rollback steps are required because this is documentation-only.

## Open Questions

No open questions.
