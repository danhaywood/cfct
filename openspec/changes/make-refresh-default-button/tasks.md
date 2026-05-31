## 1. Default Action Behavior Update

- [x] 1.1 Locate the selection workflow view logic that marks the default action and switch the default designation from Compare to Refresh.
- [x] 1.2 Update Enter key handling to invoke the Refresh action path when Refresh is enabled.
- [x] 1.3 Preserve explicit Compare button activation behavior so Compare runs only on direct Compare activation.

## 2. Guardrails and Regression Coverage

- [x] 2.1 Ensure Enter activation respects disabled-state guards and performs no action when Refresh is disabled.
- [x] 2.2 Update or add automated UI tests for Enter-triggered Refresh, disabled Refresh no-op, and explicit Compare execution.
- [x] 2.3 Run relevant test suites and confirm no regressions in command selection and comparison execution behavior.
