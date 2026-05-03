# cli-argument-driven-comparison Specification

## Purpose
TBD - created by archiving change add-cli-argument-driven-comparison. Update Purpose after archive.
## Requirements
### Requirement: CLI accepts first-pass SQL Server comparison arguments
The CLI SHALL accept arguments `-S`, `-U`, `-P`, `-l`, `-r`, `-t`, `--output-format`, and `-o` for comparison execution.
The CLI SHALL accept command-time-range arguments for command-driven table selection.
The CLI SHALL require connection values for server, username, password, left database, and right database to be resolved from either command-line arguments or `.env` defaults.
The CLI SHALL require exactly one table-selection mode to be provided: explicit table input (`-t` or table file) or command-time-range input.
The CLI SHALL fail with a clear validation error when a required connection value or table-selection mode is missing.
The CLI SHALL fail with a clear validation error when more than one table-selection mode is supplied.

#### Scenario: Required arguments are provided with explicit table mode
- **WHEN** the user runs the CLI with valid values for `-S`, `-U`, `-P`, `-l`, `-r`, and `-t`
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Required arguments are provided with command-time-range mode
- **WHEN** the user runs the CLI with valid values for `-S`, `-U`, `-P`, `-l`, `-r`, and valid command-time-range arguments
- **THEN** the CLI accepts the input and proceeds to select commands and infer tables for comparison

#### Scenario: Multiple table-selection modes are rejected
- **WHEN** the user supplies explicit table input and command-time-range input in the same invocation
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that only one table-selection mode is allowed

#### Scenario: Required argument is missing
- **WHEN** the user omits one of the required connection values and no `.env` default supplies it
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing argument

### Requirement: CLI parses table selection from comma-separated schema.table list
The `-t` argument SHALL be parsed as a comma-separated ordered list of table references.
The CLI SHALL also accept a table-file argument whose file contains one table reference per line.
Each table token SHALL be in `schema.table` format.
The CLI SHALL preserve table order from the selected explicit table source.
The CLI SHALL reject malformed, blank, or duplicate-empty tokens with a clear validation error.
The CLI SHALL reject invocations that supply both `-t` and the table-file argument.
The CLI SHALL accept command-time-range selection where start and end boundaries are inclusive.
The CLI SHALL resolve commands whose timestamps fall within the inclusive range.
The CLI SHALL infer business tables from the resolved commands using command-footprint table resolution.
The CLI SHALL fail with a clear validation error when command-time-range mode does not resolve any commands.
The CLI SHALL fail with a clear validation error when command-time-range mode resolves commands but no business tables can be inferred.

#### Scenario: Multiple tables are parsed in order
- **WHEN** `-t` is `dbo.Supplier,dbo.PurchaseOrder`
- **THEN** the CLI creates a two-table request in that same order

#### Scenario: Malformed table token is rejected
- **WHEN** `-t` contains a token that is not in `schema.table` format
- **THEN** the CLI exits with a non-zero status and reports a clear validation error

#### Scenario: Table file is parsed in order
- **WHEN** the table-file argument points to a flat file containing `dbo.Supplier` and `dbo.PurchaseOrder` on separate lines
- **THEN** the CLI creates a two-table request in that same order

#### Scenario: Blank table-file line is rejected
- **WHEN** the table-file argument points to a file containing a blank line where a table reference is expected
- **THEN** the CLI exits with a non-zero status and reports a clear validation error

#### Scenario: Multiple explicit table sources are rejected
- **WHEN** the user supplies both `-t` and the table-file argument
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that only one explicit table source is allowed

#### Scenario: Command-time-range boundaries are inclusive
- **WHEN** command-time-range mode is used with start and end timestamps
- **THEN** commands with timestamps exactly equal to start or end are included in the selected command set

#### Scenario: Command-time-range resolves tables for comparison
- **WHEN** command-time-range mode selects one or more commands that map to business tables
- **THEN** the CLI builds the comparison request using the inferred business table set

#### Scenario: Empty command-time-range result is rejected
- **WHEN** command-time-range mode selects no commands
- **THEN** the CLI exits with a non-zero status and reports a clear validation error

#### Scenario: No inferred business tables is rejected
- **WHEN** command-time-range mode selects commands but table inference returns no business tables
- **THEN** the CLI exits with a non-zero status and reports a clear validation error

### Requirement: CLI executes library comparison using provided server and database arguments
The CLI SHALL create SQL Server JDBC connections for left and right databases using `-S`, `-U`, `-P`, `-l`, and `-r`.
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

### Requirement: CLI behavior is covered by automated tests
The project SHALL include automated CLI tests for argument validation, table parsing, command-time-range parsing, output format parsing, output destination validation, renderer dispatch, and execution wiring.
The tests SHALL verify successful invocation and representative failure behavior.
The project SHALL include documentation updates in README for command-time-range usage and examples.

#### Scenario: CLI argument parser is unit-tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify required-argument, malformed-table, command-time-range validation, output-format, and output-destination behavior

#### Scenario: CLI execution path is tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify that parsed arguments invoke comparison execution and produce expected output behavior for supported output formats and destinations

#### Scenario: Command-time-range inference path is tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify inclusive range command selection, inferred table request construction, and empty-result failure behavior

### Requirement: CLI resolves connection arguments from dotenv file defaults
The CLI SHALL load optional defaults for `-S`, `-U`, `-P`, `-l`, and `-r` from a `.env` file.
The CLI SHALL use explicit command-line values in preference to `.env` values for the same setting.
The CLI SHALL not require a `.env` file when all connection values are supplied on the command line.
The CLI SHALL fail with a clear validation error when any required connection value cannot be resolved from either the command line or `.env`.
The CLI SHALL support an explicit `.env` file path option for callers that do not want to use the current working directory `.env` file.
The supported dotenv keys SHALL use the `CFCT_` prefix for server, username, password, left database, and right database settings.

#### Scenario: Missing connection arguments are read from dotenv file
- **WHEN** the user omits `-S`, `-U`, `-P`, `-l`, and `-r` and a `.env` file supplies all corresponding values
- **THEN** the CLI resolves the connection settings from the `.env` file and proceeds to execute comparison

#### Scenario: Command-line connection value overrides dotenv value
- **WHEN** the same connection setting is supplied both on the command line and in the `.env` file
- **THEN** the CLI uses the command-line value for that setting

#### Scenario: Missing dotenv file is ignored when command-line values are complete
- **WHEN** no `.env` file is present and the user supplies `-S`, `-U`, `-P`, `-l`, and `-r` on the command line
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Unresolved connection value is rejected
- **WHEN** a required connection setting is absent from both the command line and the `.env` file
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing setting

#### Scenario: Explicit dotenv file path is loaded
- **WHEN** the user supplies an explicit `.env` file path option and omits connection flags that are present in that file
- **THEN** the CLI resolves the omitted connection settings from the specified `.env` file

#### Scenario: CFCT-prefixed dotenv keys are recognized
- **WHEN** a `.env` file contains `CFCT_SERVER`, `CFCT_USERNAME`, `CFCT_PASSWORD`, `CFCT_LEFT_DATABASE`, and `CFCT_RIGHT_DATABASE`
- **THEN** the CLI resolves connection settings from those keys

### Requirement: CLI supports selectable comparison output formats
The CLI SHALL accept an `--output-format` argument for selecting the comparison output format.
The CLI SHALL support `text`, `json`, `yaml`, and `excel` output format values.
The CLI SHALL use `text` output when `--output-format` is omitted.
The CLI SHALL reject unsupported output format values with a clear validation error.
The CLI SHALL render JSON, YAML, and Excel output using the same report structures as their corresponding renderers.

#### Scenario: Text output is the default
- **WHEN** the user runs the CLI without `--output-format`
- **THEN** the CLI emits the existing deterministic text comparison report to stdout

#### Scenario: JSON output format is selected
- **WHEN** the user runs the CLI with `--output-format json`
- **THEN** the CLI emits deterministic JSON comparison output

#### Scenario: YAML output format is selected
- **WHEN** the user runs the CLI with `--output-format yaml`
- **THEN** the CLI emits deterministic YAML comparison output

#### Scenario: Excel output format is selected
- **WHEN** the user runs the CLI with `--output-format excel` and `-o comparison.xlsx`
- **THEN** the CLI writes a valid `.xlsx` workbook byte stream to `comparison.xlsx`

#### Scenario: Unsupported output format is rejected
- **WHEN** the user runs the CLI with an unsupported `--output-format` value
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the unsupported output format

### Requirement: CLI writes successful output to an optional file destination
The CLI SHALL accept a `-o` argument for the successful output file path.
The CLI SHALL write successful output to the specified file when `-o` is provided.
The CLI SHALL write text, JSON, and YAML successful output to stdout when `-o` is omitted.
The CLI SHALL require `-o` when the selected output format is `excel`.
The CLI SHALL reject `--output-format excel` without `-o` with a clear validation error.
The CLI SHALL continue to write validation and execution errors to stderr.

#### Scenario: Text output is written to a file
- **WHEN** the user runs the CLI with `--output-format text` and `-o comparison.txt`
- **THEN** the CLI writes the deterministic text comparison report to `comparison.txt`

#### Scenario: JSON output is written to stdout by default
- **WHEN** the user runs the CLI with `--output-format json` and omits `-o`
- **THEN** the CLI writes deterministic JSON comparison output to stdout

#### Scenario: JSON output is written to a file
- **WHEN** the user runs the CLI with `--output-format json` and `-o comparison.json`
- **THEN** the CLI writes deterministic JSON comparison output to `comparison.json`

#### Scenario: YAML output is written to stdout by default
- **WHEN** the user runs the CLI with `--output-format yaml` and omits `-o`
- **THEN** the CLI writes deterministic YAML comparison output to stdout

#### Scenario: YAML output is written to a file
- **WHEN** the user runs the CLI with `--output-format yaml` and `-o comparison.yaml`
- **THEN** the CLI writes deterministic YAML comparison output to `comparison.yaml`

#### Scenario: Excel output requires output file
- **WHEN** the user runs the CLI with `--output-format excel` and omits `-o`
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that `-o` is required for Excel output

