## MODIFIED Requirements

### Requirement: Project builds as a Maven multi-module reactor
The project SHALL use a root Maven parent/aggregator POM with child modules for API, implementation, CLI, webapp, and integration tests.
The root Maven reactor SHALL build modules in dependency order.
The root build SHALL keep `mvn test` and `mvn verify` as the primary validation commands.

#### Scenario: Root reactor lists expected modules
- **WHEN** the root Maven project is inspected
- **THEN** it declares modules for API, implementation, CLI, webapp, and integration tests

#### Scenario: Root test command runs module unit tests
- **WHEN** `mvn test` is run from the repository root
- **THEN** Maven builds the reactor and runs unit tests in the relevant modules

#### Scenario: Root verify command runs integration tests
- **WHEN** `mvn verify` is run from the repository root in a Docker-enabled environment
- **THEN** Maven builds the reactor and runs Docker-backed integration tests from the integration-test module

### Requirement: Module dependencies preserve layer direction
Module dependencies SHALL flow from CLI to implementation to API, from webapp to implementation to API, and from integration tests to implementation or CLI where needed.
The API module SHALL be the lowest layer.
The implementation module SHALL NOT depend on CLI, webapp, or integration-test modules.
The CLI module SHALL NOT depend on the webapp or integration-test module.
The webapp module SHALL NOT depend on the CLI or integration-test module.

#### Scenario: API remains lowest layer
- **WHEN** module dependencies are inspected
- **THEN** no API module dependency points to implementation, CLI, webapp, or integration-test modules

#### Scenario: Implementation remains independent of executable and test harness modules
- **WHEN** module dependencies are inspected
- **THEN** the implementation module does not depend on CLI, webapp, or integration-test modules

#### Scenario: Integration tests do not leak into production modules
- **WHEN** production module dependencies are inspected
- **THEN** Testcontainers, ApprovalTests, and integration harness dependencies are not required by API, implementation, CLI, or webapp runtime consumers unless explicitly scoped for tests
