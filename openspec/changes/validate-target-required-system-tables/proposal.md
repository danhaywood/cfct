## Why

Command-driven table selection depends on system metadata tables in the target database, but current login-time validation only checks connectivity and database existence.
Users can authenticate successfully and only discover missing metadata structures later when command or audit workflows fail.

## What Changes

- Add target-database structural validation during login-time connectivity checks.
- Validate that required system objects exist in the target database using `INFORMATION_SCHEMA` queries compatible with Azure SQL MI and SQL Server 2022.
- Treat both base tables and views as valid for required system objects.
- Return a clear validation failure message listing missing required objects when validation fails.
- Add a fixture-script flag that deliberately configures an invalid target-database path for manual negative-path testing.
- Update README manual-test guidance for the invalid-target fixture mode.
- Keep successful behavior unchanged when all required objects are present.

## Capabilities

### New Capabilities
- `target-required-system-table-validation`: Validate presence of required command/audit support objects in target database using INFORMATION_SCHEMA and accept views.

### Modified Capabilities
- `vaadin-webapp-configuration`: Extend connectivity validation requirements to include required target-database system object presence checks.
- `webapp-login-connection-auth`: Extend login success/failure behavior so authentication fails with clear error detail when required target system objects are missing.
- `demo-scripts-and-docs`: Extend fixture script and README requirements to support manual invalid-target database testing.

## Impact

- Affected code in webapp connectivity/login validation services and error mapping.
- Affected integration and UI tests that currently assert login or connectivity success without structural checks.
- Affected local fixture script behavior and README usage examples for manual negative-path testing.
- No external API contract changes expected.
