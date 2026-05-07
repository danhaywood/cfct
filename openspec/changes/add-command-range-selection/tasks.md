## 1. Command-grid range-selection state

- [ ] 1.1 Add command-grid UI state for range anchor tracking based on the latest non-Shift selection intent.
- [ ] 1.2 Add helper logic to resolve inclusive visible-row intervals between anchor and target under active sorting and filtering.
- [ ] 1.3 Rebase or clear anchor when row visibility changes invalidate the current anchor.

## 2. Pointer and keyboard interaction handlers

- [ ] 2.1 Implement Shift-click handling to select contiguous visible-row ranges between anchor and clicked target.
- [ ] 2.2 Implement Shift+Space handling to select contiguous visible-row ranges between anchor and focused row.
- [ ] 2.3 Preserve existing single-row toggle and arrow-key navigation behavior for non-range interactions.

## 3. Selection orchestration and side effects

- [ ] 3.1 Batch selection-state updates so one interval operation triggers one downstream selected-command recomputation.
- [ ] 3.2 Ensure business-table auto-selection continues to consume the full selected command set after range operations.
- [ ] 3.3 Ensure command-selection parameter changes from range operations still clear stale comparison status reporting.

## 4. Verification and regression coverage

- [ ] 4.1 Add or update unit tests for anchor lifecycle and interval resolution across sorted and filtered grid states.
- [ ] 4.2 Add or update Playwright tests for Shift-click and Shift+Space contiguous range selection behavior.
- [ ] 4.3 Run targeted test suites and verify no regression in existing command-grid keyboard and selection workflows.
