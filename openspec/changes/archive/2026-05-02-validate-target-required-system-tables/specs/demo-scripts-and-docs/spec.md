## MODIFIED Requirements

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

### Requirement: README documents current build, fixture, and CLI usage
The README SHALL document the current Maven module layout and build commands.
The README SHALL document how to start, check, and stop the fixture SQL Server using the fixture script.
The README SHALL document how to run the root `cfct.sh` comparison wrapper.
The README SHALL document that `cfct.sh` defaults to `.env` in the current directory and has no default tables file.
The README SHALL document fixture examples that pass `demo/.env` and `demo/tables.txt` explicitly.
The README SHALL document the direct CLI argument options, including `.env`, table-file, output-format, and output-file usage.
The README SHALL document `demo/.env` as the fixture example and `.env.TEMPLATE` as the user configuration template.
The README SHALL document how to run fixture invalid-target mode for manual negative-path login testing.
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
