# sqlserver-two-databases-test-harness Specification

## Purpose
TBD - created by archiving change add-sqlserver-test-harness. Update Purpose after archive.
## Requirements
### Requirement: Harness provisions one SQL Server instance with two logical databases
The system SHALL provide an automated integration-test harness that provisions one SQL Server 2022 instance within a single test run and creates two independent logical databases inside that instance.

#### Scenario: One instance starts for the test suite
- **WHEN** the integration test harness is executed in a Docker-enabled environment
- **THEN** it provisions one SQL Server 2022 container instance for the duration of the test run

#### Scenario: Left and right databases are created
- **WHEN** the harness initializes the SQL Server instance
- **THEN** it creates separate left and right logical databases within that instance for test use

### Requirement: Harness exposes both databases independently
The harness SHALL allow tests to address the left and right logical databases independently even though they reside in the same SQL Server instance.

#### Scenario: Databases have distinct connection targets
- **WHEN** the harness exposes connection details for both logical databases
- **THEN** tests can connect to each database independently using the shared server instance and distinct database names

#### Scenario: Tests can target either logical database explicitly
- **WHEN** a test chooses to run setup or verification against one logical database
- **THEN** the harness allows that operation to target the selected database without ambiguity

### Requirement: Harness validates connectivity and readiness
The harness SHALL verify that the provisioned SQL Server instance is ready to accept JDBC connections before smoke tests proceed against either logical database.

#### Scenario: Readiness is confirmed before use
- **WHEN** a smoke test begins interacting with either logical database
- **THEN** the harness waits until the SQL Server instance is ready for JDBC access before executing test SQL

#### Scenario: Startup failure is surfaced as a failing test
- **WHEN** the SQL Server instance fails to become ready within the configured startup window
- **THEN** the integration test run fails with a visible readiness or startup error

### Requirement: Harness supports independent initialization of both logical databases
The harness SHALL allow test setup to initialize the left and right logical databases independently.

#### Scenario: Distinct setup can be applied to each logical database
- **WHEN** a test applies initialization SQL or equivalent setup to both logical databases
- **THEN** the left and right databases can receive different setup independently

#### Scenario: State remains isolated between logical databases
- **WHEN** setup creates or mutates database objects in one logical database
- **THEN** those changes do not appear in the other logical database unless explicitly applied there

### Requirement: Project provides a baseline Spring Boot scaffold for future comparison work
The project SHALL include a baseline Spring Boot structure in the CLI module that can host future database-comparison application services while coexisting with the integration-test harness.
The SQL Server harness SHALL live outside the CLI module in the integration-test module.

#### Scenario: Build supports application and test scaffolding
- **WHEN** the project is built from a clean checkout
- **THEN** the Spring Boot application structure and the SQL Server harness tests are both part of the Maven reactor layout

#### Scenario: Spring Boot application lives in CLI module
- **WHEN** the source tree is inspected
- **THEN** the Spring Boot application entry point is located in the CLI module

#### Scenario: Harness can evolve without replacing the project foundation
- **WHEN** future comparison features are added
- **THEN** they can be implemented on top of the multi-module project structure without discarding the harness foundation introduced by this change

### Requirement: Harness supports the preferred test style
The project SHALL support a test style based on AssertJ, JUnit 5 parameterized tests with enum sources, and Approvals where characterization-style verification is useful.

#### Scenario: Fluent assertions are available in harness tests
- **WHEN** developers write harness tests
- **THEN** they can express assertions using AssertJ

#### Scenario: Repeated harness scenarios can be parameterized
- **WHEN** developers need to test repeated left/right or mode-driven scenarios
- **THEN** the project supports JUnit 5 parameterized tests using enum sources

#### Scenario: Stable textual outputs can be characterized
- **WHEN** a harness or future comparison test produces stable textual or tabular output
- **THEN** the project supports Approvals-based characterization testing for that output

### Requirement: Harness covers webapp connectivity-validation scenarios
The integration-test harness SHALL provide deterministic SQL Server containerized scenarios for webapp connectivity and configured-database existence validation.
The harness SHALL support at least one positive scenario where configured left and right databases exist.
The harness SHALL support negative scenarios for missing database and unreachable/invalid connection inputs used by webapp validation tests.

#### Scenario: Harness supports successful webapp connectivity validation
- **WHEN** webapp connectivity validation tests run against the harness with valid SQL Server and database configuration
- **THEN** tests verify that validation succeeds for both logical databases

#### Scenario: Harness supports missing-database validation failure
- **WHEN** webapp connectivity validation tests target a database name that is not present in the harness SQL Server instance
- **THEN** tests verify that webapp validation fails with a clear missing-database error

#### Scenario: Harness supports connectivity failure validation
- **WHEN** webapp connectivity validation tests use an unreachable endpoint or invalid connection settings
- **THEN** tests verify that webapp validation fails with a clear connectivity error

### Requirement: Harness supports browser-level connectivity-status testing
The SQL Server harness SHALL provide deterministic connection settings consumable by headless Playwright tests.
The harness SHALL support a happy-path setup where both configured logical databases exist.
The harness SHALL support failure-path setups for missing databases and unreachable or invalid connection settings.

#### Scenario: Harness drives happy-path browser connectivity test
- **WHEN** Playwright runs against the webapp configured with harness-backed valid SQL Server settings
- **THEN** the browser test observes home-page connection status OK

#### Scenario: Harness drives failure-path browser connectivity test
- **WHEN** Playwright runs against the webapp with harness-backed invalid connectivity or missing-database settings
- **THEN** the browser test observes home-page connection status FAILED with failure detail

### Requirement: Harness fixture includes Causeway command-log table in both logical databases
The harness SHALL create a `causewayExtCommandLog.CommandLogEntry` table in the left logical database.
The harness SHALL create a `causewayExtCommandLog.CommandLogEntry` table in the right logical database.
Each table SHALL include command-log columns required for footprint-oriented selection setup, including interaction, execution mode, logical member identifier, timestamp, target, and replay-state data.
Each table SHALL define `interactionId` as the primary key.

#### Scenario: Command-log table exists in both logical databases
- **WHEN** the harness initializes fixture schema for a test run
- **THEN** both logical databases contain `causewayExtCommandLog.CommandLogEntry` with primary key `interactionId`

### Requirement: Harness fixture includes Causeway audit-trail table in both logical databases
The harness SHALL create a `causewayExtAuditTrail.AuditTrailEntry` table in the left logical database.
The harness SHALL create a `causewayExtAuditTrail.AuditTrailEntry` table in the right logical database.
Each table SHALL include `interactionId`, `sequence`, `target`, and `propertyId` columns.
Each table SHALL define a composite primary key across `interactionId`, `sequence`, `target`, and `propertyId`.

#### Scenario: Audit-trail table exists in both logical databases with composite key
- **WHEN** the harness initializes fixture schema for a test run
- **THEN** both logical databases contain `causewayExtAuditTrail.AuditTrailEntry` with composite primary key (`interactionId`, `sequence`, `target`, `propertyId`)

