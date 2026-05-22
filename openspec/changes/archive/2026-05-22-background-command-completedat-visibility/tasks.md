## 1. Command model and data retrieval

- [x] 1.1 Extend command catalog entry/model to include `completedAt` and preserve existing fields.
- [x] 1.2 Update SQL Server command catalog query and mapping to populate `completedAt` values.
- [x] 1.3 Add or update model-level tests for null/blank/populated `completedAt` mapping behavior.

## 2. Command-grid presentation updates

- [x] 2.1 Add `completedAt` column to command grid immediately after `timestamp` and update column-order assertions.
- [x] 2.2 Implement replay-state display mapping so `UNDEFINED` renders as `BGRND:PEND` when `completedAt` is empty and `BGRND:DONE` when populated.
- [x] 2.3 Verify non-`UNDEFINED` replay states (`OK`, `PENDING`, `FAILED`) render unchanged.

## 3. Regression coverage

- [x] 3.1 Update UI/component tests for command-grid row rendering with background pending and done states.
- [x] 3.2 Run targeted `cfct-webapp` command-selection tests and confirm no regressions in filtering, ordering, and selection behavior.
