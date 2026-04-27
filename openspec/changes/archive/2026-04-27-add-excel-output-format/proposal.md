## Why

Comparison results currently have deterministic JSON output, which is useful for machines and approval tests but less convenient for users who want to inspect differences interactively.
Excel output gives users a familiar workbook format with navigable summary and detailed table sheets.

## What Changes

- Add Excel as a supported comparison output format alongside JSON.
- Render comparison results as an Excel workbook with one worksheet per compared table.
- Include a first Table of Contents worksheet that summarizes the comparison output and provides hyperlinks to each table detail sheet.
- Freeze panes in the Table of Contents and detail sheets so summary columns and headers remain visible during manual inspection.
- Render each detail sheet as actual differing rows, with each table column represented by paired left/right Excel columns.
- Colour-code detail cells so left-only rows, right-only rows, matching cells, and differing cells are visually distinct.
- Keep JSON output behavior unchanged.
- Reject unsupported output types with the existing clear validation behavior.

## Capabilities

### New Capabilities
- `excel-comparison-output`: Defines deterministic Excel workbook output for comparison results, including a first Table of Contents sheet, hyperlinks to table tabs, and one detail tab for each compared table.

### Modified Capabilities
- `json-comparison-file`: Allow comparison request files to select either `json` or `excel` as the output type instead of accepting only `json`.

## Impact

- Affects comparison request validation and output type modeling.
- Adds an Excel workbook renderer for structured multi-table comparison results.
- Extends structured comparison results to retain actual left/right row values needed by Excel detail output.
- Adds Apache POI for `.xlsx` workbook creation and structural workbook tests.
- Adds tests and generated target workbooks for manual inspection of workbook layout and colour coding.
