## Why

The current webapp home page exposes useful connectivity and table-selection state, but the layout is still basic and does not provide enough navigation, configuration context, or table browsing affordances for day-to-day use.
This change improves the UI structure and makes table selection easier to scan, sort, filter, and test before comparison execution is implemented.

## What Changes

- Add a hamburger menu to the webapp shell so future navigation actions have an obvious location.
- Move configured connection details and SQL connectivity status into a fixed footer/status bar that is visible from the main UI without taking focus from table selection.
- Replace the table-selection list with a Vaadin Grid in the AppLayout navigation drawer that supports sorting and immediate filtering.
- Preserve manual row selection behavior for eligible tables while hiding the selected-table count from the UI.
- Add a `Compare` button in the main comparison area, aligned right, that is enabled when at least one eligible table is selected.
- Leave the `Compare` button as a non-executing placeholder for this change.
- Add unit tests where UI state can be validated without a browser.
- Add Playwright happy-path coverage for the new menu, footer/status bar, grid sorting and filtering, selection state, and `Compare` button enablement.

## Capabilities

### New Capabilities
- `webapp-main-ui-layout`: Main webapp AppLayout shell, navigation-drawer table-selection grid behavior, footer/status bar connection details, and placeholder compare action.

### Modified Capabilities
- `webapp-manual-table-selection`: Manual table selection now requires a sortable and filterable Vaadin Grid in the navigation drawer, without a visible selected-table count.
- `vaadin-webapp-configuration`: Configured connection details and SQL connectivity status are now required to be surfaced in the main UI footer/status bar.
- `webapp-playwright-connectivity-status`: Browser coverage is extended to verify the happy path for the updated main UI interactions.

## Impact

- Affects the `sqlcomparer-webapp` Vaadin views, components, styling, and test support.
- May add or update webapp unit tests for selection state, footer content, and button enablement.
- Extends Playwright tests and related helper selectors for deterministic happy-path UI assertions.
- Does not change comparison execution behavior, API contracts, database comparison logic, or CLI behavior.
