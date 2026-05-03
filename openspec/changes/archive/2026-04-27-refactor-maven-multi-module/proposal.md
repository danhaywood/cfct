## Why

The project currently uses a single Maven module that mixes public API types, SQL Server implementation code, Spring Boot application wiring, and Docker-backed integration tests.
Splitting the codebase into focused Maven modules will make dependencies clearer, reduce accidental coupling, and provide a cleaner foundation for CLI and library usage.

## What Changes

- Convert the root Maven project into a parent/aggregator POM.
- Add an API module for public comparison request/result contracts and service interfaces.
- Add an implementation module for comparison logic, SQL Server metadata/row readers, request loading, and report renderers.
- Add an integration-test module for Docker/Testcontainers SQL Server harnesses and integration tests.
- Add a CLI module for Spring Boot application wiring and the application entry point.
- Move `CfctApplication` into the CLI module.
- Keep existing comparison behavior, JSON output, Excel output, approval tests, and integration test expectations unchanged.
- Preserve `mvn test` for unit tests and `mvn verify` for integration-test execution from the repository root.

## Capabilities

### New Capabilities
- `maven-multi-module-structure`: Defines the required Maven module boundaries, dependency direction, application placement, and root build behavior.

### Modified Capabilities
- `core-single-table-comparison`: Core API requirements are clarified so public comparison API contracts live in the API module while implementation services live outside the API module.
- `sqlserver-two-databases-test-harness`: Integration-test harness requirements are clarified so Docker-backed harness code and integration tests live in the integration-test module.

## Impact

- Affects Maven project structure, package ownership, module dependencies, and test execution configuration.
- Moves source files between modules without changing package names unless a module boundary requires it.
- Requires Maven dependency management for shared versions across modules.
- Requires updates to test resource paths and Failsafe/Surefire configuration so root commands continue to work.
- The refactor should not introduce user-visible behavior changes.
