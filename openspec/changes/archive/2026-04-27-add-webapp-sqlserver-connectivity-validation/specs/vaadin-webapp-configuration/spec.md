## ADDED Requirements

### Requirement: Webapp validates configured SQL Server connectivity and databases
The webapp SHALL validate that configured SQL Server connection settings can establish JDBC connectivity.
The webapp SHALL validate that configured left and right logical databases exist and are reachable with the configured credentials.
The webapp SHALL fail with clear diagnostics when connectivity fails, authentication fails, or configured databases are missing.

#### Scenario: Valid connectivity and databases allow startup
- **WHEN** the configured SQL Server endpoint is reachable and both configured logical databases exist
- **THEN** the webapp connectivity validation passes and application startup continues

#### Scenario: Missing configured database fails validation
- **WHEN** one of the configured logical database names does not exist in the target SQL Server instance
- **THEN** the webapp startup fails with a clear validation error identifying the missing database

#### Scenario: Unreachable SQL Server fails validation
- **WHEN** the configured SQL Server endpoint cannot be reached with the provided settings
- **THEN** the webapp startup fails with a clear connectivity error
