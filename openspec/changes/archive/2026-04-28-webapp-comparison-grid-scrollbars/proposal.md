## Why

Comparison pages should remain responsive as viewport size changes.
When comparison content is wider or taller than the available area, widgets such as grids must stay within their container and remain navigable.

## What Changes

- Make the comparison results area responsive so grid widgets fit within container bounds at smaller viewport sizes.
- Provide horizontal and vertical scrolling within the grid/results area only when content exceeds available space.
- Keep existing tab, column, and highlighting behavior unchanged while improving overflow handling.
- Add or update UI tests to validate responsive overflow behavior for wide and tall grids.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Extend the per-table grid requirement so oversized grids remain navigable via appropriate scrollbars.

## Impact

Webapp result container and/or grid layout styling will be updated to enforce overflow behavior.
MainView layout logic and related UI tests (component and/or Playwright) will be updated.
No API, persistence, or comparison-domain behavior changes are expected.
