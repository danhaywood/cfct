## 1. Output Type and Service API

- [ ] 1.1 Extend `ComparisonOutputType` to include `excel` parsing while preserving existing `json` parsing and unsupported-value errors.
- [ ] 1.2 Add unit tests that accept `json` and `excel`, reject blank output types, and reject unsupported output types.
- [ ] 1.3 Introduce a binary-capable configured comparison output path or rendered-output abstraction while preserving existing JSON string behavior.
- [ ] 1.4 Update `ConfiguredComparisonService` to dispatch JSON requests to the JSON renderer and Excel requests to the Excel renderer.

## 2. Excel Rendering

- [ ] 2.1 Add the minimal Apache POI dependency needed to create and read `.xlsx` workbooks.
- [ ] 2.2 Implement `ExcelMultiTableComparisonReportRenderer` that returns valid workbook bytes for a `MultiTableComparisonResult`.
- [ ] 2.3 Create the first `Table of Contents` sheet with one summary row per compared table.
- [ ] 2.4 Add difference-count and has-differences values to each Table of Contents summary row.
- [ ] 2.5 Create one detail sheet per table result in result order after the Table of Contents sheet.
- [ ] 2.6 Populate each detail sheet with table identity, business-key metadata, compared columns, ignored columns, left-only rows, right-only rows, and differing rows.
- [ ] 2.7 Implement deterministic safe Excel sheet naming, including invalid-character replacement, length limiting, and duplicate disambiguation.

## 3. Request Fixtures and Integration Coverage

- [ ] 3.1 Add a comparison request fixture that specifies output type `excel` and selected tables.
- [ ] 3.2 Add renderer tests that reopen workbook bytes with POI and assert sheet names, sheet order, summary rows, and representative detail cells.
- [ ] 3.3 Add configured comparison coverage proving an `excel` request returns valid workbook output.
- [ ] 3.4 Keep the existing configured JSON comparison integration test and approved JSON output unchanged.

## 4. Validation

- [ ] 4.1 Run `mvn test` and fix any unit-test failures.
- [ ] 4.2 Run `mvn verify` in a Docker-enabled environment and fix any integration-test failures.
- [ ] 4.3 Run OpenSpec validation for `add-excel-output-format` and fix any proposal, spec, or task issues.
