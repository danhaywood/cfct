## 1. Business table selection view-model updates

- [x] 1.1 Add `selectedOnly` state to the manual business table selection model and initialize it to `true`.
- [x] 1.2 Implement selected-state visibility predicate so the business table grid shows only selected rows when `selectedOnly` is enabled.
- [x] 1.3 Ensure toggling `selectedOnly` changes visibility only and does not mutate the underlying selected row set.

## 2. Business table grid UI wiring

- [x] 2.1 Add a `Selected only` checkbox control in the business table selection section near the grid filters.
- [x] 2.2 Bind the checkbox to the `selectedOnly` state with live grid refresh on toggle.
- [x] 2.3 Preserve existing table-identity filters so they combine correctly with the selected-only filter.

## 3. Command-driven selection integration

- [x] 3.1 Keep command-driven touched-table union updates flowing into the same selection state while selected-only filtering is enabled.
- [x] 3.2 Ensure newly selected rows become visible under selected-only mode and deselected rows are hidden after recomputation.
- [x] 3.3 Verify clear/reset behavior restores expected defaults, including selected-only checked by default for a fresh selection flow.

## 4. Verification and regression coverage

- [x] 4.1 Add or update unit/component tests for selected-only default state, toggle behavior, and non-destructive selection preservation.
- [x] 4.2 Add or update integration/UI tests for command-first workflow, including unchecking selected-only to manually include additional tables.
- [x] 4.3 Run targeted webapp test suite and fix regressions in manual table selection and compare-readiness behavior.
