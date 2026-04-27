## ADDED Requirements

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

## MODIFIED Requirements

### Requirement: CLI accepts first-pass SQL Server comparison arguments
The CLI SHALL accept arguments `-S`, `-U`, `-P`, `-l`, `-r`, and `-t` for comparison execution.
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
