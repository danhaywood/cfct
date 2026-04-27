## Why

Comparison results currently have deterministic JSON output, which is useful for machines and approval tests but less convenient for users who want to inspect differences interactively.
Excel output gives users a familiar workbook format with separate sheets for summary and detailed table results.

## What Changes

- Add Excel as a supported comparison output format alongside JSON.
- Render comparison results as an Excel workbook with one worksheet per result sheet.
- Include a first Table of Contents worksheet that summarizes the comparison output and provides an overview of the workbook contents.
- Keep JSON output behavior unchanged.
- Reject unsupported output types with the existing clear validation behavior.

## Capabilities

### New Capabilities
- `excel-comparison-output`: Defines deterministic Excel workbook output for comparison results, including a first Table of Contents sheet and one tab for each result sheet.

### Modified Capabilities
- `json-comparison-file`: Allow comparison request files to select either `json` or `excel` as the output type instead of accepting only `json`.

## Impact

- Affects comparison request validation and output type modeling.
- Adds an Excel workbook renderer for structured multi-table comparison results.
- May add an Excel-writing dependency such as Apache POI.
- Adds approval or structural tests for workbook contents, sheet order, and summary data.
