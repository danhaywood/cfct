## 1. Inspect Current Webapp UI and Tests

- [x] 1.1 Review `MainView`, selection state classes, webapp configuration properties, and existing UI tests to identify current component boundaries.
- [x] 1.2 Review existing Playwright success and failure tests to identify reusable fixtures, selectors, and happy-path data.

## 2. Main Layout and Footer

- [x] 2.1 Add a reusable main UI shell or equivalent Vaadin layout structure with a hamburger menu affordance and deterministic accessible label.
- [x] 2.2 Render the existing home page content inside the main UI shell without regressing connectivity status or table-selection visibility.
- [x] 2.3 Add a footer that displays configured server, left database, and right database values from typed webapp configuration.
- [x] 2.4 Ensure the footer omits or masks sensitive credential values, including the configured password.
- [x] 2.5 Add stable component IDs, labels, or test hooks for the hamburger menu and footer assertions.

## 3. Vaadin Grid Table Selection

- [x] 3.1 Replace the left-hand checkbox list with a Vaadin Grid backed by the existing discovered table catalog.
- [x] 3.2 Add table-identity columns and per-row selection controls while preserving eligibility and disabled styling for ineligible rows.
- [x] 3.3 Add sorting for visible table-identity columns in the Grid.
- [x] 3.4 Add explicit filtering controls for visible table-identity values in the Grid.
- [x] 3.5 Preserve selected-table state and selected-count feedback when users select, deselect, sort, or filter rows.

## 4. Placeholder Compare Action

- [x] 4.1 Add a `Compare` button in the left-hand table-selection area.
- [x] 4.2 Disable the `Compare` button when no eligible tables are selected.
- [x] 4.3 Enable the `Compare` button when at least one eligible table is selected.
- [x] 4.4 Ensure activating the enabled `Compare` button does not invoke comparison execution.

## 5. Unit and Component Tests

- [x] 5.1 Add or update unit tests for selection state and selected-count behavior after Grid-backed selection changes.
- [x] 5.2 Add or update unit tests for `Compare` button enablement logic.
- [x] 5.3 Add or update UI/component tests for footer connection details and password exclusion where practical.
- [x] 5.4 Add or update tests for Grid filtering and sorting logic where it can be isolated from browser execution.

## 6. Playwright Happy-Path Coverage

- [x] 6.1 Extend the successful connectivity Playwright test to assert the hamburger menu is visible.
- [x] 6.2 Extend the successful connectivity Playwright test to assert footer connection details are visible and sensitive credential values are absent.
- [x] 6.3 Extend the successful connectivity Playwright test to filter the table-selection Grid and assert the visible result set narrows.
- [x] 6.4 Extend the successful connectivity Playwright test to sort the table-selection Grid and assert deterministic visible row ordering.
- [x] 6.5 Extend the successful connectivity Playwright test to select an eligible table and assert the `Compare` button becomes enabled.

## 7. Validation

- [x] 7.1 Run focused webapp unit tests for UI, selection, configuration, and comparison execution boundaries.
- [x] 7.2 Run the headless Playwright happy-path test for the updated main UI.
- [x] 7.3 Run the broader webapp test suite or documented Maven verification command to catch regressions.
- [x] 7.4 Run OpenSpec validation for `improve-main-ui` and fix any proposal, spec, or task issues.

## 8. Requested UI Refinements

- [x] 8.1 Remove the apply-filter button and keep filtering immediate from the table filter field.
- [x] 8.2 Remove the eligibility column and keep ineligible rows non-selectable with tooltip text.
- [x] 8.3 Move SQL connectivity status into the footer with connection details.
- [x] 8.4 Convert the shell to Vaadin AppLayout and move table selection into the navigation area.
- [x] 8.5 Add spacing around layout, footer, drawer, and comparison widgets.

## 9. Follow-up Layout and Documentation Refinements

- [x] 9.1 Remove the `Select tables` label and selected-table count from the navigation drawer.
- [x] 9.2 Move the `Compare` button into the right-hand comparison area and align it to the right.
- [x] 9.3 Reintroduce the footer/status bar so connection URL, database names, and connectivity status are visible outside the empty comparison body.
- [x] 9.4 Extend the local fixture data with an ineligible demo table.
- [x] 9.5 Capture app screenshots and reference them from the README.
