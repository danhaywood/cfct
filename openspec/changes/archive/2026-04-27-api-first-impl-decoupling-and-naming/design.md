## Context

`cfct-cli` and `cfct-webapp` currently reference concrete service and executor classes from `cfct-impl`.
This creates compile-time coupling to implementation details and makes refactoring in `-impl` risky for application entry-point modules.
At the same time, implementation naming is inconsistent, with patterns like `CliComparisonExecutorSqlServer implements CliComparisonExecutor` instead of a consistent interface-first naming style.
The requested architecture is that `-cli` and `-webapp` depend on API contracts, while Spring wiring in `-impl` binds those contracts to implementations.

## Goals / Non-Goals

**Goals:**
- Define API-level interfaces for the comparison services used by entry points.
- Move direct references to `-impl` types out of `-cli` and `-webapp`, except for importing Spring configuration.
- Standardize implementation naming to `FooXxx implements Foo` across affected code.
- Add verification tests that prevent future boundary regressions.

**Non-Goals:**
- Rework comparison algorithms or output semantics.
- Introduce a plugin system or runtime module loading.
- Rename every class in the repository where no interface implementation relationship exists.
- Change external CLI arguments or webapp UX behavior as part of this change.

## Decisions

### Decision: Introduce API service contracts for application-facing orchestration.
Create new interfaces in `cfct-api` for the service entry points currently instantiated from `cfct-impl`.
These interfaces will represent stable use-case boundaries for single-table and multi-table comparison orchestration.
`cfct-cli` and `cfct-webapp` will depend only on these interfaces.

**Alternatives considered:**
- Keep direct `-impl` type usage and rely on discipline.
- Use reflection-based lookups to reduce compile-time coupling.

**Rationale:**
Explicit interfaces provide compile-time clarity and preserve refactor freedom in implementations.
Reflection would reduce type safety and increase startup/runtime complexity.

### Decision: Keep implementation bean creation in `cfct-impl` configuration and import only configuration from applications.
`cfct-impl` will expose Spring `@Configuration` classes that publish API contract beans.
`cfct-cli` and `cfct-webapp` will import those configuration classes via `@Import` or equivalent module wiring.
No other non-configuration `-impl` types will be referenced by application modules.

**Alternatives considered:**
- Component scan `-impl` from applications.
- Duplicate wiring in each application module.

**Rationale:**
Centralized wiring avoids duplication and keeps implementation construction details in one place.
Selective import is explicit and easier to test than broad scanning.

### Decision: Adopt interface-first implementation naming (`FooXxx implements Foo`).
For classes that implement a named interface, rename classes to the form `<Interface><Qualifier>`.
For example, `CliComparisonExecutorSqlServer implements CliComparisonExecutor` becomes `CliComparisonExecutorSqlServer implements CliComparisonExecutor`.
Naming updates will be limited to classes implementing interfaces and touched by this change.

**Alternatives considered:**
- Keep existing naming for backward familiarity.
- Use suffix `Impl` or `Default` everywhere.

**Rationale:**
The interface-first naming groups implementations naturally in IDE symbol lists and makes interface ownership obvious.
Generic `Impl` naming loses domain qualifiers and becomes ambiguous when multiple implementations exist.

### Decision: Add architecture guard tests for module boundaries.
Add tests in `-cli` and `-webapp` that fail if non-configuration classes from `-impl` are referenced.
This can be implemented with ArchUnit or targeted package-reference assertions, depending on existing test dependencies.

**Alternatives considered:**
- Rely on code review only.
- Enforce through Maven Enforcer exclusions alone.

**Rationale:**
Executable architecture rules are the most reliable way to prevent regressions over time.

## Risks / Trade-offs

- [Public implementation class renames may break downstream code] → Mitigation: Document rename mapping and keep changes scoped to implementation classes.
- [Moving interfaces to API can cause temporary duplication during transition] → Mitigation: Apply in small commits and remove obsolete adapters in the same change.
- [Boundary tests may be brittle if package rules are too broad] → Mitigation: Start with narrow assertions focused on forbidden imports from `-impl`.
- [Spring wiring changes could cause startup misconfiguration] → Mitigation: Run CLI and webapp startup tests plus existing integration suites.

## Migration Plan

1. Add API service interfaces and adjust constructors/usages in `-cli` and `-webapp` to depend on them.
2. Implement or adapt those interfaces in `-impl` and expose them through `-impl` Spring configuration.
3. Rename implementation classes to interface-first naming and update all references and bean names.
4. Add module-boundary guard tests and update existing tests impacted by renames.
5. Run reactor tests plus targeted CLI and webapp startup checks.

Rollback strategy is to revert this change atomically if module wiring fails in integration environments.

## Open Questions

Should interface naming in API follow `*Service`, `*UseCase`, or retain existing comparer-oriented names consistently.
Should we enforce naming convention only for touched classes now or add a repository-wide rule for future PR checks.
