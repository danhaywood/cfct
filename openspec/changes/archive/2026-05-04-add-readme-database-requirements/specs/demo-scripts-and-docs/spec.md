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
The README SHALL include a short database-requirements section for deployers that lists the required target-database system objects as schema-qualified names.
The README SHALL include expected structure for required system objects, including column names and SQL Server data types.
The README SHALL state that comparable business tables must provide a unique index or unique constraint whose name ends with `_PK`.
The README SHALL include a configuration reference section rendered in `application.yml` format for deployers.
The README SHALL include all supported `cfct.webapp.comparison.*` and `cfct.webapp.selection-plan.explicit.tables` properties in that YAML reference, with defaults or placeholders where appropriate.
The README SHALL include a property reference table that explains each supported property and its runtime purpose.

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

#### Scenario: README documents database prerequisites before execution
- **WHEN** a deployer reviews README setup guidance before running comparisons
- **THEN** the README lists `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping` as required target-database table-or-view objects
- **AND** the README states that compared business tables require a `_PK`-suffixed unique index or unique constraint

#### Scenario: README includes YAML configuration reference for deployers
- **WHEN** a deployer needs to prepare runtime configuration for webapp comparison execution
- **THEN** the README provides a copyable `application.yml`-formatted reference containing supported `cfct.webapp.comparison.*` properties and `cfct.webapp.selection-plan.explicit.tables`
- **AND** the reference is consistent with current application defaults and documented runtime options

#### Scenario: README documents required system object structure
- **WHEN** a deployer verifies target-database readiness
- **THEN** the README provides column-level structure for required system objects including SQL Server data types

#### Scenario: README explains each configuration property
- **WHEN** a deployer chooses runtime values for webapp configuration
- **THEN** the README includes a property table that explains defaults and purpose for each supported configuration key
