## Context

The comparison library currently runs multi-table work as a black-box operation from the perspective of consumers.
The CLI and Vaadin webapp only receive output after execution completes.
Users need progress visibility during longer runs, especially when many tables are selected.
The change spans core library API contracts, CLI wiring, and webapp UI behavior.
Backward compatibility is required so existing callers that do not register listeners continue to work unchanged.

## Goals / Non-Goals

**Goals:**
- Introduce a library-level progress listener callback that reports table-level lifecycle events during multi-table comparison.
- Keep the listener optional and non-breaking for existing consumers.
- Update the CLI to print deterministic progress lines as table comparisons start and complete.
- Update the webapp to display live progress text in the existing status bar/footer area while comparison runs.

**Non-Goals:**
- Real-time row-level progress percentages are out of scope.
- Parallel table comparison execution changes are out of scope.
- New transport protocols for server push beyond standard Vaadin UI access patterns are out of scope.

## Decisions

The library will add a progress listener interface in the API-service-contracts module so callbacks are part of public contracts.
The listener will receive immutable progress event payloads containing table reference, phase, processed count, total count, and optional message.
The multi-table orchestrator will emit events in deterministic order aligned with request table order.
The listener will be optional in comparison request options, and no-op behavior will apply when absent.
The CLI will register a listener that prints concise progress lines to stderr so stdout remains reserved for primary comparison output artifacts.
The Vaadin webapp will register a listener for the active comparison invocation and marshal updates to the UI thread using Vaadin-safe access methods.
The webapp status bar will show a running summary such as current table, completed count, total count, and terminal completion or failure state.

## Risks / Trade-offs

[Progress callback overhead] → Keep events coarse at table lifecycle boundaries and avoid row-level chatter.
[Output stream mixing in CLI] → Emit progress to stderr and preserve deterministic main output on stdout or file.
[Vaadin threading issues] → Use UI access guards and session-safe update patterns for listener callbacks.
[API surface growth] → Add optional fields and constructors carefully with defaults to keep compatibility.

## Migration Plan

Add the new listener interface and event model to API contracts with default or optional wiring.
Update core multi-table comparison orchestration to emit events at start, completion, and failure boundaries per table.
Update CLI command execution wiring to register a listener and print progress lines.
Update webapp comparison trigger flow to register a listener and update status bar text during execution.
Add unit and integration coverage for event emission order and consumer presentation behavior.
Ship with release notes that describe optional listener adoption for external callers.

## Open Questions

Should progress events include elapsed duration per table in the first version.
Should the webapp show a transient progress bar component in addition to status text.
