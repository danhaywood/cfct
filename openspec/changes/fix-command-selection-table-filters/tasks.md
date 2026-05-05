## 1. Command-to-business-table visibility synchronization

- [x] 1.1 Update command-selection to business-table recomputation flow so selection and `Selected only` visibility projection occur in one deterministic state update.
- [x] 1.2 Add or update UI/integration tests that verify initial command selection shows selected business rows immediately while `Selected only` is checked.
- [x] 1.3 Add a regression test that proves users do not need to toggle `Selected only` after command selection to reveal selected rows.

## 2. Results controls simplification

- [x] 2.1 Remove `Show MATCH rows` control rendering and related view-model state wiring from the comparison results controls.
- [x] 2.2 Keep MATCH rows excluded in result-grid predicates and adjust tests to assert control absence plus default hidden MATCH rows.

## 3. Differences-only default behavior

- [x] 3.1 Change results-stage initialization so `Differences only` defaults to unchecked.
- [x] 3.2 Add or update tests to verify unchanged compared tables remain visible on first render and are hidden only after enabling `Differences only`.

## 4. Documentation and validation

- [x] 4.1 Update user-facing docs or screenshots that reference removed `Show MATCH rows` behavior.
- [x] 4.2 Run webapp and integration test suites covering command selection and results filters, then fix any regressions.
