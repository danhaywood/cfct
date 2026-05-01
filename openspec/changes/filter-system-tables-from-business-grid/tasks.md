## 1. Table-catalog exclusion logic

- [x] 1.1 Update webapp table-catalog discovery to exclude `causewayExtCommandLog.CommandLogEntry`.
- [x] 1.2 Update webapp table-catalog discovery to exclude `causewayExtAuditTrail.AuditTrailEntry`.
- [x] 1.3 Update webapp table-catalog discovery to exclude `util.LogicalTypeTableMapping`.
- [x] 1.4 Keep existing eligibility and sorting/filtering behavior unchanged for remaining business tables.

## 2. Verification

- [x] 2.1 Add or update unit tests for table-catalog discovery to assert excluded system tables are absent.
- [x] 2.2 Add or update UI/browser tests to assert excluded system tables do not appear in the business table grid.
- [x] 2.3 Run relevant webapp tests and confirm behavior remains stable.
