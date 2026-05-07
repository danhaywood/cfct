## 1. Baseline picker UI integration

- [x] 1.1 Replace the command baseline text field with a date/time picker control in the command selection panel.
- [x] 1.2 Preserve baseline field placement above the command grid and existing test-id hooks used by automation.
- [x] 1.3 Ensure picker clear behavior maps to an unset baseline value.

## 2. Baseline value conversion and filtering behavior

- [x] 2.1 Update baseline value conversion/parsing so picker values populate `commandBaselineFilterTimestamp` reliably.
- [x] 2.2 Keep strict "command timestamp is after baseline" filtering semantics unchanged.
- [x] 2.3 Keep context-menu baseline action populating the picker from selected command timestamp values.

## 3. Selection side effects and regression safety

- [x] 3.1 Ensure baseline picker changes still clear stale comparison progress status reports.
- [x] 3.2 Ensure command-grid filtering remains eager with no explicit apply action required.
- [x] 3.3 Verify baseline picker changes do not alter command-driven table auto-selection behavior.

## 4. Verification coverage

- [x] 4.1 Update unit tests in `MainViewTest` for baseline picker render, set/clear behavior, and command filtering outcomes.
- [x] 4.2 Update Playwright coverage to exercise baseline picker interactions and filtered command-grid outcomes.
- [x] 4.3 Run targeted webapp and page-object test suites and confirm no regressions in command-grid interaction flows.
