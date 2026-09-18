## Why

Regression runs need trustworthy command-duration facts from both compared applications so performance movement can be analysed alongside, but independently from, business-table and audit differences.
`CommandLogEntry` already records realistic `startedAt` and `completedAt` values for foreground commands and their background descendants, but CFCT does not expose those values to automation clients.

## What Changes

- Read foreground and terminal background command timing observations independently from each database.
- Derive each duration from `completedAt - startedAt` rather than from nominal command timestamps or suite wall-clock span.
- Expose an additive `executionTiming` object in successful automation comparison JSON.
- Include side, foreground/background scope, logical member identifier, start time, completion time, duration, status, and side-local interaction identifiers needed for deduplication.
- Omit target bookmarks and target object identifiers from execution-timing output.
- Keep app-a and app-b background observations independent without correlating their interaction or transaction identifiers.
- Report timing facts only; do not classify performance movement as pass, warning, or failure and do not alter business or audit comparison outcomes.

## Capabilities

### New Capabilities
- `command-execution-timing`: Read and represent per-side foreground and terminal-background command execution timing observations without target bookmarks or cross-side background identifier correlation.

### Modified Capabilities
- `webapp-automation-rest-api`: Add independent command execution timing facts to settled automation comparison responses while preserving existing response and outcome semantics.

## Impact

- Adds timing model types and a command-log timing reader SPI to `cfct-api`.
- Adds SQL Server timing retrieval and duration calculation to `cfct-impl`.
- Extends settled automation comparison JSON in `cfct-webapp` additively.
- Adds unit and integration coverage for foreground timing, background timing, absent timestamps, side independence, ordering, and target omission.
- Enables Aregress to persist and report performance observations without granting it direct database access.
