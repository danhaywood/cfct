## Why

Users currently wait without feedback while multi-table comparisons run.
This makes long-running CLI and webapp comparisons feel stalled and reduces trust in the process.

## What Changes

- Add library-level progress notifications that are emitted as each table comparison starts and completes.
- Provide a callback/listener registration mechanism so consumers can observe progress events without coupling to output concerns.
- Update the CLI to register a progress callback and print progress as each table is compared.
- Update the Vaadin webapp to register a progress listener and surface live status updates in the UI status area during comparison execution.

## Capabilities

### New Capabilities
- `comparison-progress-notifications`: Defines progress event semantics, listener registration, and event delivery during multi-table comparison execution.

### Modified Capabilities
- `core-multi-table-comparison`: Extend comparison orchestration requirements to emit progress events for table-level work.
- `cli-argument-driven-comparison`: Require CLI output to show progress updates while comparisons are running.
- `webapp-main-ui-layout`: Require the webapp UI to show live comparison progress updates in the status bar area.

## Impact

This change affects the core comparison orchestration library API and execution flow.
This change affects the CLI presentation layer and command execution wiring.
This change affects Vaadin webapp execution wiring and status UI rendering behavior.
No external dependencies are expected, but public API additions must preserve backward compatibility for existing callers.
