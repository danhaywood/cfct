## Why

A table with a single primary-key constraint named with the `_PK` suffix can be incorrectly marked ineligible when other metadata or unique constraints are present.
This blocks valid comparisons such as `isisExtSecman.ApplicationUser`, where the stable aligned `id` primary key is the correct row identity and unrelated unique constraints such as `username` should not make selection ambiguous.

## What Changes

- Align manual table eligibility discovery with core business-key discovery so `_PK` matching is based on distinct literal suffix candidates rather than multiplied SQL aggregate rows.
- Prefer a single `_PK`-suffixed primary-key candidate when other `_PK`-suffixed unique candidates are also present.
- Keep ambiguity failures when multiple `_PK`-suffixed candidates remain and no single primary key can safely disambiguate them.
- Preserve existing behavior for tables with no `_PK`-suffixed unique object and for metadata-disabled tables.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `core-single-table-comparison`: Business-key discovery will prefer an unambiguous `_PK`-suffixed primary key candidate when multiple matching unique objects are present.
- `webapp-manual-table-selection`: Manual table eligibility will use distinct literal `_PK` key-object semantics and will not mark a table ineligible because unrelated unique constraints or table-level metadata joins multiply rows.

## Impact

- Affects SQL Server metadata discovery in `cfct-impl` for core comparison key selection.
- Affects SQL Server table-catalog eligibility discovery in `cfct-webapp`.
- Requires unit and integration coverage for primary-key preference, unrelated unique constraints, literal `_PK` suffix matching, and extended-property join multiplication.
- No database schema migration, public API change, or dependency change is expected.
