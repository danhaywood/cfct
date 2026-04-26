## Why

The project needs a reliable foundation for regression testing across two databases before any comparison logic is built. Establishing a repeatable Spring Boot and Testcontainers harness now reduces risk, proves the local/CI execution model, and gives later comparison work a stable base.

## What Changes

- Add an initial Spring Boot project scaffold suitable for database-comparison work.
- Add automated test infrastructure that can start one SQL Server 2022 container instance for integration testing.
- Add a smoke-test harness that creates two logical databases within that instance and verifies both can be reached over JDBC and initialized independently.
- Add baseline test support assets and configuration for containerized SQL Server regression tests, using AssertJ, parameterized tests with enum sources, and Approvals where appropriate.

## Capabilities

### New Capabilities
- `sqlserver-two-databases-test-harness`: Provide an automated integration-test harness that provisions one SQL Server 2022 instance with two isolated logical databases for future regression-comparison tests.

### Modified Capabilities
- None.

## Impact

- Maven build and dependency management.
- Spring Boot application bootstrap and test structure.
- Testcontainers-based integration tests and supporting SQL/test resources.
- Test dependencies and conventions for AssertJ, JUnit 5 parameterized tests, and Approvals.
- Docker availability in local development and CI environments.
