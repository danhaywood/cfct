## Why

The project exposes comparison functionality as a library, but there is no practical command-line workflow to run comparisons directly against SQL Server databases.
A first-pass CLI with explicit arguments for server, credentials, databases, and table selection enables immediate operational use and testable end-to-end behavior.

## What Changes

- Add a runnable CLI command path in `cfct-cli` that invokes the comparison library using command-line arguments.
- Support first-pass connection and scope flags:
  - `-S <server>`
  - `-U <username>`
  - `-P <password>`
  - `-l <leftDatabase>`
  - `-r <rightDatabase>`
  - `-t <schema.table,schema.table,...>`
- Parse `-t` as a comma-separated ordered list of fully-qualified table references.
- Execute a multi-table comparison and print deterministic report output to stdout.
- Add CLI-focused tests for argument parsing/validation and command execution behavior.

## Capabilities

### New Capabilities
- `cli-argument-driven-comparison`: Invoke multi-table comparison from CLI using explicit SQL Server and table-selection arguments.

### Modified Capabilities
- None.

## Impact

- Affects `cfct-cli` application startup and argument handling.
- Likely introduces JDBC connection creation and SQL Server driver usage in CLI runtime path.
- Adds new CLI tests and may add test seams/mocks for comparison service invocation.
- No changes to existing comparison library request/result semantics.
