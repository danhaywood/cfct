## 1. Add overflow-aware layout wrappers for comparison grids

- [x] 1.1 Locate the comparison results container and per-table grid construction in the webapp view layer.
- [x] 1.2 Introduce a dedicated wrapper/container for each result grid with semantic class names for overflow behavior.
- [x] 1.3 Configure horizontal overflow so wide grids can be scrolled without changing existing column semantics.
- [x] 1.4 Configure vertical overflow and bounded height so tall result sets can be scrolled within the comparison area.

## 2. Add and tune styling for scrollbar behavior

- [x] 2.1 Add or update webapp CSS rules for the grid wrapper to enforce `overflow-x` and `overflow-y` behavior.
- [x] 2.2 Ensure styling remains compatible with tabs, result actions, and existing cell-highlight cosmetics.
- [x] 2.3 Verify scrollbar behavior does not break compact rendering, text readability, or status indicators.

## 3. Add test coverage and validate regressions

- [x] 3.1 Add or update component tests to assert overflow classes/attributes are present on result-grid containers.
- [x] 3.2 Add or update UI tests for representative wide/tall datasets to verify horizontal and vertical navigation remains available.
- [x] 3.3 Run relevant webapp test suites and confirm no regressions in comparison tab behavior.
- [x] 3.4 Update developer-facing notes/comments describing comparison-grid overflow behavior expectations.
