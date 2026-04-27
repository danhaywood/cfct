## MODIFIED Requirements

### Requirement: Webapp validates configured SQL Server connectivity and databases
The webapp SHALL validate configured SQL Server connectivity by acquiring JDBC connections from configured DataSource beans.
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

### Requirement: Webapp invokes comparison orchestration through API contracts
The webapp SHALL invoke comparison orchestration through interfaces defined in `sqlcomparer-api`.
The webapp SHALL obtain implementations of those interfaces via imported Spring configuration from `sqlcomparer-impl`.
The webapp SHALL use DataSource-managed connection acquisition in its execution path rather than retaining externally-managed Connection state in web components.
The webapp SHALL NOT directly reference non-configuration implementation classes from `sqlcomparer-impl`.

#### Scenario: Webapp startup wiring resolves API comparison services
- **WHEN** the webapp application context starts with imported implementation wiring configuration
- **THEN** API comparison service interfaces required by the web layer are available as beans

#### Scenario: Webapp source avoids direct implementation-type coupling
- **WHEN** webapp source imports are inspected
- **THEN** no non-configuration type from `sqlcomparer-impl` is referenced by webapp code
