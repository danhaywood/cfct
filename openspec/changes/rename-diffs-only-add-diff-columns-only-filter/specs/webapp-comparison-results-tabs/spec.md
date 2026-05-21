## MODIFIED Requirements

### Requirement: Compare execution controls are action-oriented in the results stage
The comparison stage SHALL omit non-informative placeholder labels once compare execution is implemented.
The stage SHALL disable repeated compare activation while a compare run is in progress.
The stage SHALL show a concise error summary when compare execution fails.
The stage SHALL provide controls to filter compared tables and to download the latest comparison as JSON, YAML, or Excel.
The stage SHALL provide a `Diff tables only` checkbox that filters compared tables to changed results.
The stage SHALL provide a `Diff columns only` checkbox for filtering visible columns in the active result tab.
The `Diff columns only` checkbox SHALL be enabled only when the active selected table has differences.
The `Diff columns only` checkbox SHALL be disabled when no active table tab is selected or when the active selected table has no differences.
The download controls SHALL be rendered in a dedicated top row above filter and grid content.
The download controls row SHALL be right-aligned to indicate these actions apply to the full comparison run.
The download controls SHALL use one consolidated download action with a format dropdown selector.
The format dropdown SHALL default to `json` when result actions are first shown.
The compared-table filter SHALL be rendered in a separate content-controls row adjacent to result exploration content.

#### Scenario: Differences-only table filter narrows visible result tabs
- **WHEN** comparison succeeds and the user enables `Diff tables only`
- **THEN** unchanged-table tabs are hidden and changed-table tabs remain visible

#### Scenario: Diff-columns-only control is disabled without eligible table
- **WHEN** comparison succeeds and no differing table tab is active
- **THEN** the `Diff columns only` checkbox is visible but disabled

#### Scenario: Diff-columns-only control enables for differing active table
- **WHEN** the user selects a result tab whose table contains differences
- **THEN** the `Diff columns only` checkbox becomes enabled

### Requirement: Result grid supports sortable columns and value filtering
The comparison result grid SHALL allow sorting on visible data columns in ascending and descending order.
The comparison result grid SHALL provide value-filter controls for narrowing visible rows by column value.
Sorting and filtering SHALL be combinable in a single active result view.
When `Diff columns only` is checked, the result grid SHALL show only logical fields that have at least one differing value in the active table result.
When `Diff columns only` is unchecked, the result grid SHALL show the standard full field set for the active table result.
When `Diff columns only` is checked for an eligible table, existing row filters and sorts SHALL continue to operate on the reduced visible column set.

#### Scenario: Diff-columns-only hides unchanged logical fields
- **WHEN** a differing table tab is active and the user checks `Diff columns only`
- **THEN** logical fields with no value differences in that table are hidden from the grid

#### Scenario: Diff-columns-only preserves differing logical fields
- **WHEN** a differing table tab is active and the user checks `Diff columns only`
- **THEN** logical fields with one or more differing values remain visible in the grid

#### Scenario: Diff-columns-only off restores full field set
- **WHEN** the user unchecks `Diff columns only`
- **THEN** the active table grid restores the standard full field set
