# webapp-comparison-results-tabs Specification

## Purpose
TBD - created by archiving change add-webapp-comparison-tabs-and-inmemory-results. Update Purpose after archive.
## Requirements
### Requirement: Webapp renders comparison results as per-table tabs
The webapp SHALL render comparison results in the right-side comparison stage as dynamic tabs.
The webapp SHALL create one tab per selected table included in the executed comparison request.
The tab labels SHALL identify table references deterministically as `schema.table`.
The webapp SHALL replace prior tab content when a new compare run is executed.

#### Scenario: Compare creates one tab per selected table
- **WHEN** a user clicks `Compare` with multiple eligible selected tables
- **THEN** the comparison stage shows one result tab per selected table

#### Scenario: New compare run replaces prior tabs
- **WHEN** a user changes selection and clicks `Compare` again
- **THEN** the comparison stage replaces prior tabs with tabs for the new result set

### Requirement: Each result tab shows an Excel-like comparison grid
Each per-table tab SHALL render a Vaadin Grid that presents row-level comparison output in an Excel-like style.
The grid SHALL include row classification signals that distinguish matching rows, differing rows, and side-only rows.
For logical fields whose compared values are equal, the grid SHALL render one shared value column.
For logical fields whose compared values differ, the grid SHALL render paired `L:` and `R:` columns for that field.
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

### Requirement: Compare execution controls are action-oriented in the results stage
The comparison stage SHALL omit non-informative placeholder labels once compare execution is implemented.
The stage SHALL disable repeated compare activation while a compare run is in progress.
The stage SHALL show a concise error summary when compare execution fails.
The stage SHALL provide controls to filter compared tables and to download the latest comparison as JSON or Excel.

#### Scenario: Compare disables repeated activation while running
- **WHEN** a compare run is started
- **THEN** the `Compare` action is disabled until execution completes

#### Scenario: Compare failure shows concise summary
- **WHEN** comparison execution fails for the selected tables
- **THEN** the comparison stage shows a concise failure message without stale prior result tabs

#### Scenario: Results stage provides filter and download actions
- **WHEN** comparison succeeds for selected tables
- **THEN** users can filter compared table tabs and download JSON or Excel output for the latest run

