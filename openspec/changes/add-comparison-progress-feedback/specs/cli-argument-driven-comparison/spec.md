## MODIFIED Requirements

### Requirement: CLI executes library comparison using provided server and database arguments
The CLI SHALL create SQL Server JDBC connections for left and right databases using `-S`, `-U`, `-P`, `-l`, and `-r`.
The CLI SHALL execute comparison through API service contracts using the parsed table list.
The CLI SHALL NOT directly construct or invoke non-configuration classes from `cfct-impl` for comparison orchestration.
The CLI SHALL register a comparison progress listener and print table-level progress updates as comparison execution advances.
The CLI SHALL emit deterministic comparison report output in the selected output format.
The CLI SHALL write successful output to stdout or the file selected by `-o` according to output destination rules.

#### Scenario: CLI runs comparison for left and right databases
- **WHEN** valid connection, table, output format, and output destination arguments are provided and the databases are reachable
- **THEN** the CLI executes comparison through API service contracts and writes deterministic report output to the selected destination

#### Scenario: CLI reports execution failures
- **WHEN** connection or comparison execution fails
- **THEN** the CLI exits with a non-zero status and writes a clear failure message to stderr

#### Scenario: CLI prints per-table progress during execution
- **WHEN** comparison starts for a multi-table request
- **THEN** the CLI prints progress lines that identify current table and completed-table count while execution is in progress
