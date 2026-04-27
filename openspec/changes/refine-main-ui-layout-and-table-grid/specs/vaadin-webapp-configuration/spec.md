## MODIFIED Requirements

### Requirement: Home page provides a manual table-selection stage
The webapp home page SHALL provide a manual table-selection panel on the left side of the page before comparison execution.
The left panel SHALL list discovered tables with one checkbox control per eligible table.
The layout SHALL reserve a left selection region and a right comparison region, with the left region sized for selection-focused interaction.
The manual selection stage SHALL expose a compare action above the table catalog and aligned to the right within the navigation panel.

#### Scenario: User selects and deselects eligible tables
- **WHEN** a user toggles checkboxes for eligible tables in the left panel
- **THEN** the selected-table state updates immediately and is available for comparison activation

#### Scenario: Compare action is positioned above selection table
- **WHEN** the home page renders the manual table-selection stage
- **THEN** the compare action is visible above the table catalog and right-aligned in the navigation panel

#### Scenario: Selection stage is separate from comparison stage
- **WHEN** a user changes table selections in the left panel
- **THEN** comparison execution is not triggered until an explicit run action is invoked

### Requirement: Home page footer surfaces configured connection context
The webapp home page SHALL display configured connection context and SQL connectivity status in a fixed footer/status bar.
The footer/status bar SHALL read its displayed values from the same typed configuration properties used by the webapp startup and connectivity validation paths.
The footer/status bar SHALL display the SQL Server identity, left database name, right database name, and current SQL connectivity status.
The footer/status bar SHALL present connection details with compact spacing and without redundant field labels.
The footer/status bar SHALL right-align the SQL connectivity status text.
The footer/status bar SHALL omit or mask sensitive credential values.

#### Scenario: Footer uses configured properties
- **WHEN** the webapp starts with configured SQL Server and database values
- **THEN** the home page footer/status bar displays those configured connection values and SQL connectivity status

#### Scenario: Footer right-aligns status with compact presentation
- **WHEN** the home page footer/status bar renders connection context
- **THEN** connection details are shown with compact spacing, redundant labels are absent, and the SQL status text is right-aligned

#### Scenario: Footer protects credentials
- **WHEN** the home page footer/status bar displays configured connection context
- **THEN** sensitive credential values are omitted or masked
