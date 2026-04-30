## Why

We want to move toward automatic table selection based on executed commands and their audited data changes.
The current integration fixture does not include Apache Causeway command-log and audit-trail tables, so we cannot start building or testing the table-footprint discovery flow.

## What Changes

- Extend the SQL Server integration fixture schema in both logical databases with `causewayExtCommandLog.CommandLogEntry` and `causewayExtAuditTrail.AuditTrailEntry` tables.
- Model `causewayExtCommandLog.CommandLogEntry` with the requested columns and primary key for fixture-level compatibility with downstream footprint queries.
- Model `causewayExtAuditTrail.AuditTrailEntry` with its composite primary key columns only, as required for the current use case.
- Add or update integration tests that verify table creation, key definitions, and fixture readiness for future auto-selection logic.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `sqlserver-two-databases-test-harness`: Extend fixture DDL so both logical databases include the Causeway command-log and audit-trail tables needed for upcoming auto-selection development.

## Impact

The integration-test module fixture SQL and related harness setup tests are affected.
No runtime API or UI behavior changes are included in this change.
This change prepares data-model foundations for later mapping from logical identifiers to compared table entities.
