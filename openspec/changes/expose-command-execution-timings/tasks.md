## 1. Timing API Model

- [x] 1.1 Add API model types for per-side foreground and background execution timing observations.
- [x] 1.2 Add nullable duration semantics and deterministic observation ordering helpers.
- [x] 1.3 Add a timing-reader SPI that accepts the selected foreground and each side's terminal descendant scope.

## 2. SQL Server Timing Retrieval

- [x] 2.1 Implement SQL Server foreground timing reads from `CommandLogEntry` without selecting target data.
- [x] 2.2 Implement independent app-a and app-b terminal-background timing reads using side-local identifiers.
- [x] 2.3 Calculate duration from `startedAt` and `completedAt` and preserve unavailable duration for missing or invalid intervals.
- [x] 2.4 Add unit tests for valid, missing, invalid, failed, and deterministically ordered observations.

## 3. Automation Integration

- [x] 3.1 Compose execution timing from the same selected foreground and descendant scopes used by settled automation comparison.
- [x] 3.2 Add the independent top-level `executionTiming` object to successful automation JSON responses.
- [x] 3.3 Preserve execution timing for no-business-footprint and startup-refresh responses without changing existing outcomes.
- [x] 3.4 Add automation service and controller tests for object shape, side independence, target omission, empty background arrays, and outcome independence.

## 4. Integration Verification and Documentation

- [x] 4.1 Extend the SQL Server integration fixture and tests to cover foreground, completed-background, and failed-background timing observations.
- [x] 4.2 Add deterministic JSON coverage proving repeated responses order observations consistently.
- [x] 4.3 Document the execution timing payload, duration semantics, privacy constraints, and downstream compatibility in the README.
- [x] 4.4 Run the full CFCT verification suite and resolve regressions.
