## Context

Webapp configuration currently uses `cfct.webapp.comparison.*` for properties that are not all comparison-specific and not all webapp-relevant.
Left/right database selection and validation settings are webapp concerns and should live at stable webapp-focused prefixes.
`env-file` and output rendering defaults are CLI concerns and should not be part of webapp runtime configuration contracts.

## Goals / Non-Goals

**Goals:**
- Rename webapp keys to a more coherent namespace:
- `cfct.webapp.comparison.connection.left-database` → `cfct.webapp.connection.left-database`.
- `cfct.webapp.comparison.connection.right-database` → `cfct.webapp.connection.right-database`.
- `cfct.webapp.comparison.validation.enabled` → `cfct.webapp.validation.enabled`.
- `cfct.webapp.comparison.validation.fail-fast` → `cfct.webapp.validation.fail-fast`.
- Remove webapp keys that are CLI-only in intent:
- `cfct.webapp.comparison.env-file`.
- `cfct.webapp.comparison.output.format`.
- `cfct.webapp.comparison.output.file`.
- Update documentation and tests so deployers have a clear migration path.

**Non-Goals:**
- No change to datasource keys (`spring.datasource.*`).
- No change to actual comparison execution algorithms.
- No introduction of compatibility aliases beyond explicit migration documentation.

## Decisions

### Decision: Treat property rename/removal as a breaking configuration cleanup.
The old keys will be removed rather than silently supported.
This avoids long-term dual-key complexity and prevents hidden precedence bugs.
Alternative considered: support old and new keys for one release.
Rejected to keep configuration behavior deterministic and reduce maintenance overhead.

### Decision: Keep only webapp-relevant keys in `cfct.webapp.*`.
Database pair and validation behavior remain in webapp config.
CLI-facing keys are removed from webapp configuration to enforce bounded responsibility.
Alternative considered: keep removed keys as no-op metadata in docs.
Rejected because no-op config encourages cargo-cult configuration.

### Decision: Update docs and specs in the same change.
README/application.yml examples and OpenSpec capabilities are updated together with code.
Alternative considered: stage docs later.
Rejected due to high deployer confusion risk during property migration.

## Risks / Trade-offs

- [Deployers break after upgrade due to removed keys] → Add explicit migration notes and before/after key mapping in docs.
- [Missed references in tests or scripts] → Search for old keys globally and update all references in same change.
- [Behavioral regression in validation flow] → Run webapp tests and verify login/validation paths.

## Migration Plan

Implement key renames/removals in webapp configuration binding and call sites.
Update `application.yml` defaults and README property tables/examples.
Update tests and spec deltas for renamed/removed keys.
Run webapp and related module tests.

## Open Questions

None.
