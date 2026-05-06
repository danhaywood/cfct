## Context

The webapp currently uses a partially typed configuration model.
`WebappComparisonProperties` is annotated with `@ConfigurationProperties`, but datasource defaults are still injected with `@Value` fields.
This mixed approach makes the configuration contract harder to reason about and weakens compile-time safety.
The project preference is to use `@ConfigurationProperties` for all application configuration properties.

## Goals / Non-Goals

**Goals:**
- Ensure all webapp configuration inputs used by application code are represented by typed `@ConfigurationProperties` classes.
- Remove field-level `@Value` usage from webapp configuration classes.
- Preserve existing external property keys and default runtime behavior for current deployments.
- Keep configuration access centralized and testable through one typed binding model.

**Non-Goals:**
- Renaming existing external configuration keys.
- Introducing a new configuration framework beyond standard Spring Boot binding.
- Changing authentication or comparison workflow behavior unrelated to property binding style.

## Decisions

### Decision: Represent datasource defaults with typed properties instead of `@Value`
We will bind datasource defaults through typed configuration objects rather than direct field injection.
This preserves type safety and allows consistent metadata generation.
Alternative considered was keeping `@Value` for `spring.datasource.*` to avoid touching binding structure, but this continues the mixed pattern and was rejected.

### Decision: Keep existing configuration key paths stable
We will keep current property paths for both Spring datasource keys and `cfct.webapp.*` keys.
This avoids migration churn and keeps existing environment files valid.
Alternative considered was introducing a new `cfct.webapp.datasource.*` namespace, but this would require migration without clear functional benefit.

### Decision: Validate behavior with focused configuration binding tests
We will update or add tests that verify binding defaults and overrides through the typed model.
This protects against regressions when replacing `@Value` fields.
Alternative considered was relying only on manual startup verification, but that provides weaker change safety.

## Risks / Trade-offs

- [Risk] Binding semantics can shift subtly when replacing `@Value` defaults with typed binder defaults. → Mitigation: add tests for default values and override precedence.
- [Risk] Refactoring can break consumers expecting old accessor patterns. → Mitigation: preserve outward-facing accessors used by existing services and UI wiring.
- [Trade-off] Introducing additional nested property types increases class structure verbosity. → Mitigation: keep property classes cohesive and limited to meaningful configuration groups.

## Migration Plan

No external migration is required because property keys remain unchanged.
The change can be delivered as a normal application update.
Rollback is a standard code rollback to the previous release if unexpected binding issues appear.

## Open Questions

No open questions remain for implementation.
