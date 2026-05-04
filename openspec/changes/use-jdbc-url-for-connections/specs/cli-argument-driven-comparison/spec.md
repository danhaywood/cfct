## MODIFIED Requirements

### Requirement: CLI accepts first-pass SQL Server comparison arguments
The CLI SHALL accept arguments `--jdbc-url`, `-U`, `-P`, `-l`, `-r`, `-t`, `--output-format`, and `-o` for comparison execution.
The CLI SHALL accept command-time-range arguments for command-driven table selection.
The CLI SHALL require connection values for JDBC URL, username, password, left database, and right database to be resolved from either command-line arguments or `.env` defaults.
The CLI SHALL require exactly one table-selection mode to be provided: explicit table input (`-t` or table file) or command-time-range input.
The CLI SHALL fail with a clear validation error when a required connection value or table-selection mode is missing.
The CLI SHALL fail with a clear validation error when more than one table-selection mode is supplied.

#### Scenario: Required arguments are provided with explicit table mode
- **WHEN** the user runs the CLI with valid values for `--jdbc-url`, `-U`, `-P`, `-l`, `-r`, and `-t`
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Required arguments are provided with command-time-range mode
- **WHEN** the user runs the CLI with valid values for `--jdbc-url`, `-U`, `-P`, `-l`, `-r`, and valid command-time-range arguments
- **THEN** the CLI accepts the input and proceeds to select commands and infer tables for comparison

#### Scenario: Multiple table-selection modes are rejected
- **WHEN** the user supplies explicit table input and command-time-range input in the same invocation
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that only one table-selection mode is allowed

#### Scenario: Required argument is missing
- **WHEN** the user omits one of the required connection values and no `.env` default supplies it
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing argument

### Requirement: CLI executes library comparison using provided server and database arguments
The CLI SHALL create SQL Server JDBC connections for left and right databases using `--jdbc-url`, `-U`, `-P`, `-l`, and `-r`.
The CLI SHALL execute comparison through API service contracts using the parsed table list.
The CLI SHALL NOT directly construct or invoke non-configuration classes from `cfct-impl` for comparison orchestration.
The CLI SHALL register a comparison progress listener and print table-level progress updates as comparison execution advances.
The CLI SHALL emit deterministic comparison report output in the selected output format.
The CLI SHALL write successful output to stdout or the file selected by `-o` according to output destination rules.

#### Scenario: CLI runs comparison for left and right databases
- **WHEN** valid connection, table, output format, and output destination arguments are provided and the databases are reachable
- **THEN** the CLI executes comparison through API service contracts and writes deterministic report output to the selected destination

#### Scenario: CLI reports execution failures
- **WHEN** connection or comparison execution fails
- **THEN** the CLI exits with a non-zero status and writes a clear failure message to stderr

#### Scenario: CLI prints per-table progress during execution
- **WHEN** comparison starts for a multi-table request
- **THEN** the CLI prints progress lines that identify current table and completed-table count while execution is in progress

### Requirement: CLI resolves connection arguments from dotenv file defaults
The CLI SHALL load optional defaults for `--jdbc-url`, `-U`, `-P`, `-l`, and `-r` from a `.env` file.
The CLI SHALL use explicit command-line values in preference to `.env` values for the same setting.
The CLI SHALL not require a `.env` file when all connection values are supplied on the command line.
The CLI SHALL fail with a clear validation error when any required connection value cannot be resolved from either the command line or `.env`.
The CLI SHALL support an explicit `.env` file path option for callers that do not want to use the current working directory `.env` file.
The supported dotenv keys SHALL use the `CFCT_` prefix for JDBC URL, username, password, left database, and right database settings.

#### Scenario: Missing connection arguments are read from dotenv file
- **WHEN** the user omits `--jdbc-url`, `-U`, `-P`, `-l`, and `-r` and a `.env` file supplies all corresponding values
- **THEN** the CLI resolves the connection settings from the `.env` file and proceeds to execute comparison

#### Scenario: Command-line connection value overrides dotenv value
- **WHEN** the same connection setting is supplied both on the command line and in the `.env` file
- **THEN** the CLI uses the command-line value for that setting

#### Scenario: Missing dotenv file is ignored when command-line values are complete
- **WHEN** no `.env` file is present and the user supplies `--jdbc-url`, `-U`, `-P`, `-l`, and `-r` on the command line
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Unresolved connection value is rejected
- **WHEN** a required connection setting is absent from both the command line and the `.env` file
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing setting

#### Scenario: Explicit dotenv file path is loaded
- **WHEN** the user supplies an explicit `.env` file path option and omits connection flags that are present in that file
- **THEN** the CLI resolves the omitted connection settings from the specified `.env` file

#### Scenario: CFCT-prefixed dotenv keys are recognized
- **WHEN** a `.env` file contains `CFCT_JDBC_URL`, `CFCT_USERNAME`, `CFCT_PASSWORD`, `CFCT_LEFT_DATABASE`, and `CFCT_RIGHT_DATABASE`
- **THEN** the CLI resolves connection settings from those keys
