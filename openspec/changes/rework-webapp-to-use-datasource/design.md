## Context

The webapp currently orchestrates comparison and validation work around JDBC `Connection` usage patterns that are harder to manage safely in Spring applications.
The codebase already has API service contracts and implementation wiring separation, so this change should preserve that architecture while switching execution semantics to `DataSource` at application boundaries.
The requested scope is webapp-first, with optional lower-level refactoring only where it improves contract clarity and avoids duplicated connection lifecycle logic.

## Goals / Non-Goals

**Goals:**
- Move webapp comparison execution and SQL connectivity operations to DataSource-managed connection acquisition.
- Keep connection lifecycle management centralized and predictable.
- Evolve API and implementation contracts only where needed to support DataSource-based orchestration cleanly.
- Preserve existing comparison behavior, report determinism, and startup validation outcomes.

**Non-Goals:**
- Change comparison semantics, matching rules, or output formats.
- Introduce distributed transactions or advanced pooling policy changes.
- Rework CLI behavior unless shared contract changes require mechanical updates.

## Decisions

### Decision: Use DataSource at webapp service boundaries.
Webapp services that need database access will receive configured `DataSource` beans and acquire short-lived `Connection` instances inside service methods.
This removes long-lived or externally-managed `Connection` concerns from web-layer orchestration.

**Alternatives considered:**
- Keep Connection parameters in webapp services and rely on callers to manage lifecycle.
- Push all DataSource usage into controllers only.

**Rationale:**
Service-level DataSource usage keeps lifecycle logic close to execution and avoids leaking low-level details across layers.

### Decision: Introduce lower-level refactor only where contract friction exists.
If API comparison service contracts can remain stable while the webapp adapts internally, keep signatures unchanged.
If recurring Connection plumbing appears across entry points, add DataSource-oriented contract methods in API and bind them in impl configuration.

**Alternatives considered:**
- Force full API rewrite to DataSource everywhere immediately.
- Avoid any contract changes and duplicate connection handling in entry points.

**Rationale:**
This staged approach minimizes breakage while still enabling clean DataSource-driven orchestration.

### Decision: Keep module-boundary rule intact.
`sqlcomparer-webapp` must continue to depend on API contracts and explicit impl configuration import only.
DataSource migration must not reintroduce direct type coupling to non-configuration classes in `sqlcomparer-impl`.

**Alternatives considered:**
- Directly instantiate impl classes in webapp for convenience.

**Rationale:**
Preserving boundaries avoids regression against recently-established architectural constraints.

## Risks / Trade-offs

- [DataSource migration can introduce subtle connection leaks] → Mitigation: use try-with-resources consistently and add focused tests for acquisition and closure paths.
- [Partial lower-level refactor may create mixed patterns temporarily] → Mitigation: document allowed transitional patterns and finish contract cleanup in the same change.
- [API signature changes may ripple into CLI and tests] → Mitigation: keep compatibility adapters where useful and update affected modules atomically.
- [Testcontainers startup on arm64 can be slow during validation] → Mitigation: keep timeouts conservative and retain deterministic fixture setup.

## Migration Plan

1. Refactor webapp comparison and validation services to use injected DataSource beans.
2. Update API and impl contracts only where needed to avoid repeated Connection orchestration in entry-point modules.
3. Update Spring wiring and tests in webapp, and in CLI if shared signatures change.
4. Run webapp unit tests, Playwright tests, and full reactor verification.

Rollback strategy is to revert the change atomically and restore prior Connection-based call sites.

## Open Questions

Should API contracts expose both Connection-based and DataSource-based methods during transition, or switch fully in one change.
Should webapp use one DataSource with databaseName switching or two explicit left/right DataSource beans for clarity.
