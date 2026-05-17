## 1. Refresh control placement and UI wiring

- [ ] 1.1 Add a Refresh button to the command selection controls row that includes the baseline controls.
- [ ] 1.2 Position the Refresh button to the left of the two baseline fields in the rendered layout.
- [ ] 1.3 Ensure responsive drawer layouts keep Refresh and baseline controls visible and usable.

## 2. Command-list reload behavior

- [ ] 2.1 Wire Refresh click handling to reload command rows from the database-backed command source.
- [ ] 2.2 Rebind command-grid data to refreshed rows without requiring full page reload.
- [ ] 2.3 Prevent duplicate refresh actions while a refresh request is already in progress.

## 3. State reconciliation after refresh

- [ ] 3.1 Preserve active filter input values and reapply them after command-list refresh.
- [ ] 3.2 Reconcile selected command rows by identity against refreshed rows and remove stale selections.
- [ ] 3.3 Keep downstream business-table auto-selection and compare readiness consistent after selection reconciliation.

## 4. Verification

- [ ] 4.1 Add or update unit/component tests for refresh control placement and reload invocation.
- [ ] 4.2 Add or update Playwright coverage for manual refresh showing newly added commands.
- [ ] 4.3 Run targeted webapp tests to verify no regression in baseline controls and command filtering flows.
