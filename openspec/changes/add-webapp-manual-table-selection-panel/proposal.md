## Why

The webapp will eventually compare a selected subset of tables from large schemas, and users need visibility and control over that selection before running comparison.
A first-step manual selection panel reduces risk for the later auto-selection phase and creates a clear two-stage user workflow now.

## What Changes

- Add a left-hand manual table-selection panel that lists discovered tables with per-table checkboxes.
- Add eligibility signaling so tables that do not meet the `_BK` requirement are visible but disabled and visually greyed out.
- Add live feedback showing how many tables are currently selected for comparison.
- Establish the two-stage UX contract of `select tables` and then `run comparison`, with this change focusing on the selection stage.
- Add or update headless Playwright tests that verify table listing, disabled-table behavior, checkbox interaction, and feedback updates.

## Capabilities

### New Capabilities
- `webapp-manual-table-selection`: Manual user-driven table selection with eligibility-aware controls and selection feedback on the home page.

### Modified Capabilities
- `vaadin-webapp-configuration`: Extend UI requirements to include left-side selection workflow behavior and eligibility-driven interaction rules.
- `webapp-playwright-connectivity-status`: Extend browser test requirements so headless Playwright validates manual table-selection behavior in addition to connectivity status.

## Impact

This affects webapp UI layout, table discovery and eligibility evaluation presentation, and comparison-run preparation inputs.
It affects browser automation scope by adding interaction and assertion coverage for selection controls and live feedback.
It may require fixture conventions in tests to ensure both eligible and ineligible tables are represented predictably.
