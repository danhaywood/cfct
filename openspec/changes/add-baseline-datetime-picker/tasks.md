## 1. Baseline picker UI integration

- [ ] 1.1 Replace the command baseline text field with a date/time picker control in the command selection panel.
- [ ] 1.2 Preserve baseline field placement above the command grid and existing test-id hooks used by automation.
- [ ] 1.3 Ensure picker clear behavior maps to an unset baseline value.

## 2. Baseline value conversion and filtering behavior

- [ ] 2.1 Update baseline value conversion/parsing so picker values populate `commandBaselineFilterTimestamp` reliably.
- [ ] 2.2 Keep strict "command timestamp is after baseline" filtering semantics unchanged.
- [ ] 2.3 Keep context-menu baseline action populating the picker from selected command timestamp values.

## 3. Selection side effects and regression safety

- [ ] 3.1 Ensure baseline picker changes still clear stale comparison progress status reports.
- [ ] 3.2 Ensure command-grid filtering remains eager with no explicit apply action required.
- [ ] 3.3 Verify baseline picker changes do not alter command-driven table auto-selection behavior.

## 4. Verification coverage

- [ ] 4.1 Update unit tests in `MainViewTest` for baseline picker render, set/clear behavior, and command filtering outcomes.
- [ ] 4.2 Update Playwright coverage to exercise baseline picker interactions and filtered command-grid outcomes.
- [ ] 4.3 Run targeted webapp and page-object test suites and confirm no regressions in command-grid interaction flows.
