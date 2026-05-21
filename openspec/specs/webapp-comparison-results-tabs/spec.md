# webapp-comparison-results-tabs Specification

## Purpose
TBD - created by archiving change add-webapp-comparison-tabs-and-inmemory-results. Update Purpose after archive.
## Requirements
### Requirement: Webapp renders comparison results as per-table tabs
The webapp SHALL render comparison results in the right-side comparison stage as dynamic tabs.
The webapp SHALL create one tab per selected table included in the executed comparison request.
The tab labels SHALL identify table references deterministically as `schema.table`.
The webapp SHALL visually distinguish tabs for tables with differences from tabs for tables without differences using deterministic styling hooks.
The webapp SHALL treat side-only rows as differences for tab-distinction purposes.
The webapp SHALL replace prior tab content when a new compare run is executed.

#### Scenario: Compare creates one tab per selected table
- **WHEN** a user clicks `Compare` with multiple eligible selected tables
- **THEN** the comparison stage shows one result tab per selected table

#### Scenario: Tabs with differences are visually distinguished
- **WHEN** compared results include at least one table with row differences and at least one unchanged table
- **THEN** tabs for changed tables show deterministic difference styling
- **AND** unchanged-table tabs do not use that difference styling

#### Scenario: New compare run replaces prior tabs
- **WHEN** a user changes selection and clicks `Compare` again
- **THEN** the comparison stage replaces prior tabs with tabs for the new result set

### Requirement: Each result tab shows an Excel-like comparison grid
Each per-table tab SHALL render a Vaadin Grid that presents row-level comparison output in an Excel-like style.
The grid SHALL include row classification signals that distinguish matching rows, differing rows, and side-only rows.
For logical fields whose compared values are equal, the grid SHALL render one shared value column.
For logical fields whose compared values differ, the grid SHALL render paired `L:` and `R:` columns for that field.
The grid SHALL apply cell-level highlight styling for value differences and side-only missing values using deterministic semantic CSS classes.
The grid and its surrounding results content SHALL remain responsive within the comparison-stage container at varying viewport sizes.
The comparison-stage result content SHALL expand to consume the full available vertical extent in the main content region.
The result-grid viewport SHALL use the remaining vertical space below results controls and tabs.
The result-grid viewport SHALL reserve bottom clearance so fixed footer content does not overlap or obscure result rows.
The grid SHALL provide horizontal scrolling when rendered columns exceed the available results width.
The grid SHALL provide vertical scrolling when rendered rows exceed the available results height.
The grid SHALL keep table identity and row-order presentation deterministic for test assertions.

#### Scenario: Equal values are rendered once
- **WHEN** a result tab is opened for rows where a logical field has equal left and right values
- **THEN** that field is rendered as a single shared value column without paired `L:` and `R:` columns

#### Scenario: Differing values keep paired columns
- **WHEN** a result tab is opened for rows where a logical field has differing left and right values
- **THEN** that field is rendered with paired `L:` and `R:` columns

#### Scenario: Grid highlights row difference classification
- **WHEN** compared rows include differences or side-only rows
- **THEN** the grid visually indicates row classification for those rows using Excel-like status color coding

#### Scenario: Differing cells are highlighted
- **WHEN** a displayed row contains a logical field where left and right values differ
- **THEN** the corresponding value cells are rendered with the deterministic difference highlight class

#### Scenario: Missing-side cells are highlighted
- **WHEN** a displayed row exists only on one side
- **THEN** cells representing the missing side are rendered with deterministic missing-value highlight classes

#### Scenario: Responsive container bounds are preserved
- **WHEN** the page viewport is reduced and compared content is displayed
- **THEN** comparison widgets remain within the visible bounds of the comparison-stage container

#### Scenario: Result grid uses full available depth above footer
- **WHEN** comparison results are displayed in the right-side comparison stage
- **THEN** the active result-grid viewport expands to use the full available vertical space
- **AND** the viewport bottom remains above the fixed footer with no overlap

#### Scenario: Wide grids remain navigable
- **WHEN** compared output produces more columns than the visible results width
- **THEN** the user can horizontally scroll within the comparison results area to access off-screen columns

#### Scenario: Tall grids remain navigable
- **WHEN** compared output produces more rows than the visible results height
- **THEN** the user can vertically scroll within the comparison results area to access off-screen rows

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

### Requirement: Result grid hides MATCH rows by default with explicit opt-in
The comparison result grid SHALL exclude rows classified as `MATCH` by default when a comparison result tab is first shown.
The comparison stage SHALL NOT render a `Show MATCH rows` checkbox in the results controls.
The comparison stage SHALL keep MATCH-row visibility fixed to excluded in standard result exploration.

#### Scenario: MATCH rows are hidden by default
- **WHEN** a comparison run completes and a table result tab is opened
- **THEN** rows classified as `MATCH` are not shown in the initial grid view

#### Scenario: Results controls omit MATCH visibility toggle
- **WHEN** comparison succeeds and result controls are rendered
- **THEN** no `Show MATCH rows` checkbox is visible in the results controls

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

