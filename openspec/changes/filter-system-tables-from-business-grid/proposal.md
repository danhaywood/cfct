## Why

The business table grid currently includes internal support tables used for command and audit footprint plumbing.
These internal tables add noise for end users and can lead to accidental selection of non-business data.

## What Changes

- Exclude command-log, audit-trail, and logical-type mapping tables from the manual business table grid.
- Keep those tables available for internal workflows and command-driven features, but do not render them as business-table candidates.
- Preserve existing table-grid filtering, sorting, and eligibility behavior for remaining business tables.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-manual-table-selection`: Add explicit exclusion rules so system support tables are not shown in the business table grid.

## Impact

This change affects table-catalog discovery and filtering logic in `sqlcomparer-webapp`.
It may require updates to unit and browser tests that assert table-grid contents.
