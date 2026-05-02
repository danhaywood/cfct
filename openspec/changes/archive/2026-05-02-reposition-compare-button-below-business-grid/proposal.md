## Why

The compare action currently sits above the business table grid in the left navigation panel.
On smaller viewports this placement can reduce usability and make the action less discoverable after table selection.
The layout also needs clear visual separation between the command grid and business table grid.

## What Changes

Move the compare button to below the business table grid in the left navigation panel.
Keep the compare action visible when the browser window is resized and the drawer content scrolls.
Add explicit spacer separation between the command selection area and the business table selection area.
Retain existing compare enablement logic and selection semantics.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-main-ui-layout`: Update left navigation composition and responsive behavior for compare action placement and visibility.

## Impact

This change affects `MainView` layout composition and related UI tests.
Browser-level assertions may need updates for compare button position and visibility under resized viewport conditions.
