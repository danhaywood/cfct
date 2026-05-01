## 1. Repository contract and query plumbing

- [x] 1.1 Locate or introduce the repository/service contract in `sqlcomparer-impl` for resolving touched tables from command `interactionId` values.
- [x] 1.2 Implement SQL access that joins command and audit data by `interactionId` and reads audit `target` values for provided interactions.
- [x] 1.3 Add mapping-table lookup against `util.LogicalTypeTableMapping` and return a distinct deterministic set of qualified table names.

## 2. Parsing and resolution behavior

- [x] 2.1 Implement target parsing for `logicalTypeName:id` format using the prefix before the first `:` as logical type name.
- [x] 2.2 Ensure malformed targets and unmapped logical types are ignored without failing valid resolutions.
- [x] 2.3 Ensure one logical type can expand to multiple qualified table names in the returned result.

## 3. Verification

- [x] 3.1 Add or extend integration tests that pass one interactionId and verify resolved touched tables.
- [x] 3.2 Add or extend integration tests that pass multiple interactionIds and verify de-duplicated deterministic union behavior.
- [x] 3.3 Run relevant module tests and confirm the new repository behavior remains stable with current SQL Server fixtures.
