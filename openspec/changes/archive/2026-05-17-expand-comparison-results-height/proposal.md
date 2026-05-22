## Why

The comparison results area on the right side does not currently use the full vertical space available in the page.
This leaves avoidable empty space above the fixed footer and forces unnecessary scrolling inside result tables.
Users need the results table to expand to the maximum usable height so more compared rows are visible at once.

## What Changes

- Adjust comparison-stage layout sizing so the results table consumes the full available vertical extent in the main content region.
- Preserve explicit bottom clearance so the fixed footer remains unobstructed and never overlaps result content.
- Keep existing results controls and tab behavior while improving usable grid viewport height.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Refine layout requirements so result-grid containers expand to available page height without encroaching on the fixed footer.

## Impact

This change affects right-side comparison-stage layout and grid container sizing in the webapp module.
This change affects UI and end-to-end tests that assert result-grid bounds and footer non-overlap behavior.
No API, persistence, or comparison-engine contract changes are expected.
