# cli-argument-driven-comparison Specification

## Purpose
TBD - created by archiving change add-cli-argument-driven-comparison. Update Purpose after archive.
## Requirements
### Requirement: CLI accepts first-pass SQL Server comparison arguments
The CLI SHALL accept arguments `-S`, `-U`, `-P`, `-l`, `-r`, `-t`, `--output-format`, and `-o` for comparison execution.
The CLI SHALL require connection values for server, username, password, left database, and right database to be resolved from either command-line arguments or `.env` defaults.
The CLI SHALL require a table selection to be provided by command-line table list or table file.
The CLI SHALL fail with a clear validation error when a required connection value or table selection is missing.

#### Scenario: Required arguments are provided
- **WHEN** the user runs the CLI with valid values for `-S`, `-U`, `-P`, `-l`, `-r`, and `-t`
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Required argument is missing
- **WHEN** the user omits one of the required connection values and no `.env` default supplies it
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing argument

### Requirement: CLI parses table selection from comma-separated schema.table list
The `-t` argument SHALL be parsed as a comma-separated ordered list of table references.
The CLI SHALL also accept a table-file argument whose file contains one table reference per line.
Each table token SHALL be in `schema.table` format.
The CLI SHALL preserve table order from the selected input source.
The CLI SHALL reject malformed, blank, or duplicate-empty tokens with a clear validation error.
The CLI SHALL reject invocations that supply both `-t` and the table-file argument.

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

#### Scenario: Multiple table sources are rejected
- **WHEN** the user supplies both `-t` and the table-file argument
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that only one table source is allowed

### Requirement: CLI executes library comparison using provided server and database arguments
The CLI SHALL create SQL Server JDBC connections for left and right databases using `-S`, `-U`, `-P`, `-l`, and `-r`.
The CLI SHALL execute comparison through existing library services using the parsed table list.
The CLI SHALL emit deterministic comparison report output in the selected output format.
The CLI SHALL write successful output to stdout or the file selected by `-o` according to output destination rules.

#### Scenario: CLI runs comparison for left and right databases
- **WHEN** valid connection, table, output format, and output destination arguments are provided and the databases are reachable
- **THEN** the CLI executes comparison and writes deterministic report output to the selected destination

#### Scenario: CLI reports execution failures
- **WHEN** connection or comparison execution fails
- **THEN** the CLI exits with a non-zero status and writes a clear failure message to stderr

### Requirement: CLI behavior is covered by automated tests
The project SHALL include automated CLI tests for argument validation, table parsing, output format parsing, output destination validation, renderer dispatch, and execution wiring.
The tests SHALL verify successful invocation and representative failure behavior.

#### Scenario: CLI argument parser is unit-tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify required-argument, malformed-table, output-format, and output-destination validation behavior

#### Scenario: CLI execution path is tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify that parsed arguments invoke comparison execution and produce expected output behavior for supported output formats and destinations

### Requirement: CLI resolves connection arguments from dotenv file defaults
The CLI SHALL load optional defaults for `-S`, `-U`, `-P`, `-l`, and `-r` from a `.env` file.
The CLI SHALL use explicit command-line values in preference to `.env` values for the same setting.
The CLI SHALL not require a `.env` file when all connection values are supplied on the command line.
The CLI SHALL fail with a clear validation error when any required connection value cannot be resolved from either the command line or `.env`.
The CLI SHALL support an explicit `.env` file path option for callers that do not want to use the current working directory `.env` file.

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
- **THEN** the CLI emits deterministic JSON comparison output

#### Scenario: Excel output format is selected
- **WHEN** the user runs the CLI with `--output-format excel` and `-o comparison.xlsx`
- **THEN** the CLI writes a valid `.xlsx` workbook byte stream to `comparison.xlsx`

#### Scenario: Unsupported output format is rejected
- **WHEN** the user runs the CLI with an unsupported `--output-format` value
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the unsupported output format

### Requirement: CLI writes successful output to an optional file destination
The CLI SHALL accept a `-o` argument for the successful output file path.
The CLI SHALL write successful output to the specified file when `-o` is provided.
The CLI SHALL write text and JSON successful output to stdout when `-o` is omitted.
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

#### Scenario: Excel output requires output file
- **WHEN** the user runs the CLI with `--output-format excel` and omits `-o`
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that `-o` is required for Excel output

