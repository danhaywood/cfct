## 1. Results controls updates

- [x] 1.1 Rename the existing `Differences only` checkbox label to `Diff tables only` in the results controls and related UI text assertions.
- [x] 1.2 Add a new `Diff columns only` checkbox to results controls with deterministic placement near table-filter controls.
- [x] 1.3 Implement enable/disable rules so `Diff columns only` is clickable only when the active table tab has differences.

## 2. Column filtering behavior

- [x] 2.1 Implement active-tab column-difference derivation that identifies logical fields with at least one differing value.
- [x] 2.2 Apply column projection when `Diff columns only` is checked while preserving existing deterministic column ordering.
- [x] 2.3 Ensure unchecking `Diff columns only` restores the full standard field set for the active table.

## 3. Regression coverage

- [x] 3.1 Add or update UI/component tests for renamed `Diff tables only` control behavior and table-tab filtering.
- [x] 3.2 Add or update tests for `Diff columns only` enablement, disablement, and visible-column filtering outcomes.
- [x] 3.3 Run relevant webapp and Playwright test suites covering results controls and comparison-grid rendering.
