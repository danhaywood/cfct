## 1. CLI Argument Model and Validation

- [x] 1.1 Create a CLI argument parser component in `sqlcomparer-cli` that reads `-S`, `-U`, `-P`, `-l`, `-r`, and `-t`.
- [x] 1.2 Implement required-argument validation with clear error messages for missing flags or values.
- [x] 1.3 Implement `-t` parsing as ordered comma-separated `schema.table` tokens and reject malformed or blank tokens.
- [x] 1.4 Add parser unit tests covering valid input, missing arguments, and malformed table tokens.

## 2. CLI Execution Wiring

- [x] 2.1 Add a CLI runner entry path from `SqlComparerApplication.main` that invokes comparison execution from parsed arguments.
- [x] 2.2 Implement connection creation for left and right databases using shared server and credentials with `-l` and `-r` database names.
- [x] 2.3 Wire parsed table references into a multi-table comparison request and render deterministic report output to stdout.
- [x] 2.4 Ensure execution failures print clear messages to stderr and exit with non-zero status.

## 3. CLI Tests

- [x] 3.1 Add tests for successful CLI execution flow with mocked/stubbed execution dependencies.
- [x] 3.2 Add tests for failure flow verifying non-zero exit behavior and error reporting.
- [x] 3.3 Update Spring context tests as needed to include new CLI components without breaking existing wiring assertions.

## 4. Validation

- [x] 4.1 Run `sqlcomparer-cli` module tests and fix regressions.
- [x] 4.2 Run full project verification (`mvn verify`) and fix regressions.
