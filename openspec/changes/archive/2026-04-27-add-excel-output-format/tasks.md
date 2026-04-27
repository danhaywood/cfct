## 1. Output Type and Service API

- [x] 1.1 Extend `ComparisonOutputType` to include `excel` parsing while preserving existing `json` parsing and unsupported-value errors.
- [x] 1.2 Add unit tests that accept `json` and `excel`, reject blank output types, and reject unsupported output types.
- [x] 1.3 Introduce `ConfiguredComparisonOutput` as a binary-capable configured comparison output abstraction while preserving existing JSON string behavior.
- [x] 1.4 Update `ConfiguredComparisonService` to dispatch JSON requests to the JSON renderer and Excel requests to the Excel renderer.

## 2. Excel Rendering

- [x] 2.1 Add the minimal Apache POI dependency needed to create and read `.xlsx` workbooks.
- [x] 2.2 Implement `ExcelMultiTableComparisonReportRenderer` that returns valid workbook bytes for a `MultiTableComparisonResult`.
- [x] 2.3 Create the first `Table of Contents` sheet with one summary row per compared table.
- [x] 2.4 Add difference-count and has-differences values to each Table of Contents summary row.
- [x] 2.5 Create one detail sheet per table result in result order after the Table of Contents sheet.
- [x] 2.6 Populate each detail sheet with table identity, business-key metadata, compared columns, ignored columns, left-only rows, right-only rows, and differing rows.
- [x] 2.7 Implement deterministic safe Excel sheet naming, including invalid-character replacement, length limiting, and duplicate disambiguation.
- [x] 2.8 Add document hyperlinks from Table of Contents table-name cells to the corresponding detail sheets.
- [x] 2.9 Freeze the Table of Contents sheet at `B2`.
- [x] 2.10 Extend comparison result objects to retain actual left/right values for reportable rows.
- [x] 2.11 Render detail differences as one row per result item with paired left/right columns for each displayed table column.
- [x] 2.12 Freeze detail sheets below the difference-table header and after the result plus paired business-key columns.
- [x] 2.13 Apply detail colour coding: light yellow for only-in-left rows, darker yellow for only-in-right rows, green for matching cells, and pink/red for differing cells.

## 3. Request Fixtures and Integration Coverage

- [x] 3.1 Add a comparison request fixture that specifies output type `excel` and selected tables.
- [x] 3.2 Add renderer tests that reopen workbook bytes with POI and assert sheet names, sheet order, summary rows, hyperlinks, frozen panes, representative detail cells, and styles.
- [x] 3.3 Add configured comparison coverage proving an `excel` request returns valid workbook output.
- [x] 3.4 Keep the existing configured JSON comparison integration test and approved JSON output unchanged.
- [x] 3.5 Write generated sample workbooks under `target/excel-comparison-output/` for optional manual inspection.

## 4. Validation

- [x] 4.1 Run `mvn test` and fix any unit-test failures.
- [x] 4.2 Run `mvn verify` in a Docker-enabled environment and fix any integration-test failures.
- [x] 4.3 Run OpenSpec validation for `add-excel-output-format` and fix any proposal, spec, or task issues.
