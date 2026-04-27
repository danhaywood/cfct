## MODIFIED Requirements

### Requirement: Project provides a baseline Spring Boot scaffold for future comparison work
The project SHALL include a baseline Spring Boot structure in the CLI module that can host future database-comparison application services while coexisting with the integration-test harness.
The SQL Server harness SHALL live outside the CLI module in the integration-test module.

#### Scenario: Build supports application and test scaffolding
- **WHEN** the project is built from a clean checkout
- **THEN** the Spring Boot application structure and the SQL Server harness tests are both part of the Maven reactor layout

#### Scenario: Spring Boot application lives in CLI module
- **WHEN** the source tree is inspected
- **THEN** the Spring Boot application entry point is located in the CLI module

#### Scenario: Harness can evolve without replacing the project foundation
- **WHEN** future comparison features are added
- **THEN** they can be implemented on top of the multi-module project structure without discarding the harness foundation introduced by this change
