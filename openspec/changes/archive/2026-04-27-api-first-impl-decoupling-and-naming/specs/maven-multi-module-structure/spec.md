## MODIFIED Requirements

### Requirement: Module dependencies preserve layer direction
Module dependencies SHALL flow from CLI to API for use-case contracts, from webapp to API for use-case contracts, and from implementation to API for contract implementations.
The API module SHALL be the lowest layer.
The implementation module SHALL NOT depend on CLI, webapp, or integration-test modules.
The CLI module SHALL NOT depend on the webapp or integration-test module.
The webapp module SHALL NOT depend on the CLI or integration-test module.
The CLI and webapp modules MAY depend on `cfct-impl` only for importing explicit Spring wiring configuration classes.
The CLI and webapp modules SHALL NOT reference non-configuration implementation types from `cfct-impl`.

#### Scenario: API remains lowest layer
- **WHEN** module dependencies are inspected
- **THEN** no API module dependency points to implementation, CLI, webapp, or integration-test modules

#### Scenario: Implementation remains independent of executable and test harness modules
- **WHEN** module dependencies are inspected
- **THEN** the implementation module does not depend on CLI, webapp, or integration-test modules

#### Scenario: Entry-point modules depend on API contracts for execution
- **WHEN** CLI and webapp module dependencies and imports are inspected
- **THEN** comparison orchestration is invoked through API contracts and not through non-configuration implementation types

#### Scenario: Integration tests do not leak into production modules
- **WHEN** production module dependencies are inspected
- **THEN** Testcontainers, ApprovalTests, and integration harness dependencies are not required by API, implementation, CLI, or webapp runtime consumers unless explicitly scoped for tests
