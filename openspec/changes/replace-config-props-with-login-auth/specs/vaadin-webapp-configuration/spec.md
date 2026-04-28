## MODIFIED Requirements

### Requirement: Webapp configuration models the same logical inputs as CLI
The webapp SHALL provide typed configuration properties for login defaults and execution preferences such as server, database names, username, password, env-file path, output format, and output file.
The webapp SHALL use configured property values only as initial login defaults and SHALL allow users to edit any field before authentication.
The webapp SHALL NOT require these properties to be present for interactive webapp use.
The webapp SHALL document how runtime login inputs and configuration defaults map to equivalent CLI argument concepts.
The webapp SHALL treat table selection as a strategy concern and SHALL NOT require parity with CLI table-input flags.

#### Scenario: Webapp starts without configured credentials
- **WHEN** the webapp starts without username or password values in `application.yml`
- **THEN** typed configuration still binds non-secret defaults and the application starts successfully

#### Scenario: Configured defaults pre-populate login form
- **WHEN** the webapp has connection-related properties configured in `application.yml` or externalized configuration
- **THEN** those values are shown as initial editable defaults in the login form

#### Scenario: Configuration defaults can be overridden externally
- **WHEN** a deploy environment provides overriding Spring configuration values for login defaults
- **THEN** the webapp resolves those values over defaults from `application.yml`

### Requirement: Webapp validates configured SQL Server connectivity and databases
The webapp SHALL validate SQL Server connectivity and database reachability using runtime login credentials instead of startup-time static credentials.
The webapp SHALL execute connectivity validation during login or explicit connection test flow before granting access to comparison workflows.
The webapp SHALL fail login with clear diagnostics when connectivity fails, authentication fails, or requested databases are missing.

#### Scenario: Valid runtime credentials allow authentication to complete
- **WHEN** runtime login credentials can connect to the SQL Server endpoint and both requested databases exist
- **THEN** connectivity validation succeeds and the user is authenticated for comparison workflows

#### Scenario: Invalid runtime credentials fail authentication
- **WHEN** runtime login credentials are invalid or one requested database is unavailable
- **THEN** authentication fails with a clear validation error and comparison workflows remain inaccessible

### Requirement: Home page footer surfaces configured connection context
The webapp home page SHALL display authenticated connection context and SQL connectivity status in a fixed footer or status bar after login succeeds.
The footer or status bar SHALL read displayed values from the active authenticated session context rather than static startup credential configuration.
The footer or status bar SHALL display SQL Server identity, source database name, target database name, and current SQL connectivity status.
The footer or status bar SHALL present connection details with compact spacing and without redundant field labels.
The footer or status bar SHALL right-align SQL connectivity status text.
The footer or status bar SHALL omit or mask sensitive credential values.

#### Scenario: Footer reflects authenticated session context
- **WHEN** a user is logged in with runtime connection details
- **THEN** the home page footer or status bar displays the authenticated connection context and SQL connectivity status

#### Scenario: Footer protects credentials for authenticated users
- **WHEN** the home page footer or status bar renders authenticated connection context
- **THEN** sensitive credential values are omitted or masked
