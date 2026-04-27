## Context

The configured comparison flow loads a JSON request, compares the selected tables, and renders the structured multi-table result as JSON.
The current `ComparisonOutputType` enum only accepts `json`, and `ConfiguredComparisonService` injects only `JsonMultiTableComparisonReportRenderer`.
Users now need an interactive spreadsheet output that preserves deterministic comparison ordering while making summary and per-table inspection easier.

The core comparison model already contains the information needed for a workbook: table identity, business-key metadata, compared and ignored columns, left-only rows, right-only rows, and differing rows.
The implementation should add rendering and dispatch behavior without changing core comparison semantics.

## Goals / Non-Goals

**Goals:**
- Support `excel` as a comparison request output type.
- Produce an Excel workbook with a first Table of Contents sheet.
- Produce one additional worksheet for each compared table in request/result order.
- Keep JSON output unchanged and deterministic.
- Make workbook output testable without requiring manual Excel inspection.

**Non-Goals:**
- Add a command-line interface or web endpoint.
- Change how tables are compared, ordered, or validated by the core comparer.
- Add styling-heavy spreadsheet features such as formulas, charts, filters, or hyperlinks unless needed for simple readability.
- Replace JSON output or change existing approved JSON structure.

## Decisions

### Use Apache POI for workbook creation

Add Apache POI as the Excel-writing dependency and implement an `ExcelMultiTableComparisonReportRenderer` in the report package.
Apache POI is a mature Java library for producing `.xlsx` workbooks and is suitable for structural unit tests that reopen generated workbooks.
The main alternative was generating CSV files, but CSV cannot represent multiple tabs or a Table of Contents sheet in one artifact.
Another alternative was hand-writing Office Open XML, but that would add unnecessary maintenance risk.

### Return workbook output as bytes from the Excel renderer

The Excel renderer should expose a `byte[] render(MultiTableComparisonResult result)` method because `.xlsx` is binary content.
`ConfiguredComparisonService` currently returns `String`, so implementation should either add a binary-capable method for workbook consumers or introduce a small rendered-output abstraction that carries output type, media type, file extension, and bytes.
The lower-risk path is to add a binary-capable method while preserving the existing string-returning methods for JSON behavior.

### Keep request parsing in the existing JSON request model

Extend `ComparisonOutputType` to include `EXCEL("excel")` and update parsing to accept both known values case-insensitively.
This keeps request file shape stable and limits behavior changes to output validation and renderer dispatch.
Unsupported values should continue to fail with the current clear validation error.

### Render deterministic sheet order and simple tables

The workbook should always create the Table of Contents worksheet first.
Each table worksheet should follow in `MultiTableComparisonResult.tableResults()` order.
The Table of Contents should include one row per compared table with schema, table name, compared-column count, ignored-column count, left-only count, right-only count, differing-row count, and whether the table has differences.
Each table worksheet should include compact sections for table metadata, business key, compared columns, ignored columns, rows only in left, rows only in right, and differing rows.
This mirrors the existing JSON report content without introducing new comparison data.

### Test workbook structure rather than binary bytes

Tests should render a workbook, reopen it with POI, and assert sheet names, sheet order, summary rows, and representative detail cells.
This avoids brittle binary approval files while still verifying deterministic workbook content.
Integration coverage should add an Excel comparison request fixture and assert the configured service dispatches to the Excel renderer.

## Risks / Trade-offs

- [Risk] Apache POI adds dependency weight to the application.
  → Mitigation: Depend only on the required POI OOXML artifact and keep the renderer isolated in the report package.
- [Risk] Excel sheet names have length and character restrictions.
  → Mitigation: Generate safe deterministic names from schema and table names, truncate as needed, and disambiguate duplicates with numeric suffixes.
- [Risk] The existing service API returns `String`, which does not fit binary workbook output.
  → Mitigation: Preserve existing JSON methods and add a binary output path or rendered-output abstraction for Excel.
- [Risk] Workbook formatting can become subjective and hard to test.
  → Mitigation: Start with simple header and data rows, and test structure and values rather than visual styling.
