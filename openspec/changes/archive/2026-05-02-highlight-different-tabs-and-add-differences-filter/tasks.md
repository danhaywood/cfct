## 1. Result Tab Difference Styling

- [x] 1.1 Derive per-table `hasDifferences` state from comparison row statuses in webapp result-model mapping.
- [x] 1.2 Add deterministic tab styling hooks/classes for changed versus unchanged tables.
- [x] 1.3 Apply visual color treatment only to tabs marked as changed.

## 2. Differences-Only Result Filter

- [x] 2.1 Add a `Differences only` checkbox to comparison result controls.
- [x] 2.2 Compose differences-only filtering with existing compared-table text filtering.
- [x] 2.3 Ensure filter toggle updates visible tabs and selected-tab content deterministically.

## 3. Fixture Extension

- [x] 3.1 Extend SQL fixture setup with `dbo.CustomerAddress` table and `_PK` index in both logical databases.
- [x] 3.2 Seed identical left/right `CustomerAddress` rows to produce no differences.
- [x] 3.3 Ensure command/table mapping and selection paths can include `CustomerAddress` in compare runs.

## 4. Automated Test Coverage

- [x] 4.1 Add or update unit tests for per-table difference detection and tab styling hooks.
- [x] 4.2 Add or update unit tests for differences-only checkbox filtering behavior.
- [x] 4.3 Add or update Playwright tests asserting changed-tab coloring and unchanged-tab suppression when differences-only is enabled.

## 5. Verification and Documentation

- [x] 5.1 Run relevant webapp unit tests and Playwright browser tests for result-tab behaviors.
- [x] 5.2 Update README or user-facing notes describing changed-tab coloring and the differences-only filter.
