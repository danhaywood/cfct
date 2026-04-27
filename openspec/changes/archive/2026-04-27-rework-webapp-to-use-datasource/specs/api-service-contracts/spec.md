## MODIFIED Requirements

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
