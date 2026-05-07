## Why

The Excel detail sheet currently renders differing values inline as `L:... R:...`, which makes wide tables harder to scan and compare visually.
We want Excel output to return to a clearer two-column left/right presentation for columns that actually differ while keeping equal columns compact.

## What Changes

- Change Excel detail-row rendering so fields with at least one differing row in the table are shown as two directional columns in the worksheet.
- Keep fields whose values are equal across all reported rows as a single shared worksheet column.
- Keep primary-key/business-key fields as a single shared worksheet column even when directional labels are available elsewhere.
- Update Excel header labeling so directional columns use the legacy compact markers (`<<<` and `>>>`) instead of inline value prefixes.
- Remove inline `L:` / `R:` value prefixes from Excel cell values because direction is expressed by separate columns.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `excel-comparison-output`: Update detail-sheet column layout rules so directional left/right columns are emitted conditionally per logical field and key columns remain collapsed.

## Impact

The change affects Excel workbook rendering logic and related tests in the Excel output module.
JSON, YAML, CLI behavior, and webapp rendering remain unchanged.
