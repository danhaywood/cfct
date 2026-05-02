# vaadin-webapp-configuration Specification

## Purpose
TBD - created by archiving change add-vaadin-webapp-config-scaffold. Update Purpose after archive.
## Requirements
### Requirement: Repository provides a Vaadin webapp module scaffold
The repository SHALL provide a Maven module named `cfct-webapp`.
The webapp module SHALL package a Spring Boot application with Vaadin Flow integration.
The webapp module SHALL start successfully with a minimal bootstrap route suitable for future UI development.
The webapp module SHALL use a stable Vaadin Flow release line, selecting the latest stable line identified during proposal work.

#### Scenario: Webapp module is present in source tree
- **WHEN** the project modules are inspected
- **THEN** a `cfct-webapp` module exists with its own `pom.xml` and Spring Boot application entry point

#### Scenario: Webapp application starts with minimal route
- **WHEN** the webapp module is started in a local development environment
- **THEN** Spring Boot and Vaadin initialize successfully and render a minimal placeholder view

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

### Requirement: Webapp resolves table targets through SelectionPlan strategies
The webapp SHALL define a `SelectionPlan` abstraction that resolves comparison targets as `List<TableRef>`.
The webapp SHALL provide an initial explicit selection-plan implementation that stores concrete `TableRef` values.
The webapp SHALL allow future automated selection-plan implementations without changing the comparison execution contract.
The webapp SHALL consume resolved `List<TableRef>` output from `SelectionPlan` when preparing comparison execution.

#### Scenario: Explicit selection plan resolves concrete tables
- **WHEN** the webapp uses the explicit selection-plan implementation with configured concrete `TableRef` values
- **THEN** the plan resolves those values as the comparison table list

#### Scenario: Selection plan output is used for execution preparation
- **WHEN** the webapp prepares a comparison run
- **THEN** it reads table targets from `SelectionPlan` output instead of CLI table-input structures

#### Scenario: Automated selection plan can be added later
- **WHEN** a new automated selection-plan implementation is introduced
- **THEN** it can plug into the same `SelectionPlan` contract and return `List<TableRef>` without changing core execution interfaces

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

### Requirement: Home page surfaces SQL connectivity validation status
The webapp home page SHALL display SQL connectivity validation status as an explicit state that is either OK or FAILED.
The webapp SHALL display a concise failure summary when the status is FAILED.
The rendered status block SHALL be stable enough for deterministic browser-level assertions.

#### Scenario: Home page shows OK for successful validation
- **WHEN** startup SQL connectivity validation succeeds for configured server and databases
- **THEN** the home page shows connection status OK and no failure summary

#### Scenario: Home page shows FAILED for validation errors
- **WHEN** startup SQL connectivity validation reports a connectivity, authentication, or missing-database error
- **THEN** the home page shows connection status FAILED with a concise failure summary

### Requirement: Home page provides a manual table-selection stage
The webapp home page SHALL provide a manual table-selection panel on the left side of the page before comparison execution.
The left panel SHALL list discovered tables with one checkbox control per eligible table.
The layout SHALL reserve a left selection region and a right comparison region, with the left region sized for selection-focused interaction.
The manual selection stage SHALL expose a compare action above the table catalog and aligned to the right within the navigation panel.

#### Scenario: User selects and deselects eligible tables
- **WHEN** a user toggles checkboxes for eligible tables in the left panel
- **THEN** the selected-table state updates immediately and is available for comparison activation

#### Scenario: Compare action is positioned above selection table
- **WHEN** the home page renders the manual table-selection stage
- **THEN** the compare action is visible above the table catalog and right-aligned in the navigation panel

#### Scenario: Selection stage is separate from comparison stage
- **WHEN** a user changes table selections in the left panel
- **THEN** comparison execution is not triggered until an explicit run action is invoked

### Requirement: Home page enforces `_BK` eligibility in manual selection
The webapp SHALL evaluate table eligibility for manual selection based on `_PK` requirement rules.
The webapp SHALL render ineligible tables in a visually disabled style.
The webapp SHALL disable checkbox interaction for ineligible tables.

#### Scenario: Ineligible table is visible but disabled
- **WHEN** the table list includes a table that does not satisfy `_PK` requirement rules
- **THEN** the table row is shown in greyed or disabled styling and its checkbox cannot be selected

### Requirement: Webapp invokes comparison orchestration through API contracts
The webapp SHALL invoke comparison orchestration through interfaces defined in `cfct-api`.
The webapp SHALL obtain implementations of those interfaces via imported Spring configuration from `cfct-impl`.
The webapp SHALL use DataSource-managed connection acquisition in its execution path rather than retaining externally-managed Connection state in web components.
The webapp SHALL NOT directly reference non-configuration implementation classes from `cfct-impl`.

#### Scenario: Webapp startup wiring resolves API comparison services
- **WHEN** the webapp application context starts with imported implementation wiring configuration
- **THEN** API comparison service interfaces required by the web layer are available as beans

#### Scenario: Webapp source avoids direct implementation-type coupling
- **WHEN** webapp source imports are inspected
- **THEN** no non-configuration type from `cfct-impl` is referenced by webapp code

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

