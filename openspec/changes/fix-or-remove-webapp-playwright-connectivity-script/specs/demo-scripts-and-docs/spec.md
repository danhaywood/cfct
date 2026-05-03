## MODIFIED Requirements

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
