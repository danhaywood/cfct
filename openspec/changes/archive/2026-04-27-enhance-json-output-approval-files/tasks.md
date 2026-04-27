## 1. JSON Report Model

- [x] 1.1 Inspect current comparison model records and renderer tests to identify available row value data for missing and differing rows.
- [x] 1.2 Extend `JsonMultiTableComparisonReportRenderer` to emit a per-table `summary` object with compared column count, ignored column count, rows-only-in-left count, rows-only-in-right count, differing row count, and table difference status.
- [x] 1.3 Extend rows-only-in-left JSON entries to include row key and left-side values for business-key and compared columns.
- [x] 1.4 Extend rows-only-in-right JSON entries to include row key and right-side values for business-key and compared columns.
- [x] 1.5 Extend differing-row JSON entries to include row key, left-side values, right-side values, and changed column details.
- [x] 1.6 Preserve deterministic field order, table order, row order, and column order in all generated JSON structures.

## 2. Tests and Approval Artifacts

- [x] 2.1 Update existing JSON approval expectations to match the detailed JSON structure.
- [x] 2.2 Add or update assertions that verify JSON output includes summary counts and side-by-side row values for missing and differing rows.
- [x] 2.3 Add an integration test that writes the representative detailed JSON output to a deterministic `target` approval-artifact path.
- [x] 2.4 Add an integration test step that writes the matching Excel workbook output to a deterministic `target` approval-artifact path.
- [x] 2.5 Ensure the JSON and Excel approval artifacts are produced from the same comparison fixture.

## 3. Validation

- [x] 3.1 Run the relevant unit and integration tests for JSON rendering and configured comparison output.
- [x] 3.2 Open or inspect the generated JSON and Excel approval artifacts to confirm they contain comparable report detail.
- [x] 3.3 Run the full Maven verification command expected for the repository and address any regressions.
