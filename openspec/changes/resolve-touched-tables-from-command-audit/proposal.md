## Why

We now have fixture data for command log, audit trail, and logical-type-to-table mappings, but no service that resolves those records into the physical tables touched by one or more command interactions.
We need this capability now so selection and comparison workflows can derive table scopes directly from interaction identifiers instead of hard-coded table lists.

## What Changes

- Add a repository-style service that accepts one or more command `interactionId` values and resolves touched physical tables.
- Resolve touched tables by joining command entries to audit entries, parsing `audit.target` values in `logicalTypeName:id` format, and expanding through logical-type-to-table mappings.
- Return a deterministic de-duplicated set of qualified table names across all provided interactions.
- Handle malformed or unmapped audit targets without failing unrelated valid resolutions.

## Capabilities

### New Capabilities
- `command-audit-footprint-table-resolution`: Resolve touched physical tables from command interaction identifiers via audit target parsing and logical-type mapping lookup.

### Modified Capabilities
- None.

## Impact

This change affects repository and service logic in `sqlcomparer-impl` and integration tests in `sqlcomparer-integration-tests`.
It uses existing fixture schemas and adds no new external dependencies or breaking API changes.
