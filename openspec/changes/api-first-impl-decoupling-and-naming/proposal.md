## Why

The current `-cli` and `-webapp` modules reference concrete types from `-impl`, which makes module boundaries leaky and harder to evolve safely.
The codebase also uses inconsistent implementation naming (`XxxFoo implements Foo`), and we want a uniform convention (`FooXxx implements Foo`) to improve discoverability and readability.

## What Changes

- Introduce API-level service interfaces for core comparison use cases currently consumed by `-cli` and `-webapp`, and make those clients depend on API contracts instead of concrete implementation classes.
- Move Spring wiring responsibility for implementation bindings into `-impl` configuration so `-cli` and `-webapp` only import implementation configuration, not implementation types.
- Rename implementation classes to the `FooXxx implements Foo` convention across the affected modules, including examples such as `SqlServerCliComparisonExecutor`.
- Update tests and module-level checks to enforce that `-cli` and `-webapp` do not directly reference non-configuration types in `-impl`.
- **BREAKING**: Public class names in implementation modules will change where they currently follow the old naming pattern.

## Capabilities

### New Capabilities
- `api-service-contracts`: Defines API-layer service interfaces and dependency direction rules for application entry points.
- `implementation-naming-conventions`: Defines the implementation class naming pattern for interface implementations.

### Modified Capabilities
- `maven-multi-module-structure`: Tighten requirements so application modules consume API contracts and only import implementation wiring.
- `cli-argument-driven-comparison`: Update requirements to invoke comparison via API service interfaces rather than direct implementation types.
- `vaadin-webapp-configuration`: Update requirements to invoke comparison via API service interfaces rather than direct implementation types.

## Impact

Affected modules include `sqlcomparer-api`, `sqlcomparer-impl`, `sqlcomparer-cli`, and `sqlcomparer-webapp`.
Spring configuration classes will be introduced or adjusted to bind API interfaces to implementation classes.
Tests will be expanded to validate dependency boundaries and naming conventions.
