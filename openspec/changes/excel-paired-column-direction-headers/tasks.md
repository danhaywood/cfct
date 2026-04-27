## 1. Excel Detail Header Layout

- [ ] 1.1 Update `ExcelMultiTableComparisonReportRenderer` to render two header rows for the detail table region.
- [ ] 1.2 Render `Result` as a merged header cell spanning the two header rows.
- [ ] 1.3 Render each business-key and compared column name once in the top header row across the paired cells.
- [ ] 1.4 Render `<<<` and `>>>` in the second header row for every paired column.
- [ ] 1.5 Keep paired data-column ordering, row writing, and styles unchanged apart from new header-row offsets.
- [ ] 1.6 Update freeze-pane setup so panes are frozen below the second header row and after paired business-key columns.

## 2. Tests

- [ ] 2.1 Update `ExcelMultiTableComparisonReportRendererTest` assertions for the new two-row grouped header layout.
- [ ] 2.2 Add assertions validating merged header regions for `Result` and grouped paired columns.
- [ ] 2.3 Update any integration test assertions that depend on old header row indexes or old `(left)/(right)` labels.

## 3. Validation

- [ ] 3.1 Run renderer unit tests covering Excel output and verify they pass.
- [ ] 3.2 Run configured comparison integration tests that inspect generated workbook contents.
- [ ] 3.3 Run full project verification and fix any regressions.
