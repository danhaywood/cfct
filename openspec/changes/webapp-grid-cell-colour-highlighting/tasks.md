## 1. Add cell highlight semantics to webapp grid rendering

- [ ] 1.1 Locate the comparison tab/grid renderer code that builds shared and paired value columns for table results.
- [ ] 1.2 Define deterministic semantic CSS class names for difference cells and missing-side cells.
- [ ] 1.3 Apply class assignment in cell renderers using existing row-side and per-field difference metadata without changing shared domain contracts.
- [ ] 1.4 Ensure equal-value cells remain unhighlighted and existing row-level classification behavior is preserved.

## 2. Add theme styles and ensure visual consistency

- [ ] 2.1 Add or update webapp theme CSS to style the new semantic cell classes with Excel-like highlight colours.
- [ ] 2.2 Scope selectors so style specificity works reliably within Vaadin Grid cells across result tabs.
- [ ] 2.3 Verify highlight styling does not degrade readability of text, row ordering, or existing status cues.

## 3. Add test coverage and validate behavior

- [ ] 3.1 Add or update UI/component tests to assert deterministic class presence for differing-value cells.
- [ ] 3.2 Add or update UI/component tests to assert deterministic class presence for left-only and right-only missing cells.
- [ ] 3.3 Run relevant webapp and integration test suites and confirm no regressions in comparison tab behavior.
- [ ] 3.4 Update any developer-facing notes/comments that describe webapp comparison-grid visual semantics.
