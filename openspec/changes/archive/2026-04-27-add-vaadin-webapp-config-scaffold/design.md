## Context

The project currently exposes comparison execution through the CLI module and wrapper script.
There is no web module that can load comparison settings from Spring configuration and present a web entry point.
This change introduces a new module and dependency set, so architecture and layering decisions should be explicit before implementation.

## Goals / Non-Goals

**Goals:**
- Add a `cfct-webapp` module scaffolded with Spring Boot and Vaadin Flow.
- Model webapp configuration keys so they capture the same logical settings accepted by the CLI.
- Define a stable configuration binding approach using `application.yml` and typed configuration properties.
- Keep module boundaries aligned with existing API, implementation, and CLI layering.

**Non-Goals:**
- Do not implement final comparison UI screens, forms, or result views in this change.
- Do not remove or replace the existing CLI workflow.
- Do not change comparison engine semantics or output rendering logic.

## Decisions

Use a dedicated `cfct-webapp` Maven module rather than extending `cfct-cli`.
This keeps packaging concerns separate and avoids coupling a web runtime to the CLI executable artifact.
Alternative considered was adding Vaadin to the CLI module, but that would blur responsibilities and increase startup complexity for CLI-only use.

Use Vaadin Flow on the latest stable 25.x line for compatibility with Spring Boot 4.
This keeps the scaffold aligned with the current Spring ecosystem while avoiding pre-release artifacts.
Alternative considered was staying on 24.x, but that introduced Spring Boot 4 auto-configuration incompatibilities.

Use Spring Boot `@ConfigurationProperties` classes in the webapp module to bind keys that correspond to CLI concepts.
This provides typed validation and clear mapping between web configuration and existing comparison request construction.
Alternative considered was reading raw `Environment` keys on demand, but that reduces discoverability and testability.

Expose configuration sections under a webapp namespace while preserving field-level parity with CLI concepts.
This allows future UI views to use one canonical configuration object while still documenting equivalence with CLI arguments.
Alternative considered was reusing CLI flag names directly as property paths, but that is awkward and non-idiomatic for Spring configuration.

## Risks / Trade-offs

[Risk] Vaadin version drift can occur as new releases appear after scaffolding.
→ Mitigation is to centralize the Vaadin version in module properties and document update checks in README.

[Risk] Configuration parity with CLI can become inconsistent over time.
→ Mitigation is to define explicit mapping requirements in specs and add validation tests during implementation.

[Risk] Adding a new web module can unintentionally break reactor assumptions.
→ Mitigation is to update multi-module specs and run full reactor builds after module creation.

## Migration Plan

Create the new webapp module with minimal Spring Boot and Vaadin bootstrap classes.
Register the module in the root reactor and validate build order.
Add `application.yml` defaults and typed properties classes that map to CLI-equivalent settings.
Document startup and configuration behavior in README.

## Open Questions

Should the webapp directly depend on `cfct-impl` for first implementation, or define an adapter service in the web module.
Should sensitive values like passwords be expected only from externalized environment variables in production profiles.
