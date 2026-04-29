## Why

Action controls in the webapp comparison flow feel visually misaligned and semantically grouped in ways that make the UI harder to scan.
Aligning the Compare button with top navigation height and separating global download actions from table filtering improves clarity without changing behavior.

## What Changes

- Add top spacing for the left-drawer Compare action so it aligns visually with the top application bar and hamburger-row height.
- Reposition download actions in the right comparison stage so they are shown above filter/grid content and right-aligned as global actions.
- Keep compared-table filter near the compared-table content area, distinct from global output actions.
- Preserve existing compare execution, tab rendering, and download functionality while improving layout and visual hierarchy.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-main-ui-layout`: Refine selection-stage action layout to align Compare action spacing with top navigation structure.
- `webapp-comparison-results-tabs`: Refine results-stage action layout so global download controls are separated from table-level filtering and right-aligned.

## Impact

MainView layout structure and related CSS/theme classes will be updated.
Webapp UI tests for control placement and visibility will need updates.
No comparison logic, API contracts, or data model changes are expected.
