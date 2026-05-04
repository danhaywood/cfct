## 1. Command-grid columns and default ordering

- [x] 1.1 Add `replayState` as a visible command-grid column.
- [x] 1.2 Reorder visible command identity columns to `replayState`, `member`, `timestamp`, `interactionId`.
- [x] 1.3 Set default command-grid ordering to timestamp ascending and keep ordering stable after data refresh.

## 2. Inline header filter controls

- [x] 2.1 Move command filtering controls into a command-grid header filter row.
- [x] 2.2 Add member and interactionId text filter inputs in the command-grid header row.
- [x] 2.3 Add replay-state checkbox and conditional dropdown (`PENDING`, `OK`, `FAILED`) in the command-grid header row.

## 3. Combined command filtering behavior

- [x] 3.1 Extend command filter predicate logic to include replay-state when enabled.
- [x] 3.2 Preserve existing member and interactionId filtering behavior when replay-state filtering is disabled.
- [x] 3.3 Verify combined filtering returns rows matching all active criteria without mutating command selection state.

## 4. Regression coverage and verification

- [x] 4.1 Update unit/component tests for command column order, default timestamp-ascending sorting, and inline header filter behavior.
- [x] 4.2 Update browser-level tests for inline command-grid filtering and replay-state filter flow.
- [x] 4.3 Run targeted webapp tests and resolve regressions related to command selection, sorting, and filtering.
