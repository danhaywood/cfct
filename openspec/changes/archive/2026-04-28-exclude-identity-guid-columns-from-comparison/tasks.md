## 1. Core metadata and column partitioning updates

- [x] 1.1 Identify where SQL Server column metadata currently distinguishes compared versus ignored columns.
- [x] 1.2 Extend metadata handling to mark identity-backed columns as always ignored for value comparison.
- [x] 1.3 Extend metadata handling to mark `UNIQUEIDENTIFIER` datatype columns as always ignored for value comparison.
- [x] 1.4 Add case-insensitive built-in exclusion for columns named `uuid` or `guid`.
- [x] 1.5 Ensure built-in technical-column exclusions compose correctly with caller-provided ignored-column options.

## 2. Comparison behavior integrity

- [x] 2.1 Preserve business-key discovery behavior using `_PK` unique index conventions.
- [x] 2.2 Ensure row matching still works when key columns are technical identifiers excluded from compared-value columns.
- [x] 2.3 Verify compared-column lists and row-difference outputs exclude technical identifiers while retaining business-domain differences.

## 3. Tests and fixture characterization

- [x] 3.1 Add or update single-table comparison unit/integration tests for identity exclusion behavior.
- [x] 3.2 Add or update tests for `uuid`/`guid` name-based exclusion behavior.
- [x] 3.3 Add or update tests for SQL Server `UNIQUEIDENTIFIER` datatype exclusion behavior.
- [x] 3.4 Update purchase-order fixture characterization expectations so identity-only differences are ignored.
- [x] 3.5 Verify multi-table comparison behavior remains deterministic with new exclusion defaults.

## 4. Validation and documentation

- [x] 4.1 Update any user-facing docs or notes that describe default compared/ignored columns.
- [x] 4.2 Run module test suites affected by column partitioning logic and fixture expectations.
- [x] 4.3 Run OpenSpec validation for the change and resolve issues.
