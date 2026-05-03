## MODIFIED Requirements

### Requirement: API module defines application-facing comparison service contracts
The API module SHALL define interfaces for application-facing comparison orchestration used by CLI and webapp entry points.
The service contracts SHALL cover single-table and multi-table comparison use cases that are currently consumed via implementation classes.
The contracts SHALL use API-owned request and result types so callers can execute comparison flows without importing implementation packages.
The contracts SHALL support entry-point implementations that acquire connections through DataSource-managed lifecycle rather than requiring long-lived Connection ownership at the caller boundary.
The contracts SHALL expose in-memory comparison result structures suitable for direct UI rendering without requiring marshalled externalized report payloads.

#### Scenario: CLI compiles against API service contracts
- **WHEN** the CLI module is compiled
- **THEN** it can invoke comparison orchestration through API interfaces without importing non-configuration classes from `cfct-impl`

#### Scenario: Webapp compiles against API service contracts
- **WHEN** the webapp module is compiled
- **THEN** it can invoke comparison orchestration through API interfaces without importing non-configuration classes from `cfct-impl`

#### Scenario: API contract returns in-memory table results for UI consumers
- **WHEN** a webapp consumer invokes multi-table comparison orchestration
- **THEN** the API returns in-memory per-table comparison result structures that can be rendered directly by UI components
