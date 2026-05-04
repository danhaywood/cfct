## 1. Comparison results grid behavior

- [x] 1.1 Update comparison result data-provider logic to exclude `MATCH` rows by default for newly rendered tabs.
- [x] 1.2 Add a `Show MATCH rows` checkbox in the result-controls area and bind it to live row visibility updates without re-running compare.
- [x] 1.3 Enable sortable behavior on relevant comparison grid columns and verify deterministic ordering behavior.
- [x] 1.4 Add per-column value filter inputs for the result grid and wire them to combined filter state.
- [x] 1.5 Add or update UI tests that cover default hidden MATCH rows, toggle-on behavior, sorting, and filtering combinations.

## 2. Compare action prominence and drawer layout safety

- [x] 2.1 Update theme/style tokens so `Compare` is rendered with primary-action emphasis and consistent hover/focus states.
- [x] 2.2 Refactor left-drawer layout containers to reserve dedicated compare-action space below business table content.
- [x] 2.3 Ensure business table selection grid sizing and overflow behavior cannot overlap or obscure the compare-action row.
- [x] 2.4 Add responsive UI tests for small-height viewports to confirm compare action remains visible and clickable.

## 3. Resizable navigation drawer

- [x] 3.1 Add an end-user drawer resize affordance for expanded navigation mode with configured min/max width bounds.
- [x] 3.2 Implement in-session state storage for selected drawer width and apply it across subsequent expand states.
- [x] 3.3 Ensure AppLayout content reflow remains stable during drawer resize and collapse/expand transitions.
- [x] 3.4 Add UI and integration tests for resize bounds, content reflow, and responsive behavior with custom drawer widths.

## 4. Validation and documentation

- [x] 4.1 Run unit and browser-level test suites relevant to comparison results, drawer layout, and main shell interactions.
- [x] 4.2 Update developer-facing docs or test notes for new result-grid controls and drawer resizing behavior if needed.
- [x] 4.3 Perform final manual QA of compare workflow end-to-end and capture any follow-up fixes before merge.