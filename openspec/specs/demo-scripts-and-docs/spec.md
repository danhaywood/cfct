# demo-scripts-and-docs Specification

## Purpose
TBD - created by archiving change add-demo-scripts-and-docs. Update Purpose after archive.
## Requirements
### Requirement: Repository provides a CLI demo wrapper script
The repository SHALL provide a root-level shell script named `comparedb.sh` that invokes the SQL comparer CLI using configurable env-file and table-file inputs.
The wrapper SHALL verify that the CLI executable artifact is present before invoking it.
The wrapper SHALL fail with a clear build instruction when required jar artifacts are missing.
The wrapper SHALL not automatically build or rebuild jar artifacts.
The wrapper SHALL use production-oriented variable names and environment overrides rather than `DEMO_`-prefixed names.
The wrapper SHALL support an example env file at `demo/.env` through the CLI environment-file option.
The wrapper SHALL use the example tables file through the CLI table-file option by default.
The wrapper SHALL allow callers to pass additional CLI arguments without editing the script.

#### Scenario: Comparison wrapper runs with default example inputs
- **WHEN** the fixture SQL Server is running, required jars are built, and the user runs `./comparedb.sh` with no custom options
- **THEN** the wrapper invokes the CLI with `demo/.env` and `demo/tables.txt`

#### Scenario: Comparison wrapper accepts additional arguments
- **WHEN** the user runs `./comparedb.sh` with additional CLI arguments
- **THEN** the wrapper passes those arguments through to the CLI invocation

#### Scenario: Comparison wrapper reports missing jar artifacts
- **WHEN** the user runs `./comparedb.sh` before required jar artifacts have been built
- **THEN** the wrapper exits with a non-zero status and reports the Maven build command needed before retrying

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
The repository SHALL provide a committed example environment file at `demo/.env` containing fixture-only connection settings for `-S`, `-U`, `-P`, `-l`, and `-r`.
The repository SHALL provide a committed demo tables flat file with one `schema.table` reference per line.
The repository SHALL provide a root `.env.TEMPLATE` that documents the supported dotenv keys for user-managed environments.
The demo files SHALL be safe examples and SHALL not contain production credentials.

#### Scenario: Demo environment file maps to CLI dotenv keys
- **WHEN** the demo environment file is inspected
- **THEN** it contains the CLI-supported dotenv keys for server, username, password, left database, and right database

#### Scenario: Demo tables file contains one table per line
- **WHEN** the demo tables file is inspected
- **THEN** each non-empty line contains a single `schema.table` table reference

#### Scenario: Root env template documents user-managed configuration
- **WHEN** `.env.TEMPLATE` is inspected
- **THEN** it documents the CLI-supported dotenv keys without containing production credentials

### Requirement: README documents current build, fixture, and CLI usage
The README SHALL document the current Maven module layout and build commands.
The README SHALL document how to start, check, and stop the fixture SQL Server using the fixture script.
The README SHALL document how to run the root `comparedb.sh` comparison wrapper.
The README SHALL document the direct CLI argument options, including `.env`, table-file, output-format, and output-file usage.
The README SHALL document `demo/.env` as the fixture example and `.env.TEMPLATE` as the user configuration template.
The README SHALL remove or correct information that no longer matches current project behavior.

#### Scenario: README explains the comparison wrapper path
- **WHEN** a user reads the README from a clean checkout
- **THEN** the README gives an ordered path for building the CLI jar, starting the fixture, and running `./comparedb.sh`

#### Scenario: README documents direct CLI invocation
- **WHEN** a user wants to bypass the wrapper script
- **THEN** the README shows the supported CLI options for server, username, password, left database, right database, table list, table file, environment file, output format, and output file
