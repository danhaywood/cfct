## Why

The webapp currently starts but does not actively validate that configured SQL Server targets are reachable and that configured logical databases exist.
Before UI workflows are added, we need infrastructure that proves connectivity and database presence reliably in automated tests.

## What Changes

- Add webapp startup/application infrastructure that reads configured connection settings and validates SQL Server connectivity.
- Validate that configured left and right database names exist before comparison flows are used.
- Surface clear startup/runtime validation errors when connectivity fails or configured databases are missing.
- Add new Testcontainers-based test infrastructure for the webapp path to verify connectivity and database-existence checks against SQL Server.
- Keep scope limited to infrastructure and validation; do not add Playwright or browser E2E automation in this change.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `vaadin-webapp-configuration`: Add requirements for SQL Server connectivity and configured-database existence validation in the webapp infrastructure.
- `sqlserver-two-databases-test-harness`: Extend harness usage to cover webapp connectivity-validation scenarios with deterministic setup.

## Impact

This change affects `sqlcomparer-webapp` infrastructure services, error handling, and integration tests using SQL Server Testcontainers.
This change may introduce new test support classes or scripts for webapp-focused containerized validation but does not add Playwright or UI automation.
