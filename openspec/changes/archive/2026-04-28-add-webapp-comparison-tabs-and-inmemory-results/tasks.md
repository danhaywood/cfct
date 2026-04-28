## 1. API contract and in-memory result model

- [x] 1.1 Extend API comparison service contracts to expose in-memory per-table comparison result structures for webapp consumption.
- [x] 1.2 Add or refine API DTOs for tab-friendly table identity, row-level comparison status, and paired left/right values.
- [x] 1.3 Update implementation configuration wiring so webapp receives the new in-memory result-capable API service beans.

## 2. Datasource-based comparison execution flow

- [x] 2.1 Refactor datasource-based webapp comparison execution service to return in-memory multi-table results instead of placeholder behavior.
- [x] 2.2 Ensure connection acquisition and closure semantics remain bounded to service execution scope.
- [x] 2.3 Verify deterministic comparison semantics (key discovery, row matching, difference classification) remain unchanged after refactor.

## 3. Webapp compare action and tabbed result UI

- [x] 3.1 Implement compare-button click handling to execute comparison for all currently selected eligible tables.
- [x] 3.2 Add right-side dynamic tab container that creates one tab per compared `schema.table`.
- [x] 3.3 Implement per-tab Vaadin Grid rendering for paired left/right values and row classification indicators.
- [x] 3.4 Add loading/error state handling for compare execution and prevent repeated compare clicks while a run is active.
- [x] 3.5 Remove obsolete comparison-stage placeholder labels and expose compared-table filter plus JSON/Excel download actions.
- [x] 3.6 Add Excel-like status color coding for comparison grid row classification.

## 4. Playwright and unit test updates

- [x] 4.1 Update unit tests for `MainView` and comparison orchestration services to cover compare execution and tabbed result rendering.
- [x] 4.2 Extend Playwright happy-path test to select multiple eligible tables, click compare, and assert dynamic result tabs appear.
- [x] 4.3 Add Playwright assertions for per-tab comparison grid presence and paired-side value rendering semantics.
- [x] 4.4 Refresh screenshot baselines for expanded, selected, and collapsed states with result-stage updates where relevant.

## 5. Validation and documentation

- [x] 5.1 Run module tests for `sqlcomparer-api`, `sqlcomparer-impl`, and `sqlcomparer-webapp` to verify cross-module behavior.
- [x] 5.2 Run headless Playwright connectivity and comparison-flow tests in Testcontainers-backed mode.
- [x] 5.3 Update README and/or webapp notes to describe compare execution behavior and tabbed in-app result presentation.
- [x] 5.4 Run OpenSpec validation for the new change artifacts and resolve any formatting or structural issues.
