## Why

Users cannot quickly identify which compared tables contain differences because all result tabs look the same.
Users also cannot quickly narrow results to only changed tables, and the current demo fixture set lacks a stable no-difference table to demonstrate this behavior.

## What Changes

- Color result tabs for tables that contain row differences or side-only rows.
- Add a result-stage checkbox filter to show only tables that have differences.
- Keep unchanged tables visible by default when the new filter is off.
- Extend the webapp SQL fixture with a new business-like `CustomerAddress` table that compares equal between left and right databases.
- Add or update browser-level tests to prove tab coloring and differences-only filtering behavior.

## Capabilities

### New Capabilities

- `webapp-differences-only-results-filter`: Defines result-stage filtering that restricts visible compared tables to those with differences.

### Modified Capabilities

- `webapp-comparison-results-tabs`: Add visual distinction for tabs with differences and filter interactions for changed-only views.
- `webapp-playwright-connectivity-status`: Extend browser tests to assert tab-color and differences-only filtering behavior.
- `sqlserver-two-databases-test-harness`: Extend deterministic fixture setup with a `CustomerAddress` comparable table that has no left/right differences.

## Impact

This change affects tab rendering and result controls in `cfct-webapp` comparison-stage UI.
This change affects Playwright fixture SQL setup and browser-level assertions.
No API contract, CLI behavior, or comparison-core diff semantics are expected to change.
