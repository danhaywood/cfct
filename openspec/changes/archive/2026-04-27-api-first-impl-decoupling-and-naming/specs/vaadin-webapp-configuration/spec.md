## ADDED Requirements

### Requirement: Webapp invokes comparison orchestration through API contracts
The webapp SHALL invoke comparison orchestration through interfaces defined in `cfct-api`.
The webapp SHALL obtain implementations of those interfaces via imported Spring configuration from `cfct-impl`.
The webapp SHALL NOT directly reference non-configuration implementation classes from `cfct-impl`.

#### Scenario: Webapp startup wiring resolves API comparison services
- **WHEN** the webapp application context starts with imported implementation wiring configuration
- **THEN** API comparison service interfaces required by the web layer are available as beans

#### Scenario: Webapp source avoids direct implementation-type coupling
- **WHEN** webapp source imports are inspected
- **THEN** no non-configuration type from `cfct-impl` is referenced by webapp code
