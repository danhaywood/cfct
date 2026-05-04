## MODIFIED Requirements

### Requirement: Repository provides demo CLI input files
The repository SHALL provide a committed example environment file at `demo/.env` containing fixture-only connection settings aligned with Spring datasource keys and left/right database values.
The repository SHALL provide a committed demo tables flat file with one `schema.table` reference per line.
The repository SHALL provide a root `.env.TEMPLATE` that documents the supported dotenv keys for user-managed environments.
The demo files SHALL be safe examples and SHALL not contain production credentials.
The documented dotenv keys for CLI and wrapper usage SHALL use `CFCT_` prefixes.

#### Scenario: Demo environment file maps to CLI dotenv keys
- **WHEN** the demo environment file is inspected
- **THEN** it contains `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_DRIVER_CLASS_NAME`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `CFCT_LEFT_DATABASE`, and `CFCT_RIGHT_DATABASE`

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
- **THEN** the README shows the supported CLI options for JDBC URL/driver, username, password, left database, right database, table list, table file, environment file, output format, and output file

#### Scenario: README documents invalid-target fixture mode
- **WHEN** a user needs to manually verify target-database validation failures
- **THEN** the README shows the fixture-script invalid-target flag and how to use its reported target database name in webapp login inputs

#### Scenario: README webapp Playwright command is runnable
- **WHEN** a user follows the README command for webapp Playwright connectivity testing
- **THEN** the command resolves and starts execution without referencing a missing or broken script path
