## MODIFIED Requirements

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

### Requirement: Compare execution controls are action-oriented in the results stage
The comparison stage SHALL omit non-informative placeholder labels once compare execution is implemented.
The stage SHALL disable repeated compare activation while a compare run is in progress.
The stage SHALL show a concise error summary when compare execution fails.
The stage SHALL provide controls to filter compared tables and to download the latest comparison as JSON, YAML, or Excel.
The stage SHALL provide a `Differences only` checkbox that filters compared tables to changed results.
The download controls SHALL be rendered in a dedicated top row above filter and grid content.
The download controls row SHALL be right-aligned to indicate these actions apply to the full comparison run.
The download controls SHALL use one consolidated download action with a format dropdown selector.
The format dropdown SHALL default to `json` when result actions are first shown.
The compared-table filter SHALL be rendered in a separate content-controls row adjacent to result exploration content.

#### Scenario: Compare disables repeated activation while running
- **WHEN** a compare run is started
- **THEN** the `Compare` action is disabled until execution completes

#### Scenario: Compare failure shows concise summary
- **WHEN** comparison execution fails for the selected tables
- **THEN** the comparison stage shows a concise failure message without stale prior result tabs

#### Scenario: Results stage provides filter and download actions
- **WHEN** comparison succeeds for selected tables
- **THEN** users can filter compared table tabs and download JSON, YAML, or Excel output for the latest run

#### Scenario: Differences-only filter narrows visible result tabs
- **WHEN** comparison succeeds and the user enables `Differences only`
- **THEN** unchanged-table tabs are hidden and changed-table tabs remain visible

#### Scenario: Download actions are grouped above filter/grid
- **WHEN** comparison succeeds and result actions are visible
- **THEN** the unified download action and format selector appear in a dedicated top row above filter and result-grid content

#### Scenario: Download actions are right-aligned as global controls
- **WHEN** result actions are visible
- **THEN** the download actions row is right-aligned and visually separated from the compared-table filter row
