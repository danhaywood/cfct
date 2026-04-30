## 1. Add fixture constraints and sample data

- [x] 1.1 Locate fixture schema/data SQL files that currently create and populate `CommandLogEntry` and `AuditTrailEntry`.
- [x] 1.2 Add foreign-key constraint from `causewayExtAuditTrail.AuditTrailEntry.interactionId` to `causewayExtCommandLog.CommandLogEntry.interactionId`.
- [x] 1.3 Insert deterministic `registerProduct` command rows targeting a supplier in left and right fixture datasets.
- [x] 1.4 Insert matching audit rows for the same interaction IDs targeting product aggregates.

## 2. Verify seeded footprint behavior

- [x] 2.1 Add or update harness integration tests to assert FK-backed linkage between command and audit rows.
- [x] 2.2 Add or update tests to assert `registerProduct` command target is supplier and related audit targets are product.
- [x] 2.3 Run integration tests and confirm fixture initialization remains green.
