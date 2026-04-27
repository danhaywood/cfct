## Why

JSON comparison output currently has less inspection detail than the Excel workbook, making it harder to review comparison results outside Excel or use JSON as a complete machine-readable report.
Approval-style output dumps will make it easy to inspect the rendered JSON and Excel outputs during test development and regression review.

## What Changes

- Expand JSON comparison output so it carries comparable summary, metadata, and row-level detail to the Excel output.
- Preserve deterministic JSON ordering and stable field names for approval review and downstream consumers.
- Add an approval-style test that writes inspectable output files for the JSON and Excel renderers.
- Ensure the dumped approval files are suitable for manual inspection without requiring the normal test assertions to parse binary Excel content directly.

## Capabilities

### New Capabilities
- `comparison-output-approval-files`: Approval-style test output files for inspecting rendered comparison artifacts.

### Modified Capabilities
- `json-comparison-file`: JSON comparison output includes Excel-equivalent comparison summary, table metadata, difference counts, and row-level detail.

## Impact

- Affects JSON rendering for configured comparison results.
- Affects test fixtures or integration tests that validate configured JSON and Excel output.
- May require updating approved JSON output expectations.
- Does not change comparison input file syntax or supported output type names.
