## MODIFIED Requirements

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
