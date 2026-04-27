## 1. Main UI layout refinements

- [ ] 1.1 Move the `Compare` button into the AppLayout navigation area and render it above the table-selection grid.
- [ ] 1.2 Right-align the navigation-area compare action row and preserve current enable/disable behavior based on eligible selections.
- [ ] 1.3 Add a collapsed-navigation affordance indicating hidden actions/content remain available when the drawer is collapsed.

## 2. Footer/status bar improvements

- [ ] 2.1 Refactor footer/status layout to remove redundant labels while retaining server and database context values.
- [ ] 2.2 Add spacing adjustments for cleaner footer readability and visual grouping.
- [ ] 2.3 Right-align the `Status: OK` or failure status text in the footer/status bar.
- [ ] 2.4 Verify credentials remain omitted or masked in the footer/status rendering.

## 3. Manual table grid presentation updates

- [ ] 3.1 Configure the schema column to auto-size based on visible content.
- [ ] 3.2 Center-align the select/checkbox column cells.
- [ ] 3.3 Remove `Select` header text from the select column while keeping selection controls accessible.
- [ ] 3.4 Confirm sorting, filtering, and ineligible-row behavior remain unchanged after column layout updates.

## 4. Playwright screenshot and UI assertions

- [ ] 4.1 Update Playwright assertions for the compare action position in the navigation panel and updated footer alignment.
- [ ] 4.2 Refresh baseline screenshot assets for the expanded navigation state.
- [ ] 4.3 Add a collapsed-navigation screenshot baseline and assertion for hidden-content affordance visibility.
- [ ] 4.4 Run headless Playwright tests and update approval artifacts for deterministic CI execution.

## 5. Validation and readiness checks

- [ ] 5.1 Run module tests covering manual selection and home-page layout behavior after UI changes.
- [ ] 5.2 Run OpenSpec validation for the new change artifacts and resolve any spec-format issues.
- [ ] 5.3 Capture a short implementation note in the change files if technical trade-offs differ from design assumptions.
