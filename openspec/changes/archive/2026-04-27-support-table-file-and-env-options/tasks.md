## 1. CLI Input Model

- [x] 1.1 Extend the CLI parser to recognize `--tables-file` and `--env-file` in addition to the existing flags.
- [x] 1.2 Update argument validation so connection settings are required after CLI and `.env` values are merged, not only as raw command-line flags.
- [x] 1.3 Update unknown-argument and missing-value validation to cover the new options.

## 2. Table File Support

- [x] 2.1 Extract shared `schema.table` token parsing so comma-separated tables and file tables use the same validation.
- [x] 2.2 Implement table-file loading that reads one table reference per line and preserves file order.
- [x] 2.3 Reject blank or malformed table-file lines with clear validation errors.
- [x] 2.4 Reject invocations that supply both `-t` and `--tables-file`.

## 3. Dotenv Support

- [x] 3.1 Implement a small `.env` reader for simple `KEY=value` entries with blank-line and comment handling.
- [x] 3.2 Map `.env` keys to connection settings using `SQLCOMPARER_SERVER`, `SQLCOMPARER_USERNAME`, `SQLCOMPARER_PASSWORD`, `SQLCOMPARER_LEFT_DATABASE`, and `SQLCOMPARER_RIGHT_DATABASE`.
- [x] 3.3 Load `.env` from the working directory by default when present.
- [x] 3.4 Load the file specified by `--env-file` when provided.
- [x] 3.5 Ensure explicit CLI values override `.env` values and secret values are not logged.

## 4. Tests and Validation

- [x] 4.1 Add parser tests for successful table-file parsing and order preservation.
- [x] 4.2 Add parser tests for blank table-file lines, malformed table-file entries, and conflicting table sources.
- [x] 4.3 Add parser tests for `.env` fallback, CLI-over-`.env` precedence, missing default `.env`, explicit `--env-file`, and unresolved required values.
- [x] 4.4 Update command-runner tests as needed for the revised required-argument behavior.
- [x] 4.5 Run the CLI module test suite and fix any regressions.
