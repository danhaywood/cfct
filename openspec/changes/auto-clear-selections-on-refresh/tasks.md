## 1. Update refresh selection-reset behavior

- [ ] 1.1 Locate the command-grid Refresh action handler and document current reload + selection-state flow.
- [ ] 1.2 Implement refresh-triggered clearing of command-grid selection state and business-table selection state.
- [ ] 1.3 Ensure refresh-triggered state reset also clears any previously rendered comparison progress/status report.

## 2. Verify behavior and protect with tests

- [ ] 2.1 Add or update UI/component tests to assert Refresh clears command selections, table selections, and status report state.
- [ ] 2.2 Verify existing filter and reload behavior remains unchanged when Refresh is activated.
- [ ] 2.3 Run project checks and targeted tests for webapp command-selection behavior.
