## Why

The main comparison UI has usability and visual hierarchy issues that slow users down.
The Compare action placement, footer/status formatting, and table column presentation need refinement to improve clarity and efficiency.

## What Changes

- Move the Compare button into the navigation menu area, positioned above the table and aligned to the right.
- Refine the footer/status bar by removing redundant labels, adding spacing, and right-aligning the `Status: OK` indicator.
- Improve table grid presentation by auto-sizing the schema column, center-aligning the select column, and removing `Select` header text.
- Refresh UI screenshots to reflect the updated layout and add a screenshot with the navigation bar collapsed.
- Capture and verify the collapsed-navigation visual state in screenshots without introducing new persistent navigation labels.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `vaadin-webapp-configuration`: Update page-level layout and positioning rules for compare actions and status presentation.
- `webapp-main-ui-layout`: Update top-level UI composition behavior for toolbar/menu placement and removal of redundant navbar labeling.
- `webapp-manual-table-selection`: Update grid column behavior for schema auto-sizing and select-column header/alignment.
- `webapp-playwright-connectivity-status`: Refresh screenshot and visual-regression expectations for expanded and collapsed navigation states.

## Impact

The primary impact is in Vaadin webapp layout and view components, including top navigation composition and footer/status rendering.
Table grid column configuration and associated tests/snapshots will need updates.
Playwright screenshot assets and expected baseline outputs will be updated to match the new UX behavior.
