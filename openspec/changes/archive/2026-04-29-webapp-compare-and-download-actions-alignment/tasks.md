## 1. Align compare action in selection drawer

- [x] 1.1 Locate the selection-stage action row that contains the `Compare` button in the navigation drawer.
- [x] 1.2 Add semantic container/class styling so the compare action row has top spacing aligned with the hamburger/navbar height rhythm.
- [x] 1.3 Preserve right alignment and enable/disable behavior of the `Compare` button while applying the new spacing.

## 2. Re-group results-stage actions

- [x] 2.1 Refactor the comparison-stage action area into two rows: a top export-actions row and a separate filter/content-controls row.
- [x] 2.2 Move JSON/Excel download controls into the top row and right-align that row.
- [x] 2.3 Keep compared-table filter in the separate row above tab/grid exploration content and preserve existing filtering behavior.

## 3. Validate layout behavior and update tests/docs

- [x] 3.1 Add or update component/UI tests to assert compare-row spacing class presence and right alignment.
- [x] 3.2 Add or update component/UI tests to assert download controls are rendered above filter/grid and right-aligned.
- [x] 3.3 Run relevant webapp test suites and confirm no regression in compare, filter, or download behavior.
- [x] 3.4 Update developer-facing notes/comments describing results-stage action grouping semantics.
