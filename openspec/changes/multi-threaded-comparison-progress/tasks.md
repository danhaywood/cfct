## 1. Core concurrent comparison orchestration

- [ ] 1.1 Introduce bounded parallel execution for multi-table comparison with configurable worker count and safe executor lifecycle management.
- [ ] 1.2 Refactor multi-table orchestration to collect per-table outcomes concurrently while assembling final result entries in original request order.
- [ ] 1.3 Update core unit and integration tests to verify bounded concurrency behavior and deterministic result ordering under out-of-order completion.

## 2. Progress event semantics

- [ ] 2.1 Update progress listener emission logic so start events remain request-ordered while completion and failure events publish on actual finish.
- [ ] 2.2 Ensure completion counters are monotonic and consistent across success and failure paths during concurrent execution.
- [ ] 2.3 Update listener-focused tests to assert asynchronous completion ordering tolerance and monotonic `completed of total` counts.

## 3. Webapp compare progress cues

- [ ] 3.1 Add ephemeral compare-run UI state to track per-table completion status and a compare-adjacent `completed of total` counter.
- [ ] 3.2 Render completion background styling for business table rows as table completion events arrive.
- [ ] 3.3 Render and update the live compare-adjacent counter during active runs and hide it when no run is active.

## 4. Visual-state reset behavior

- [ ] 4.1 Implement a shared reset path that clears row completion cues and counter state when the command Clear action is triggered.
- [ ] 4.2 Invoke the same reset path when business table selection or filter parameters change to prevent stale compare cues.
- [ ] 4.3 Add webapp tests for clear and reselection flows to verify prior run cues are fully removed before the next compare workflow.

## 5. End-to-end validation and docs

- [ ] 5.1 Add or update integration tests covering multi-threaded completion updates from comparison execution through UI-facing state updates.
- [ ] 5.2 Validate accessibility and theme compatibility of completion row background cues in the manual table grid.
- [ ] 5.3 Update relevant developer docs or release notes describing concurrent compare execution and live progress cue behavior.
