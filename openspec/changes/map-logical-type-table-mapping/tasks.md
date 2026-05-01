## 1. Fixture schema updates

- [x] 1.1 Locate the SQL Server harness fixture setup scripts/classes that create command and audit tables for left and right databases.
- [x] 1.2 Add idempotent `util` schema creation and `util.LogicalTypeTableMapping` table creation to both logical database setup paths.
- [x] 1.3 Ensure the new table definition matches the required columns (`logicalTypeName`, `qualifiedName`) and remains rerunnable.

## 2. Fixture seed data updates

- [x] 2.1 Add deterministic mapping rows for logical types used by seeded command-log and audit-trail fixture rows.
- [x] 2.2 Add at least one inheritance-style sample where a single logical type maps to multiple distinct qualified table names.
- [x] 2.3 Keep seed logic idempotent so repeated fixture initialization does not produce duplicates or failures.

## 3. Verification and documentation

- [x] 3.1 Extend or add integration tests to assert `util.LogicalTypeTableMapping` exists in both logical databases.
- [x] 3.2 Extend or add integration tests to assert mapping rows align with seeded command/audit logical identifiers and include a multi-table mapping case.
- [x] 3.3 Run module or full Maven tests for integration fixtures and update any fixture-related test documentation/comments if needed.
