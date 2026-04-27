# cli-argument-driven-comparison Specification

## Purpose
TBD - created by archiving change add-cli-argument-driven-comparison. Update Purpose after archive.

## Requirements
### Requirement: CLI accepts first-pass SQL Server comparison arguments
The CLI SHALL accept arguments `-S`, `-U`, `-P`, `-l`, `-r`, and `-t` for comparison execution.
The CLI SHALL require all of these arguments for execution.
The CLI SHALL fail with a clear validation error when a required argument is missing.

#### Scenario: Required arguments are provided
- **WHEN** the user runs the CLI with valid values for `-S`, `-U`, `-P`, `-l`, `-r`, and `-t`
- **THEN** the CLI accepts the input and proceeds to execute comparison

#### Scenario: Required argument is missing
- **WHEN** the user omits one of the required arguments
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the missing argument

### Requirement: CLI parses table selection from comma-separated schema.table list
The `-t` argument SHALL be parsed as a comma-separated ordered list of table references.
Each table token SHALL be in `schema.table` format.
The CLI SHALL preserve table order from the input list.
The CLI SHALL reject malformed, blank, or duplicate-empty tokens with a clear validation error.

#### Scenario: Multiple tables are parsed in order
- **WHEN** `-t` is `dbo.Supplier,dbo.PurchaseOrder`
- **THEN** the CLI creates a two-table request in that same order

#### Scenario: Malformed table token is rejected
- **WHEN** `-t` contains a token that is not in `schema.table` format
- **THEN** the CLI exits with a non-zero status and reports a clear validation error

### Requirement: CLI executes library comparison using provided server and database arguments
The CLI SHALL create SQL Server JDBC connections for left and right databases using `-S`, `-U`, `-P`, `-l`, and `-r`.
The CLI SHALL execute comparison through existing library services using the parsed table list.
The CLI SHALL emit deterministic comparison report output to stdout.

#### Scenario: CLI runs comparison for left and right databases
- **WHEN** valid connection and table arguments are provided and the databases are reachable
- **THEN** the CLI executes comparison and writes deterministic report output to stdout

#### Scenario: CLI reports execution failures
- **WHEN** connection or comparison execution fails
- **THEN** the CLI exits with a non-zero status and writes a clear failure message to stderr

### Requirement: CLI behavior is covered by automated tests
The project SHALL include automated CLI tests for argument validation, table parsing, and execution wiring.
The tests SHALL verify successful invocation and representative failure behavior.

#### Scenario: CLI argument parser is unit-tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify required-argument and malformed-table validation behavior

#### Scenario: CLI execution path is tested
- **WHEN** the CLI test suite runs
- **THEN** tests verify that parsed arguments invoke comparison execution and produce expected output behavior
