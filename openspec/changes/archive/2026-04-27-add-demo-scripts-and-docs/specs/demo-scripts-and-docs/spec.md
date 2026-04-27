## ADDED Requirements

### Requirement: Repository provides a CLI demo wrapper script
The repository SHALL provide a shell script that invokes the SQL comparer CLI using demo input files by default.
The wrapper SHALL build or locate the CLI executable artifact before invoking it.
The wrapper SHALL use the demo `.env` file through the CLI environment-file option.
The wrapper SHALL use the demo tables file through the CLI table-file option.
The wrapper SHALL allow callers to pass additional CLI arguments without editing the script.

#### Scenario: Demo wrapper runs with default inputs
- **WHEN** the fixture SQL Server is running and the user runs the CLI demo wrapper with no custom options
- **THEN** the wrapper invokes the CLI with the demo `.env` file and demo tables file

#### Scenario: Demo wrapper accepts additional arguments
- **WHEN** the user runs the CLI demo wrapper with additional CLI arguments
- **THEN** the wrapper passes those arguments through to the CLI invocation

### Requirement: Repository provides a fixture SQL Server lifecycle script
The repository SHALL provide a shell script that can start, stop, and report status for a local fixture SQL Server container.
The script SHALL initialize separate left and right databases when starting the fixture.
The script SHALL load fixture schema and data into those databases for the demo tables.
The script SHALL use deterministic connection values that match the committed demo `.env` file.
The script SHALL make the fixture container removable without requiring Maven test execution.

#### Scenario: Fixture script starts initialized SQL Server
- **WHEN** the user runs the fixture script start command in a Docker-enabled environment
- **THEN** the script starts SQL Server, creates left and right databases, and initializes demo fixture data

#### Scenario: Fixture script reports status
- **WHEN** the user runs the fixture script status command
- **THEN** the script reports whether the fixture SQL Server container is running and which host port it exposes

#### Scenario: Fixture script stops SQL Server
- **WHEN** the user runs the fixture script stop command after the fixture has been started
- **THEN** the script stops and removes the fixture SQL Server container

### Requirement: Repository provides demo CLI input files
The repository SHALL provide a committed demo environment file containing fixture-only connection settings for `-S`, `-U`, `-P`, `-l`, and `-r`.
The repository SHALL provide a committed demo tables flat file with one `schema.table` reference per line.
The demo files SHALL be safe examples and SHALL not contain production credentials.

#### Scenario: Demo environment file maps to CLI dotenv keys
- **WHEN** the demo environment file is inspected
- **THEN** it contains the CLI-supported dotenv keys for server, username, password, left database, and right database

#### Scenario: Demo tables file contains one table per line
- **WHEN** the demo tables file is inspected
- **THEN** each non-empty line contains a single `schema.table` table reference

### Requirement: README documents current build, fixture, and CLI usage
The README SHALL document the current Maven module layout and build commands.
The README SHALL document how to start, check, and stop the fixture SQL Server using the fixture script.
The README SHALL document how to run the CLI demo wrapper.
The README SHALL document the direct CLI argument options, including `.env` and table-file usage.
The README SHALL remove or correct information that no longer matches current project behavior.

#### Scenario: README explains the demo path
- **WHEN** a user reads the README from a clean checkout
- **THEN** the README gives an ordered path for starting the fixture and running the CLI demo

#### Scenario: README documents direct CLI invocation
- **WHEN** a user wants to bypass the wrapper script
- **THEN** the README shows the supported CLI options for server, username, password, left database, right database, table list, table file, and environment file
