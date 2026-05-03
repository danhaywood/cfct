## Why

The command-log and audit-trail fixture data currently captures logical type identifiers, but it does not model how those logical types map to physical tables in SQL Server.
We need this mapping fixture now so upcoming comparison logic can resolve command and audit logical identifiers to the correct qualified table names, including inheritance cases where one logical type maps to multiple physical tables.

## What Changes

- Extend the SQL Server integration-test fixture schema to create `_util.LogicalTypeTableMapping` in both logical databases.
- Populate deterministic mapping rows for the logical types used by seeded command-log and audit-trail data.
- Include at least one inheritance-style example where a logical type maps to multiple qualified table names.
- Ensure fixture initialization remains idempotent and repeatable across left and right databases.

## Capabilities

### New Capabilities
- `logical-type-to-table-mapping-fixture`: Adds fixture support for mapping Causeway logical types to one or more qualified physical table names.

### Modified Capabilities
- `sqlserver-two-databases-test-harness`: Extend harness fixture requirements to include `_util.LogicalTypeTableMapping` creation and seeded data aligned with command and audit fixtures.

## Impact

This change affects SQL setup scripts and integration-test fixture initialization in `cfct-integration-tests`.
It expands fixture data dependencies for tests that interpret command and audit logical identifiers, while keeping existing APIs and runtime configuration unchanged.
