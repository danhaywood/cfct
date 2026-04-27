# api-service-contracts Specification

## Purpose
TBD - created by archiving change api-first-impl-decoupling-and-naming. Update Purpose after archive.
## Requirements
### Requirement: API module defines application-facing comparison service contracts
The API module SHALL define interfaces for application-facing comparison orchestration used by CLI and webapp entry points.
The service contracts SHALL cover single-table and multi-table comparison use cases that are currently consumed via implementation classes.
The contracts SHALL use API-owned request and result types so callers can execute comparison flows without importing implementation packages.
The contracts SHALL support entry-point implementations that acquire connections through DataSource-managed lifecycle rather than requiring long-lived Connection ownership at the caller boundary.

#### Scenario: CLI compiles against API service contracts
- **WHEN** the CLI module is compiled
- **THEN** it can invoke comparison orchestration through API interfaces without importing non-configuration classes from `sqlcomparer-impl`

#### Scenario: Webapp compiles against API service contracts
- **WHEN** the webapp module is compiled
- **THEN** it can invoke comparison orchestration through API interfaces without importing non-configuration classes from `sqlcomparer-impl`

### Requirement: Implementation module publishes API-contract beans through configuration
The implementation module SHALL provide Spring configuration that binds API service interfaces to concrete implementation classes.
Application modules SHALL import this configuration to obtain service implementations.
Application modules SHALL NOT construct implementation classes directly for comparison orchestration.

#### Scenario: CLI receives comparison service through imported implementation configuration
- **WHEN** the CLI application context starts with imported implementation configuration
- **THEN** required API service interfaces are resolved as Spring beans

#### Scenario: Webapp receives comparison service through imported implementation configuration
- **WHEN** the webapp application context starts with imported implementation configuration
- **THEN** required API service interfaces are resolved as Spring beans

### Requirement: Entry-point modules enforce no direct implementation-type coupling
The CLI and webapp modules SHALL enforce that source code references API contracts for comparison orchestration.
The only allowed compile-time references from CLI and webapp to `sqlcomparer-impl` SHALL be explicit Spring wiring configuration imports.
The project SHALL include automated tests or architecture rules that fail when forbidden direct implementation references are introduced.

#### Scenario: Forbidden implementation import fails architecture rule
- **WHEN** a non-configuration class from `sqlcomparer-impl` is imported by CLI or webapp source code
- **THEN** module architecture verification fails with a clear boundary error

