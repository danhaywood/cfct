## Context

The configured comparison flow loads a JSON request, compares the selected tables, and renders the structured multi-table result as JSON.
The original `ComparisonOutputType` enum only accepted `json`, and `ConfiguredComparisonService` injected only `JsonMultiTableComparisonReportRenderer`.
Users need an interactive spreadsheet output that preserves deterministic comparison ordering while making summary navigation and table-level inspection easier.

The core comparison model already identified missing rows and differing rows.
Excel detail output also needs actual left/right values for missing and differing rows, so the structured result now retains those values while preserving existing JSON and text rendering behavior.

## Goals / Non-Goals

**Goals:**
- Support `excel` as a comparison request output type.
- Produce an Excel workbook with a first Table of Contents sheet.
- Produce one additional worksheet for each compared table in request/result order.
- Make the Table of Contents navigable with hyperlinks from the table name cells to the detail sheets.
- Freeze panes in the Table of Contents at `B2` and freeze appropriate key/header panes in each detail sheet.
- Render detail sheets as actual difference rows with paired left/right columns for each displayed table column.
- Colour-code detail rows and cells for manual inspection.
- Keep JSON output unchanged and deterministic.
- Make workbook output testable without requiring manual Excel inspection.
- Write sample workbooks under `target/excel-comparison-output/` during tests for optional manual inspection.

**Non-Goals:**
- Add a command-line interface or web endpoint.
- Change how tables are selected, ordered, or validated by the core comparer.
- Add styling-heavy spreadsheet features such as formulas, charts, filters, or cross-workbook links.
- Replace JSON output or change existing approved JSON structure.

## Decisions

### Use Apache POI for workbook creation

Add Apache POI as the Excel-writing dependency and implement an `ExcelMultiTableComparisonReportRenderer` in the report package.
Apache POI is a mature Java library for producing `.xlsx` workbooks and is suitable for structural unit tests that reopen generated workbooks.
The main alternative was generating CSV files, but CSV cannot represent multiple tabs, frozen panes, hyperlinks, or cell styles in one artifact.
Another alternative was hand-writing Office Open XML, but that would add unnecessary maintenance risk.

### Return workbook output as bytes from the Excel renderer

The Excel renderer exposes a `byte[] render(MultiTableComparisonResult result)` method because `.xlsx` is binary content.
`ConfiguredComparisonService` preserves the existing string-returning JSON methods and adds `compareOutput(...)` methods that return `ConfiguredComparisonOutput` with output type, media type, file extension, and bytes.
This keeps existing JSON callers working while giving Excel callers access to binary workbook bytes.

### Keep request parsing in the existing JSON request model

`ComparisonOutputType` includes `EXCEL("excel")` and parsing accepts all known values case-insensitively.
This keeps request file shape stable and limits request-file behavior changes to output validation and renderer dispatch.
Unsupported values continue to fail with the current clear validation error.

### Retain actual row values in the structured comparison result

`RowDifference` now retains left and right compared-column values for rows that exist on both sides and differ.
`TableComparisonResult` now retains compared-column values for rows only in left and rows only in right.
This avoids re-querying databases during rendering and keeps the Excel renderer a pure renderer over the comparison result.
Existing constructors remain available for tests and renderers that only need summary differences.

### Render a navigable Table of Contents

The workbook always creates the `Table of Contents` worksheet first.
The Table of Contents freezes panes at `B2`, so the table-name column and header row remain visible while scrolling.
Column A contains the combined table display name, such as `dbo.Supplier`, and that cell is a document hyperlink to the corresponding detail sheet.
Summary columns include compared-column count, ignored-column count, left-only count, right-only count, differing-row count, and whether the table has differences.
Table of Contents data rows are not colour-coded, to keep the summary visually neutral.

### Render detail sheets as paired left/right rows

Each table worksheet follows `MultiTableComparisonResult.tableResults()` order and uses a deterministic safe Excel sheet name.
The detail sheet begins with compact metadata rows for table identity, business-key index, business-key columns, compared columns, and ignored columns.
The difference table then uses a single row per result item rather than separate left and right rows.
Column A summarizes the result as `Only in left`, `Only in right`, or `Differ`.
Each business-key and compared column is rendered as two Excel columns, one for the left value and one for the right value.
For missing rows, only the side where the row exists is populated.
For differing rows, both sides are populated on the same row.
Detail sheets freeze panes after the result column plus paired key columns and below the header row.

### Use simple, meaningful detail colours

Only-in-left rows use light yellow.
Only-in-right rows use a darker yellow.
Matching cells in differing rows use green.
Differing value cells use pink/red.
Headers and metadata use neutral shaded styles.
This provides visual scanning support without relying on formulas or complex formatting.

### Test workbook structure and also write manual samples

Tests render workbooks, reopen them with POI, and assert sheet names, sheet order, hyperlinks, frozen panes, summary rows, representative detail cells, and styles.
This avoids brittle binary approval files while still verifying deterministic workbook content.
Tests also write sample workbooks to `target/excel-comparison-output/` so a developer can open the generated `.xlsx` files manually when desired.

## Risks / Trade-offs

- [Risk] Apache POI adds dependency weight to the application.
  → Mitigation: Depend only on the required POI OOXML artifact and keep the renderer isolated in the report package.
- [Risk] Excel sheet names have length and character restrictions.
  → Mitigation: Generate safe deterministic names from schema and table names, truncate as needed, and disambiguate duplicates with numeric suffixes.
- [Risk] The existing service API returns `String`, which does not fit binary workbook output.
  → Mitigation: Preserve existing JSON methods and add a binary output abstraction for Excel.
- [Risk] Retaining row values increases result object size.
  → Mitigation: Retain values only for rows with reportable differences, not for all matching rows.
- [Risk] Workbook formatting can become subjective and hard to test.
  → Mitigation: Use a small fixed set of styles and assert important structure and colours in tests.
