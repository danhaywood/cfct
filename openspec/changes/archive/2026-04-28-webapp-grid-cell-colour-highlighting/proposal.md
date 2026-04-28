## Why

The webapp comparison grid already separates equal and differing values, but users still need to scan cell-by-cell to spot which values are missing or different.
Adding Excel-like cell colour cues will make differences and side-only gaps immediately visible and improve readability for larger tables.

## What Changes

- Add conditional cell styling in the webapp comparison grid to colour cells that represent differing values, left-only values, and right-only values.
- Keep existing column structure and row classification behavior while enhancing visual cues at cell level.
- Ensure styling is deterministic and testable so UI behavior remains stable.
- Extend UI tests to verify that value-difference and missing-value cells receive the expected highlight classes.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Extend the Excel-like grid requirement to include cell-level colour highlighting for missing or differing values.

## Impact

Webapp result-grid rendering logic and CSS theme styles will be updated.
Playwright and/or component-level UI tests for comparison tabs will need updates to assert cell highlight class behavior.
No API contract, persistence, or dependency changes are expected.
