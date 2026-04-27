## ADDED Requirements

### Requirement: CLI supports selectable comparison output formats
The CLI SHALL accept an `--output-format` argument for selecting the comparison output format.
The CLI SHALL support `text`, `json`, and `excel` output format values.
The CLI SHALL use `text` output when `--output-format` is omitted.
The CLI SHALL reject unsupported output format values with a clear validation error.
The CLI SHALL render JSON and Excel output using the same report structures as the existing JSON and Excel renderers.

#### Scenario: Text output is the default
- **WHEN** the user runs the CLI without `--output-format`
- **THEN** the CLI emits the existing deterministic text comparison report to stdout

#### Scenario: JSON output format is selected
- **WHEN** the user runs the CLI with `--output-format json`
- **THEN** the CLI emits deterministic JSON comparison output to stdout

#### Scenario: Excel output format is selected
- **WHEN** the user runs the CLI with `--output-format excel`
- **THEN** the CLI emits a valid `.xlsx` workbook byte stream to stdout

#### Scenario: Unsupported output format is rejected
- **WHEN** the user runs the CLI with an unsupported `--output-format` value
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the unsupported output format

## MODIFIED Requirements

### Requirement: CLI accepts first-pass SQL Server comparison arguments
The CLI SHALL accept arguments `-S`, `-U`, `-P`, `-l`, `-r`, `-t`, and `--output-format` for comparison execution.
The CLI SHALL require connection values for server, username, password, left database, and right database to be resolved from either command-line arguments or `.env` defaults.
The CLI SHALL require a table selection to be provided by command-line table list or table file.
The CLI SHALL fail with a clear validation error when a required connection value or table selection is missing.

#### Scenario: Required arguments are provided
- **WHEN** the user runs the CLI with valid values for `-S`, `-U`, `-P`, `-l`, `-r`, and `-t`
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Required argument is missing
- **WHEN** the user omits one of the required connection values and no `.env` default supplies it
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing argument

### Requirement: CLI executes library comparison using provided server and database arguments
The CLI SHALL create SQL Server JDBC connections for left and right databases using `-S`, `-U`, `-P`, `-l`, and `-r`.
The CLI SHALL execute comparison through existing library services using the parsed table list.
The CLI SHALL emit deterministic comparison report output to stdout in the selected output format.

#### Scenario: CLI runs comparison for left and right databases
- **WHEN** valid connection, table, and output format arguments are provided and the databases are reachable
- **THEN** the CLI executes comparison and writes deterministic report output to stdout

#### Scenario: CLI reports execution failures
- **WHEN** connection or comparison execution fails
- **THEN** the CLI exits with a non-zero status and writes a clear failure message to stderr

### Requirement: CLI behavior is covered by automated tests
The project SHALL include automated CLI tests for argument validation, table parsing, output format parsing, renderer dispatch, and execution wiring.
The tests SHALL verify successful invocation and representative failure behavior.

#### Scenario: CLI argument parser is unit-tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify required-argument, malformed-table, and output-format validation behavior

#### Scenario: CLI execution path is tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify that parsed arguments invoke comparison execution and produce expected output behavior for supported output formats
