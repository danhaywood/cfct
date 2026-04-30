## 1. Extend fixture DDL for Causeway tables

- [ ] 1.1 Locate the SQL Server harness fixture initialization scripts/classes that create baseline tables in both logical databases.
- [ ] 1.2 Add DDL for `causewayExtCommandLog.CommandLogEntry` with requested columns and primary key `transactionId`.
- [ ] 1.3 Add DDL for `causewayExtAuditTrail.AuditTrailEntry` with required columns and composite primary key (`interactionId`, `sequence`, `target`, `propertyId`).
- [ ] 1.4 Ensure schema/table creation is deterministic and idempotent within existing harness setup/reset flow.

## 2. Verify fixture shape in tests

- [ ] 2.1 Add or update integration tests to assert both new tables exist in left and right logical databases.
- [ ] 2.2 Add or update integration assertions to verify primary-key definitions for both tables.
- [ ] 2.3 Run the integration-test module and confirm fixture initialization and existing harness scenarios still pass.
