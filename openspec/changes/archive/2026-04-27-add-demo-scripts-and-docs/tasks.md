## 1. Demo Inputs

- [x] 1.1 Create a repository-level `demo/` directory for committed demo input files.
- [x] 1.2 Add a fixture-only demo env file with `SQLCOMPARER_SERVER`, `SQLCOMPARER_USERNAME`, `SQLCOMPARER_PASSWORD`, `SQLCOMPARER_LEFT_DATABASE`, and `SQLCOMPARER_RIGHT_DATABASE`.
- [x] 1.3 Add a demo tables flat file with one `schema.table` entry per line for fixture tables.

## 2. Fixture SQL Server Script

- [x] 2.1 Create a repository-level fixture script that supports `start`, `stop`, and `status` commands.
- [x] 2.2 Implement `start` to launch a deterministic SQL Server 2022 Docker container with configurable host port and fixture credentials.
- [x] 2.3 Implement readiness polling and clear failure messages for SQL Server startup.
- [x] 2.4 Implement database creation for the left and right demo databases.
- [x] 2.5 Reuse existing SQL fixture resources to load schema and left/right data for the demo tables.
- [x] 2.6 Implement `status` to show whether the fixture container is running and which port is exposed.
- [x] 2.7 Implement `stop` to remove the fixture container idempotently.

## 3. CLI Wrapper Script

- [x] 3.1 Create a repository-level CLI demo wrapper script.
- [x] 3.2 Implement CLI jar build or discovery before invocation.
- [x] 3.3 Invoke the CLI with the committed demo env file and demo tables file by default.
- [x] 3.4 Pass through additional caller arguments to the CLI invocation.
- [x] 3.5 Ensure scripts use strict shell options and clear usage messages.

## 4. README Updates

- [x] 4.1 Update README project layout and build/test instructions to match the current modules and workflows.
- [x] 4.2 Document prerequisites for Maven, Java, Docker, and SQL Server container startup.
- [x] 4.3 Document the fixture lifecycle script commands.
- [x] 4.4 Document the CLI demo wrapper flow.
- [x] 4.5 Document direct CLI invocation, including `-S`, `-U`, `-P`, `-l`, `-r`, `-t`, `--tables-file`, and `--env-file`.
- [x] 4.6 Clearly label demo credentials as fixture-only and not production-safe.

## 5. Validation

- [x] 5.1 Run shell syntax checks for added scripts where available.
- [x] 5.2 Run the relevant Maven tests or at least compile/test the CLI module after documentation/script additions.
- [x] 5.3 Manually review README commands against the script names and demo file paths.
