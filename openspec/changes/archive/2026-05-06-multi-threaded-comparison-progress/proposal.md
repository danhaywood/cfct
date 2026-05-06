## Why

Current multi-table compare execution and progress feedback are perceived as opaque and slow when many tables are selected.
Running table comparisons concurrently while surfacing per-row completion cues and a live completed counter improves perceived responsiveness and operator confidence.

## What Changes

- Execute selected table comparisons concurrently using a bounded multi-threaded strategy instead of single-threaded sequencing.
- Emit and consume completion progress updates as each table finishes, independent of original selection order.
- Update the business table grid so each completed table row gets a completion background cue during the active run.
- Show a live completed counter next to the Compare action (for example, `2 of 5`) while a run is active.
- Clear row completion cues and the live counter when the user presses Clear.
- Clear row completion cues and the live counter when a new selection workflow begins before or after a prior run.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `core-multi-table-comparison`: Change multi-table execution requirements to support bounded parallel execution while preserving deterministic result assembly.
- `comparison-progress-notifications`: Change progress event semantics so completion updates reflect asynchronous table completion rather than strict request-order completion.
- `webapp-manual-table-selection`: Add requirements for row-level completion highlighting and compare-adjacent completion counters, including reset behavior on clear and selection changes.

## Impact

Core comparison orchestration in the library module will change to use an executor-based concurrent strategy with safe aggregation.
Progress event contracts and related tests in API and integration layers will be updated for completion-order notifications.
Webapp selection and compare UI state management will be extended to track per-table completion and live counters, plus reset hooks for clear and selection changes.
No external API endpoint changes are expected, but observable progress timing and ordering behavior will change for consumers that register listeners.
