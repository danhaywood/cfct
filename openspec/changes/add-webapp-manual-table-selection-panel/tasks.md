## 1. Table catalog and eligibility foundation

- [ ] 1.1 Add a table-catalog model for the webapp selection stage that includes table identity, eligibility, and selected state.
- [ ] 1.2 Add a table-discovery path that loads candidate tables for display in the manual selection panel.
- [ ] 1.3 Implement `_BK` eligibility evaluation and attach eligibility reason metadata for ineligible rows.
- [ ] 1.4 Add unit tests for table-discovery mapping and `_BK` eligibility evaluation behavior.

## 2. Left-panel manual selection UI

- [ ] 2.1 Update the home-page layout to introduce a left selection region and a right comparison region placeholder.
- [ ] 2.2 Render the left selection panel as a grid-style table list with one checkbox per row.
- [ ] 2.3 Apply disabled/greyed styling and non-interactive checkboxes for ineligible tables.
- [ ] 2.4 Add live selected-table feedback text that updates immediately as checkboxes are toggled.
- [ ] 2.5 Add UI tests validating checkbox toggling, disabled rows, and selection-count feedback updates.

## 3. Two-stage workflow contract

- [ ] 3.1 Ensure table selection changes update stage-one selection state without triggering comparison execution.
- [ ] 3.2 Expose selected-table output in a form consumable by the later comparison-run stage.
- [ ] 3.3 Add tests verifying that selection interaction does not auto-run comparison logic.

## 4. Playwright and documentation updates

- [ ] 4.1 Extend headless Playwright tests to validate table-selection panel behavior with deterministic selectors.
- [ ] 4.2 Add Playwright scenarios covering eligible selection, ineligible disablement, and feedback count changes.
- [ ] 4.3 Ensure Playwright selection tests run with Testcontainers-backed reproducible table fixtures.
- [ ] 4.4 Update README with the two-stage workflow description and manual selection behavior notes.

## 5. Verification

- [ ] 5.1 Run webapp unit tests for selection catalog, eligibility, and UI interaction behavior.
- [ ] 5.2 Run headless Playwright tests that include the new manual selection assertions.
- [ ] 5.3 Run affected module or reactor tests to confirm no regressions in connectivity status and existing webapp flows.
