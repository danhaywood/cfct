## Why

The webapp currently lets users select tables, but clicking `Compare` does not execute comparisons or present per-table results.
Users need immediate in-app comparison feedback per selected table without relying on exported files or external report formats.

## What Changes

- Execute comparison when the user clicks `Compare` for the currently selected eligible tables.
- Render comparison output on the right side as dynamic tabs, with one tab per selected table.
- Show a result grid in each table tab using an Excel-like visual style for readable side-by-side differences and status color coding.
- Refactor the comparison orchestration contract so webapp consumers receive comparison results in memory instead of requiring marshalled externalized formats.
- Keep existing datasource-managed connection lifecycle and comparison semantics while changing result delivery and UI rendering.
- Simplify right-side comparison chrome by removing placeholder labels and exposing practical controls: compared-table filter plus JSON/Excel download actions.

## Capabilities

### New Capabilities

- `webapp-comparison-results-tabs`: Define tabbed comparison-result presentation and per-table result-grid behavior in the web UI.

### Modified Capabilities

- `api-service-contracts`: Extend service contracts to return in-memory comparison result structures suitable for direct UI rendering.
- `datasource-based-comparison-execution`: Ensure datasource-based execution returns in-memory multi-table results while preserving deterministic comparison semantics.
- `webapp-main-ui-layout`: Change compare action behavior from placeholder to real execution and render results in right-side tabs.
- `webapp-playwright-connectivity-status`: Extend browser assertions to cover compare execution, dynamic result tabs, and result-grid rendering.

## Impact

The webapp UI layer, comparison execution service wiring, and API contract model will be updated to support in-memory result consumption.
Implementation and tests in `cfct-api`, `cfct-impl`, and `cfct-webapp` will be affected, including Playwright happy-path coverage.
Output rendering responsibilities will shift toward UI grids for interactive use while existing external report formats remain available for CLI workflows.
