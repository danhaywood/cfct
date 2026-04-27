## 1. Demo Inputs

- [ ] 1.1 Create a repository-level `demo/` directory for committed demo input files.
- [ ] 1.2 Add a fixture-only demo env file with `SQLCOMPARER_SERVER`, `SQLCOMPARER_USERNAME`, `SQLCOMPARER_PASSWORD`, `SQLCOMPARER_LEFT_DATABASE`, and `SQLCOMPARER_RIGHT_DATABASE`.
- [ ] 1.3 Add a demo tables flat file with one `schema.table` entry per line for fixture tables.

## 2. Fixture SQL Server Script

- [ ] 2.1 Create a repository-level fixture script that supports `start`, `stop`, and `status` commands.
- [ ] 2.2 Implement `start` to launch a deterministic SQL Server 2022 Docker container with configurable host port and fixture credentials.
- [ ] 2.3 Implement readiness polling and clear failure messages for SQL Server startup.
- [ ] 2.4 Implement database creation for the left and right demo databases.
- [ ] 2.5 Reuse existing SQL fixture resources to load schema and left/right data for the demo tables.
- [ ] 2.6 Implement `status` to show whether the fixture container is running and which port is exposed.
- [ ] 2.7 Implement `stop` to remove the fixture container idempotently.

## 3. CLI Wrapper Script

- [ ] 3.1 Create a repository-level CLI demo wrapper script.
- [ ] 3.2 Implement CLI jar build or discovery before invocation.
- [ ] 3.3 Invoke the CLI with the committed demo env file and demo tables file by default.
- [ ] 3.4 Pass through additional caller arguments to the CLI invocation.
- [ ] 3.5 Ensure scripts use strict shell options and clear usage messages.

## 4. README Updates

- [ ] 4.1 Update README project layout and build/test instructions to match the current modules and workflows.
- [ ] 4.2 Document prerequisites for Maven, Java, Docker, and SQL Server container startup.
- [ ] 4.3 Document the fixture lifecycle script commands.
- [ ] 4.4 Document the CLI demo wrapper flow.
- [ ] 4.5 Document direct CLI invocation, including `-S`, `-U`, `-P`, `-l`, `-r`, `-t`, `--tables-file`, and `--env-file`.
- [ ] 4.6 Clearly label demo credentials as fixture-only and not production-safe.

## 5. Validation

- [ ] 5.1 Run shell syntax checks for added scripts where available.
- [ ] 5.2 Run the relevant Maven tests or at least compile/test the CLI module after documentation/script additions.
- [ ] 5.3 Manually review README commands against the script names and demo file paths.
