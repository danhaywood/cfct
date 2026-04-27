## 1. Inspect Current Webapp UI and Tests

- [ ] 1.1 Review `MainView`, selection state classes, webapp configuration properties, and existing UI tests to identify current component boundaries.
- [ ] 1.2 Review existing Playwright success and failure tests to identify reusable fixtures, selectors, and happy-path data.

## 2. Main Layout and Footer

- [ ] 2.1 Add a reusable main UI shell or equivalent Vaadin layout structure with a hamburger menu affordance and deterministic accessible label.
- [ ] 2.2 Render the existing home page content inside the main UI shell without regressing connectivity status or table-selection visibility.
- [ ] 2.3 Add a footer that displays configured server, left database, and right database values from typed webapp configuration.
- [ ] 2.4 Ensure the footer omits or masks sensitive credential values, including the configured password.
- [ ] 2.5 Add stable component IDs, labels, or test hooks for the hamburger menu and footer assertions.

## 3. Vaadin Grid Table Selection

- [ ] 3.1 Replace the left-hand checkbox list with a Vaadin Grid backed by the existing discovered table catalog.
- [ ] 3.2 Add table-identity columns and per-row selection controls while preserving eligibility and disabled styling for ineligible rows.
- [ ] 3.3 Add sorting for visible table-identity columns in the Grid.
- [ ] 3.4 Add explicit filtering controls for visible table-identity values in the Grid.
- [ ] 3.5 Preserve selected-table state and selected-count feedback when users select, deselect, sort, or filter rows.

## 4. Placeholder Compare Action

- [ ] 4.1 Add a `Compare` button in the left-hand table-selection area.
- [ ] 4.2 Disable the `Compare` button when no eligible tables are selected.
- [ ] 4.3 Enable the `Compare` button when at least one eligible table is selected.
- [ ] 4.4 Ensure activating the enabled `Compare` button does not invoke comparison execution.

## 5. Unit and Component Tests

- [ ] 5.1 Add or update unit tests for selection state and selected-count behavior after Grid-backed selection changes.
- [ ] 5.2 Add or update unit tests for `Compare` button enablement logic.
- [ ] 5.3 Add or update UI/component tests for footer connection details and password exclusion where practical.
- [ ] 5.4 Add or update tests for Grid filtering and sorting logic where it can be isolated from browser execution.

## 6. Playwright Happy-Path Coverage

- [ ] 6.1 Extend the successful connectivity Playwright test to assert the hamburger menu is visible.
- [ ] 6.2 Extend the successful connectivity Playwright test to assert footer connection details are visible and sensitive credential values are absent.
- [ ] 6.3 Extend the successful connectivity Playwright test to filter the table-selection Grid and assert the visible result set narrows.
- [ ] 6.4 Extend the successful connectivity Playwright test to sort the table-selection Grid and assert deterministic visible row ordering.
- [ ] 6.5 Extend the successful connectivity Playwright test to select an eligible table and assert the `Compare` button becomes enabled.

## 7. Validation

- [ ] 7.1 Run focused webapp unit tests for UI, selection, configuration, and comparison execution boundaries.
- [ ] 7.2 Run the headless Playwright happy-path test for the updated main UI.
- [ ] 7.3 Run the broader webapp test suite or documented Maven verification command to catch regressions.
- [ ] 7.4 Run OpenSpec validation for `improve-main-ui` and fix any proposal, spec, or task issues.
