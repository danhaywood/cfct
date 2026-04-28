## 1. Identify and refactor key-discovery matching

- [x] 1.1 Locate the current business-key metadata matching logic for unique index names in the core single-table comparison implementation.
- [x] 1.2 Extract or introduce a shared suffix-matching helper that performs case-insensitive `endsWith(configuredSuffix)` checks.
- [x] 1.3 Update metadata discovery to evaluate both unique indexes and unique constraints using the shared suffix predicate.
- [x] 1.4 Keep existing ambiguity and missing-key failure behavior while updating messages and object labeling for index-or-constraint handling.

## 2. Add and update automated tests

- [x] 2.1 Add unit tests for the suffix predicate covering case-insensitive matching and compound names such as `PurchaseOrder__reference__PK`.
- [x] 2.2 Add or update metadata-discovery tests to verify a PK-suffixed unique constraint is accepted as the row key source.
- [x] 2.3 Add or update metadata-discovery tests to verify ambiguity errors when multiple PK-suffixed index/constraint objects exist.
- [x] 2.4 Ensure existing PK-suffixed unique index scenarios still pass unchanged as regression coverage.

## 3. Validate and finalize

- [x] 3.1 Run relevant module test suites and confirm all updated key-discovery scenarios pass.
- [x] 3.2 Run full comparison fixture tests impacted by business-key discovery and confirm no unrelated regressions.
- [x] 3.3 Update inline comments or developer documentation where key-discovery naming expectations are described.
