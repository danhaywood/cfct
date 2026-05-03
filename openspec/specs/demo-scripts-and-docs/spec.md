# demo-scripts-and-docs Specification

## Purpose
TBD - created by archiving change add-demo-scripts-and-docs. Update Purpose after archive.
## Requirements
### Requirement: Repository provides a CLI demo wrapper script
The repository SHALL provide a root-level shell script named `cfct.sh` that invokes the CFCT CLI using configurable env-file and table-file inputs.
The wrapper SHALL verify that the CLI executable artifact is present before invoking it.
The wrapper SHALL fail with a clear build instruction when required jar artifacts are missing.
The wrapper SHALL not automatically build or rebuild jar artifacts.
The wrapper SHALL use production-oriented variable names and environment overrides rather than `DEMO_`-prefixed names.
The wrapper SHALL use `.env` in the current working directory as its default env file.
The wrapper SHALL not define a default tables file.
The wrapper SHALL support table selection by pass-through CLI arguments or by `CFCT_TABLES_FILE`.
The wrapper SHALL allow callers to pass additional CLI arguments without editing the script.
The wrapper SHALL support env-file selection via `--env-file <path>`.
The wrapper SHALL support env-file selection via `CFCT_ENV_FILE`.
The wrapper SHALL prioritize `--env-file <path>` over `CFCT_ENV_FILE` when both are provided.

#### Scenario: Comparison wrapper runs with explicit fixture example inputs
- **WHEN** the fixture SQL Server is running, required jars are built, and the user runs `./cfct.sh` with `CFCT_ENV_FILE=demo/.env` and `--tables-file demo/tables.txt`
- **THEN** the wrapper invokes the CLI with `demo/.env` and `demo/tables.txt`

#### Scenario: Comparison wrapper accepts additional arguments
- **WHEN** the user runs `./cfct.sh` with additional CLI arguments
- **THEN** the wrapper passes those arguments through to the CLI invocation

#### Scenario: Comparison wrapper supports tables file environment override
- **WHEN** the user runs `./cfct.sh` with `CFCT_TABLES_FILE` set
- **THEN** the wrapper passes that file path to the CLI table-file option

#### Scenario: Comparison wrapper supports env-file argument override
- **WHEN** the user runs `./cfct.sh --env-file custom.env --tables-file demo/tables.txt`
- **THEN** the wrapper invokes the CLI using `custom.env` as the env file input

#### Scenario: Comparison wrapper prioritizes explicit env-file argument
- **WHEN** the user runs `./cfct.sh --env-file custom.env` with `CFCT_ENV_FILE=demo/.env`
- **THEN** the wrapper invokes the CLI using `custom.env` instead of `demo/.env`

#### Scenario: Comparison wrapper reports missing jar artifacts
- **WHEN** the user runs `./cfct.sh` before required jar artifacts have been built
- **THEN** the wrapper exits with a non-zero status and reports the Maven build command needed before retrying

### Requirement: Repository provides a fixture SQL Server lifecycle script
The repository SHALL provide a shell script that can start, stop, and report status for a local fixture SQL Server container.
The script SHALL initialize separate left and right databases when starting the fixture in normal mode.
The script SHALL load fixture schema and data into those databases for the demo tables.
The script SHALL use deterministic connection values that match the committed demo `.env` file.
The script SHALL make the fixture container removable without requiring Maven test execution.
The script SHALL provide an explicit flag that starts fixture SQL Server in an invalid-target-database mode for manual negative-path testing.
In invalid-target mode, the script SHALL keep source-database fixture setup valid while producing a deterministic non-existent target database name for manual login testing.

#### Scenario: Fixture script starts initialized SQL Server
- **WHEN** the user runs the fixture script start command in a Docker-enabled environment
- **THEN** the script starts SQL Server, creates left and right databases, and initializes demo fixture data

#### Scenario: Fixture script reports status
- **WHEN** the user runs the fixture script status command
- **THEN** the script reports whether the fixture SQL Server container is running and which host port it exposes

#### Scenario: Fixture script stops SQL Server
- **WHEN** the user runs the fixture script stop command after the fixture has been started
- **THEN** the script stops and removes the fixture SQL Server container

#### Scenario: Fixture script supports invalid-target mode
- **WHEN** the user starts the fixture script with the invalid-target flag
- **THEN** the script reports a deterministic non-existent target database name suitable for manual login validation failure checks

### Requirement: Repository provides demo CLI input files
The repository SHALL provide a committed example environment file at `demo/.env` containing fixture-only connection settings for `-S`, `-U`, `-P`, `-l`, and `-r`.
The repository SHALL provide a committed demo tables flat file with one `schema.table` reference per line.
The repository SHALL provide a root `.env.TEMPLATE` that documents the supported dotenv keys for user-managed environments.
The demo files SHALL be safe examples and SHALL not contain production credentials.
The documented dotenv keys for CLI and wrapper usage SHALL use `CFCT_` prefixes.

#### Scenario: Demo environment file maps to CLI dotenv keys
- **WHEN** the demo environment file is inspected
- **THEN** it contains the CLI-supported dotenv keys for server, username, password, left database, and right database using `CFCT_` prefixes

#### Scenario: Demo tables file contains one table per line
- **WHEN** the demo tables file is inspected
- **THEN** each non-empty line contains a single `schema.table` table reference

#### Scenario: Root env template documents user-managed configuration
- **WHEN** `.env.TEMPLATE` is inspected
- **THEN** it documents the CLI-supported dotenv keys without containing production credentials

### Requirement: README documents current build, fixture, and CLI usage
The README SHALL document the current Maven module layout and build commands.
The README SHALL document how to start, check, and stop the fixture SQL Server using the fixture script.
The README SHALL document how to run the root `cfct.sh` comparison wrapper.
The README SHALL document that `cfct.sh` defaults to `.env` in the current directory and has no default tables file.
The README SHALL document fixture examples that pass `demo/.env` and `demo/tables.txt` explicitly.
The README SHALL document the direct CLI argument options, including `.env`, table-file, output-format, and output-file usage.
The README SHALL document `demo/.env` as the fixture example and `.env.TEMPLATE` as the user configuration template.
The README SHALL document how to run fixture invalid-target mode for manual negative-path login testing.
The README SHALL document only executable commands for webapp Playwright connectivity testing and SHALL remove or replace stale script references.
The README SHALL remove or correct information that no longer matches current project behavior.

#### Scenario: README explains the comparison wrapper path
- **WHEN** a user reads the README from a clean checkout
- **THEN** the README gives an ordered path for building the CLI jar, starting the fixture, and running `./cfct.sh` with explicit fixture env and tables files

#### Scenario: README documents direct CLI invocation
- **WHEN** a user wants to bypass the wrapper script
- **THEN** the README shows the supported CLI options for server, username, password, left database, right database, table list, table file, environment file, output format, and output file

#### Scenario: README documents invalid-target fixture mode
- **WHEN** a user needs to manually verify target-database validation failures
- **THEN** the README shows the fixture-script invalid-target flag and how to use its reported target database name in webapp login inputs

#### Scenario: README webapp Playwright command is runnable
- **WHEN** a user follows the README command for webapp Playwright connectivity testing
- **THEN** the command resolves and starts execution without referencing a missing or broken script path

