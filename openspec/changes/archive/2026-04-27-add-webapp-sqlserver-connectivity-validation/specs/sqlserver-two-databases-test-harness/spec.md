## ADDED Requirements

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
