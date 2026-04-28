## Why

The current comparison presentation always shows paired `L:` and `R:` value columns even when both sides are equal, which adds visual noise and makes differences harder to scan.
A compact presentation that collapses equal values into one column will improve readability in both the web grid and exported spreadsheet while preserving full left/right detail when values differ.

## What Changes

- Update web comparison result tabs so equal values render as a single logical value column.
- Keep explicit paired `L:` and `R:` value presentation only for fields where left and right values differ.
- Update Excel detail-sheet rendering to apply the same compact rule for equal vs differing values.
- Preserve row classification and download workflows.
- Refresh README screenshots to demonstrate the new compact comparison presentation.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Modify result-grid column rendering so equal values are collapsed and differing values remain side-by-side.
- `excel-comparison-output`: Modify detail-sheet value rendering so equal values are shown once and differing values keep explicit left/right values.

## Impact

- Web grid rendering logic in the comparison results stage.
- Excel report formatting logic for detail sheets.
- Browser and renderer tests that assert comparison column structure and cell output.
- README visual documentation and screenshots for comparison results.
