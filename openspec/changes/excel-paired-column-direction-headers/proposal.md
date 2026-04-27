## Why

The Excel detail worksheet currently labels every paired column as `<column> (left)` and `<column> (right)`, which is visually noisy and makes scan-reading harder.
A compact two-row header with a shared column name and directional markers will improve readability while preserving left/right semantics.

## What Changes

- Update Excel detail sheet header layout to use two header rows for paired columns.
- Render the top header row with each logical column name spanning its two side-by-side cells.
- Render the second header row with `<<<` and `>>>` markers for left and right directionality.
- Keep existing comparison data, colouring, freeze panes, and deterministic worksheet structure unchanged.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `excel-comparison-output`: Detail-sheet header requirements are updated from single-row `(left)/(right)` labels to grouped two-row headers with directional sublabels.

## Impact

- Affects `ExcelMultiTableComparisonReportRenderer` header-writing logic and related tests.
- Affects workbook layout assertions in renderer and integration tests that inspect header cells.
- No API or request-format changes.
