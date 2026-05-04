## Why

Using a hostname-style `server` property is too restrictive for real deployments that require SQL Server JDBC URL options.
Deployers need to provide full JDBC URLs so CFCT can connect to environments such as Azure SQL Managed Instance with additional connection parameters.

## What Changes

- Replace hostname-style connection input with JDBC URL input for comparison execution paths in CLI and webapp flows.
- Replace custom connection property keys with standard Spring datasource keys: `spring.datasource.url`, `spring.datasource.driver-class-name`, `spring.datasource.username`, and `spring.datasource.password`.
- Keep left/right database selection behavior where needed by comparison workflows, while building connections from datasource configuration.
- **BREAKING**: Existing `server`-based and custom CFCT connection-property semantics are replaced by Spring datasource equivalents.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `cli-argument-driven-comparison`: Change connection argument and dotenv behavior from server host input to JDBC URL input aligned with Spring datasource semantics.
- `vaadin-webapp-configuration`: Replace custom webapp connection default properties with `spring.datasource.*` semantics.
- `demo-scripts-and-docs`: Update README, `.env` examples, and wrapper guidance to use `SPRING_DATASOURCE_*` configuration.

## Impact

CLI parsing, dotenv/env key usage, webapp configuration properties, login UI labels/placeholders, and SQL connection factory logic are affected.
Test fixtures and docs that currently assume `server` or custom CFCT connection keys will need updates to Spring datasource examples, including Azure SQL MI-compatible JDBC URL patterns.
