## MODIFIED Requirements

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
