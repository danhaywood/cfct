## MODIFIED Requirements

### Requirement: Webapp configuration models the same logical inputs as CLI
The webapp SHALL use Spring datasource properties for connection defaults: `spring.datasource.url`, `spring.datasource.driver-class-name`, `spring.datasource.username`, and `spring.datasource.password`.
The webapp SHALL provide typed configuration properties for non-datasource execution preferences such as database names, env-file path, output format, and output file.
The webapp SHALL provide typed configuration properties for default ignore-column advisor enablement flags.
The webapp SHALL allow independent enable or disable control for identity, uuid/guid, timestamp, and extended-properties ignore advisors.
The webapp SHALL default each default ignore-column advisor enablement flag to enabled.
The webapp SHALL use configured property values only as initial login defaults and SHALL allow users to edit any field before authentication.
The webapp SHALL NOT require these properties to be present for interactive webapp use.
The webapp SHALL document how runtime login inputs and configuration defaults map to equivalent CLI argument concepts.
The webapp SHALL treat table selection as a strategy concern and SHALL NOT require parity with CLI table-input flags.

#### Scenario: Webapp starts without configured credentials
- **WHEN** the webapp starts without username or password values in `application.yml`
- **THEN** typed configuration still binds non-secret defaults and the application starts successfully

#### Scenario: Configured defaults pre-populate login form
- **WHEN** the webapp has `spring.datasource.*` connection properties configured in `application.yml` or externalized configuration
- **THEN** those values are shown as initial editable defaults in the login form

#### Scenario: Configuration defaults can be overridden externally
- **WHEN** a deploy environment provides overriding Spring configuration values for login defaults
- **THEN** the webapp resolves those values over defaults from `application.yml`

#### Scenario: Ignore-column advisors are enabled by default
- **WHEN** no explicit ignore-advisor enablement values are provided
- **THEN** identity, uuid/guid, timestamp, and extended-properties ignore advisors remain enabled

#### Scenario: One ignore-column advisor can be disabled independently
- **WHEN** deployment configuration disables one ignore-column advisor flag and leaves others enabled
- **THEN** only that advisor stops contributing ignore decisions while other advisors continue to apply

### Requirement: Webapp validates configured SQL Server connectivity and databases
The webapp SHALL validate SQL Server connectivity and database reachability using runtime login credentials instead of startup-time static credentials.
The webapp SHALL execute connectivity validation during login or explicit connection test flow before granting access to comparison workflows.
The webapp SHALL validate that required target-database system objects are present using `INFORMATION_SCHEMA.TABLES` in the target database context.
The required target objects SHALL include `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
The required-object check SHALL accept both `BASE TABLE` and `VIEW` entries for each required object.
The webapp SHALL fail login with clear diagnostics when connectivity fails, authentication fails, requested databases are missing, or required target objects are missing.

#### Scenario: Valid runtime credentials allow authentication to complete
- **WHEN** runtime login credentials with a valid JDBC URL can connect to the SQL Server endpoint, both requested databases exist, and required target objects are present as tables or views
- **THEN** connectivity validation succeeds and the user is authenticated for comparison workflows

#### Scenario: Invalid runtime credentials fail authentication
- **WHEN** runtime login credentials are invalid or one requested database is unavailable
- **THEN** authentication fails with a clear validation error and comparison workflows remain inaccessible

#### Scenario: Missing required target system objects fail authentication
- **WHEN** runtime login credentials are valid and requested databases exist but one or more required target objects are missing
- **THEN** authentication fails with a clear validation error that lists missing schema-qualified object names
