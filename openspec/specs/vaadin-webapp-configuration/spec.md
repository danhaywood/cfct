# vaadin-webapp-configuration Specification

## Purpose
TBD - created by archiving change add-vaadin-webapp-config-scaffold. Update Purpose after archive.
## Requirements
### Requirement: Repository provides a Vaadin webapp module scaffold
The repository SHALL provide a Maven module named `sqlcomparer-webapp`.
The webapp module SHALL package a Spring Boot application with Vaadin Flow integration.
The webapp module SHALL start successfully with a minimal bootstrap route suitable for future UI development.
The webapp module SHALL use a stable Vaadin Flow release line, selecting the latest stable line identified during proposal work.

#### Scenario: Webapp module is present in source tree
- **WHEN** the project modules are inspected
- **THEN** a `sqlcomparer-webapp` module exists with its own `pom.xml` and Spring Boot application entry point

#### Scenario: Webapp application starts with minimal route
- **WHEN** the webapp module is started in a local development environment
- **THEN** Spring Boot and Vaadin initialize successfully and render a minimal placeholder view

### Requirement: Webapp configuration models the same logical inputs as CLI
The webapp SHALL provide typed configuration properties that represent server, username, password, left database, right database, env-file path, output format, and output file.
The webapp SHALL load defaults from `application.yml` and allow externalized overrides through Spring configuration sources.
The webapp SHALL document how each shared webapp property maps to the equivalent CLI argument concept.
The webapp SHALL treat table selection as a strategy concern and SHALL NOT require parity with CLI table-input flags.

#### Scenario: Webapp properties bind shared execution settings from application.yml
- **WHEN** the webapp starts with configured values in `application.yml`
- **THEN** typed configuration properties are populated with server, credential, database, env-file, and output settings

#### Scenario: Webapp properties can be overridden externally
- **WHEN** a deploy environment provides overriding Spring configuration values
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
The left panel SHALL list discovered tables with one checkbox control per table.
The layout SHALL reserve a left selection region and a right comparison region, with the left region sized for selection-focused interaction.
The webapp SHALL provide live feedback showing how many tables are selected.

#### Scenario: User selects and deselects eligible tables
- **WHEN** a user toggles checkboxes for eligible tables in the left panel
- **THEN** the selected-table feedback updates immediately to reflect the current count

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

