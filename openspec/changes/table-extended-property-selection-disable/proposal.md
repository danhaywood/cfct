## Why

The webapp already uses metadata-driven ignore decisions for columns, but table-level exclusion still requires hardcoded logic.
Allowing table-level extended-property opt-out makes table eligibility configurable at the database layer and reduces accidental selection of tables that should not be compared.
This is especially useful for audit tables and Flyway migration history tables that should be excluded from bulk `Select all` operations.

## What Changes

- Add table-level metadata evaluation for a dedicated extended property that marks a business table as non-selectable in the manual table grid.
- Keep excluded tables visible in the grid, but render them disabled with a tooltip explaining that metadata-based exclusion is active.
- Ensure metadata-excluded rows are not selected by `Select all`, including practical examples such as audit and Flyway history tables configured with `cfct.ignored=true`.
- Keep metadata-excluded rows visible (including under `Selected only`) so users can see they are intentionally excluded and read the tooltip reason.
- Ensure command-driven and keyboard-driven selection workflows honor the same disabled eligibility state.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-manual-table-selection`: Extend eligibility behavior to support table-level extended-property exclusion with explanatory tooltip messaging.

## Impact

- Affected webapp catalog-loading and eligibility-evaluation code for manual table rows.
- Affected selection UI rendering and tooltip text for disabled rows.
- Affected UI and integration tests that assert selectable rows and eligibility reasons.
