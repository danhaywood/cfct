## ADDED Requirements

### Requirement: Project builds as a Maven multi-module reactor
The project SHALL use a root Maven parent/aggregator POM with child modules for API, implementation, CLI, and integration tests.
The root Maven reactor SHALL build modules in dependency order.
The root build SHALL keep `mvn test` and `mvn verify` as the primary validation commands.

#### Scenario: Root reactor lists expected modules
- **WHEN** the root Maven project is inspected
- **THEN** it declares modules for API, implementation, CLI, and integration tests

#### Scenario: Root test command runs module unit tests
- **WHEN** `mvn test` is run from the repository root
- **THEN** Maven builds the reactor and runs unit tests in the relevant modules

#### Scenario: Root verify command runs integration tests
- **WHEN** `mvn verify` is run from the repository root in a Docker-enabled environment
- **THEN** Maven builds the reactor and runs Docker-backed integration tests from the integration-test module

### Requirement: API module owns public comparison contracts
The project SHALL provide an API module for public comparison contracts.
The API module SHALL contain request, result, table, column, key, difference, option, exception, and service-interface types needed by callers and implementations.
The API module SHALL NOT depend on the CLI module, the implementation module, Spring Boot application packaging, Testcontainers, ApprovalTests, or report-rendering libraries.

#### Scenario: API module can compile independently of implementation
- **WHEN** the API module is built
- **THEN** it compiles without depending on implementation, CLI, or integration-test modules

#### Scenario: Public comparison model is available from API module
- **WHEN** another module depends on the API module
- **THEN** it can use comparison request and result contract types without depending on the CLI module

### Requirement: Implementation module owns comparison behavior
The project SHALL provide an implementation module that depends on the API module.
The implementation module SHALL contain comparison services, SQL Server-specific metadata and row readers, request loading, configured comparison execution, and report renderers.
The implementation module SHALL NOT depend on the CLI module or integration-test module.

#### Scenario: Implementation module depends on API module
- **WHEN** the implementation module is built
- **THEN** it can use API module contracts to implement comparison behavior

#### Scenario: Implementation module excludes executable application concerns
- **WHEN** the implementation module source tree is inspected
- **THEN** it does not contain the Spring Boot application entry point

### Requirement: CLI module owns Spring Boot application packaging
The project SHALL provide a CLI module that depends on the implementation module.
The CLI module SHALL contain the Spring Boot application entry point.
Executable Spring Boot packaging SHALL be configured in the CLI module rather than the API or implementation modules.

#### Scenario: Spring application entry point lives in CLI module
- **WHEN** the source tree is inspected
- **THEN** `CfctApplication` is located in the CLI module

#### Scenario: CLI module can package executable application
- **WHEN** the CLI module is packaged by Maven
- **THEN** Spring Boot application packaging uses the CLI module application entry point

### Requirement: Integration-test module owns Docker-backed verification
The project SHALL provide an integration-test module for Testcontainers-backed SQL Server verification.
The integration-test module SHALL contain the SQL Server harness, integration tests, SQL fixture resources, approval files, and integration-test logging configuration.
The integration-test module SHALL use Maven Failsafe to run `*IT` tests during `mvn verify`.

#### Scenario: Harness code lives in integration-test module
- **WHEN** the source tree is inspected
- **THEN** SQL Server harness support classes are located in the integration-test module

#### Scenario: Fixture resources are available to integration tests
- **WHEN** integration tests run from the integration-test module
- **THEN** SQL fixture resources and approval files are available on the test classpath

#### Scenario: Failsafe runs integration tests
- **WHEN** `mvn verify` runs in a Docker-enabled environment
- **THEN** Maven Failsafe executes `*IT` tests from the integration-test module

### Requirement: Module dependencies preserve layer direction
Module dependencies SHALL flow from CLI to implementation to API, and from integration tests to implementation or CLI where needed.
The API module SHALL be the lowest layer.
The implementation module SHALL NOT depend on CLI or integration-test modules.
The CLI module SHALL NOT depend on the integration-test module.

#### Scenario: API remains lowest layer
- **WHEN** module dependencies are inspected
- **THEN** no API module dependency points to implementation, CLI, or integration-test modules

#### Scenario: Implementation remains independent of executable and test harness modules
- **WHEN** module dependencies are inspected
- **THEN** the implementation module does not depend on CLI or integration-test modules

#### Scenario: Integration tests do not leak into production modules
- **WHEN** production module dependencies are inspected
- **THEN** Testcontainers, ApprovalTests, and integration harness dependencies are not required by API, implementation, or CLI runtime consumers unless explicitly scoped for tests
