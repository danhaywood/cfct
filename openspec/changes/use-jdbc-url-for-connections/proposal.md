## Why

Using a hostname-style `server` property is too restrictive for real deployments that require SQL Server JDBC URL options.
Deployers need to provide full JDBC URLs so CFCT can connect to environments such as Azure SQL Managed Instance with additional connection parameters.

## What Changes

- Replace hostname-style connection input with JDBC URL input for comparison execution paths in CLI and webapp flows.
- Update runtime configuration, defaults, and user-facing documentation to use JDBC URL terminology and examples.
- Keep username/password and left/right database selection behavior, but build connections from a provided JDBC URL base.
- **BREAKING**: Existing `server`-based configuration and argument semantics are replaced by JDBC URL equivalents.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `cli-argument-driven-comparison`: Change connection argument and dotenv behavior from server host input to JDBC URL input.
- `vaadin-webapp-configuration`: Change login defaults and runtime connection input semantics from server host to JDBC URL.
- `demo-scripts-and-docs`: Update README, `.env` examples, and wrapper guidance to use JDBC URL configuration.

## Impact

CLI parsing, dotenv key usage, webapp configuration properties, login UI labels/placeholders, and SQL connection factory logic are affected.
Test fixtures and docs that currently assume `server` values will need updates to JDBC URL examples, including Azure SQL MI-compatible patterns.
