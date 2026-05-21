# webapp-differences-only-results-filter Specification

## Purpose
TBD - created by archiving change highlight-different-tabs-and-add-differences-filter. Update Purpose after archive.
## Requirements
### Requirement: Results stage supports differences-only table filtering
The webapp SHALL provide a `Diff tables only` checkbox in the comparison results controls.
When unchecked, the results stage SHALL show all compared tables that satisfy existing compared-table text filtering.
When checked, the results stage SHALL show only compared tables whose result contains at least one differing row or side-only row.
The differences-only table filter SHALL compose with the existing compared-table text filter.

#### Scenario: Diff-tables-only filter is off by default
- **WHEN** comparison results are first rendered after a successful run
- **THEN** the `Diff tables only` checkbox is unchecked
- **AND** compared table tabs are not reduced by diff-tables-only filtering until the user enables it

#### Scenario: Diff-tables-only filter hides unchanged tables
- **WHEN** the user checks `Diff tables only`
- **THEN** only tabs for compared tables with differences remain visible

#### Scenario: Diff-tables-only filter composes with text filter
- **WHEN** the user applies compared-table text filtering and also checks `Diff tables only`
- **THEN** only compared tables that match both filters remain visible

