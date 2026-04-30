## Why

The fixture now includes Causeway command-log and audit-trail tables, but it does not yet contain representative rows to exercise footprint-based table auto-selection.
We need deterministic sample data linking command execution to audited object changes so upcoming selector logic can be developed and tested.

## What Changes

- Extend fixture data scripts to insert sample `CommandLogEntry` and `AuditTrailEntry` rows in both logical databases.
- Add a foreign-key relationship from `causewayExtAuditTrail.AuditTrailEntry.interactionId` to `causewayExtCommandLog.CommandLogEntry.interactionId`.
- Seed a `registerProduct` command example where a command acting on a `Supplier` produces audit rows for creation of a `Product`.
- Add integration assertions validating referential integrity and expected sample-row shape.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `sqlserver-two-databases-test-harness`: Extend fixture data and integrity constraints for Causeway command/audit tables with realistic sample rows tied to existing business tables.

## Impact

Fixture SQL in integration-test resources is affected, plus harness integration tests that verify fixture shape/data.
No production API/UI behavior changes are introduced.
