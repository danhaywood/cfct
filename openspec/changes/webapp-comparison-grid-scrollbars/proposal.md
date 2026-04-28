## Why

Wide comparison tables and large result sets can exceed the visible results area in the webapp, making some columns or rows hard to access.
Adding explicit horizontal and vertical scrolling behavior improves usability without changing comparison semantics.

## What Changes

- Ensure the comparison results region supports horizontal scrolling when the grid has more columns than available width.
- Ensure the comparison results region supports vertical scrolling when row count exceeds available height.
- Keep existing tab, column, and highlighting behavior unchanged while improving overflow handling.
- Add or update UI tests to validate scrollbar/overflow behavior for wide and tall grids.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Extend the per-table grid requirement so oversized grids remain navigable via appropriate scrollbars.

## Impact

Webapp result container and/or grid layout styling will be updated to enforce overflow behavior.
MainView layout logic and related UI tests (component and/or Playwright) will be updated.
No API, persistence, or comparison-domain behavior changes are expected.
