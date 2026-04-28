## MODIFIED Requirements

### Requirement: Compare execution controls are action-oriented in the results stage
The comparison stage SHALL omit non-informative placeholder labels once compare execution is implemented.
The stage SHALL disable repeated compare activation while a compare run is in progress.
The stage SHALL show a concise error summary when compare execution fails.
The stage SHALL provide controls to filter compared tables and to download the latest comparison as JSON or Excel.
The download controls SHALL be rendered in a dedicated top row above filter and grid content.
The download controls row SHALL be right-aligned to indicate these actions apply to the full comparison run.
The compared-table filter SHALL be rendered in a separate content-controls row adjacent to result exploration content.

#### Scenario: Compare disables repeated activation while running
- **WHEN** a compare run is started
- **THEN** the `Compare` action is disabled until execution completes

#### Scenario: Compare failure shows concise summary
- **WHEN** comparison execution fails for the selected tables
- **THEN** the comparison stage shows a concise failure message without stale prior result tabs

#### Scenario: Results stage provides filter and download actions
- **WHEN** comparison succeeds for selected tables
- **THEN** users can filter compared table tabs and download JSON or Excel output for the latest run

#### Scenario: Download actions are grouped above filter/grid
- **WHEN** comparison succeeds and result actions are visible
- **THEN** JSON and Excel download controls appear in a dedicated top row above filter and result-grid content

#### Scenario: Download actions are right-aligned as global controls
- **WHEN** result actions are visible
- **THEN** the download actions row is right-aligned and visually separated from the compared-table filter row
