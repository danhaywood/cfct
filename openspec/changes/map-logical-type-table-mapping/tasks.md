## 1. Fixture schema updates

- [ ] 1.1 Locate the SQL Server harness fixture setup scripts/classes that create command and audit tables for left and right databases.
- [ ] 1.2 Add idempotent `_util` schema creation and `_util.LogicalTypeTableMapping` table creation to both logical database setup paths.
- [ ] 1.3 Ensure the new table definition matches the required columns (`logicalTypeName`, `qualifiedName`) and remains rerunnable.

## 2. Fixture seed data updates

- [ ] 2.1 Add deterministic mapping rows for logical types used by seeded command-log and audit-trail fixture rows.
- [ ] 2.2 Add at least one inheritance-style sample where a single logical type maps to multiple distinct qualified table names.
- [ ] 2.3 Keep seed logic idempotent so repeated fixture initialization does not produce duplicates or failures.

## 3. Verification and documentation

- [ ] 3.1 Extend or add integration tests to assert `_util.LogicalTypeTableMapping` exists in both logical databases.
- [ ] 3.2 Extend or add integration tests to assert mapping rows align with seeded command/audit logical identifiers and include a multi-table mapping case.
- [ ] 3.3 Run module or full Maven tests for integration fixtures and update any fixture-related test documentation/comments if needed.
