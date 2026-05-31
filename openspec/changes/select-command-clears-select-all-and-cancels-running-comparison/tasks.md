## 1. Selection state orchestration

- [ ] 1.1 Identify and update the command-row selection handler so row selection always clears active `Select all` state first.
- [ ] 1.2 Ensure command-driven table auto-selection recomputes using only explicitly selected command rows after `Select all` is cleared.
- [ ] 1.3 Add guard logic so rapid repeated selection events do not reapply stale selection state.

## 2. Comparison cancellation integration

- [ ] 2.1 Detect active comparison execution during command row selection and trigger the existing user-initiated cancellation flow.
- [ ] 2.2 Wait for cancellation completion before applying the new command selection to compare eligibility state.
- [ ] 2.3 Clear transient comparison progress and stale status report state as part of reselection cancellation handling.

## 3. Verification and regression coverage

- [ ] 3.1 Add or update UI interaction tests for selecting a command after `Select all` is active.
- [ ] 3.2 Add or update execution-flow tests for selecting a command while comparison is in progress and asserting cancellation occurs first.
- [ ] 3.3 Run relevant module test suites and confirm no regressions in command selection and compare orchestration behavior.
