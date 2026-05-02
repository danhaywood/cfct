# webapp-main-ui-layout Specification

## Purpose
TBD - created by archiving change improve-main-ui. Update Purpose after archive.
## Requirements
### Requirement: Webapp provides a main UI shell
The webapp SHALL render the home page inside a Vaadin AppLayout shell with a hamburger menu affordance.
The hamburger menu control SHALL have a deterministic accessible label suitable for browser-level tests.
The shell SHALL place table-selection functionality in the AppLayout navigation area.
The shell SHALL place command-selection functionality in the AppLayout navigation area above the table-selection grid.
The shell SHALL place primary selection-stage actions in the navigation area above the table-selection grid.
The shell SHALL preserve the existing main content area for comparison-stage functionality.
The shell SHALL keep the navbar minimal without persistent collapsed-state labels.
The shell SHALL provide a top-right account menu area in the navbar for authenticated session actions.
The shell SHALL place logout inside the account menu instead of the left navigation area.
The shell SHALL add spacer treatment above the command-selection grid block in the left navigation area.

#### Scenario: Hamburger menu is available
- **WHEN** the home page is rendered
- **THEN** a hamburger menu control is visible with a deterministic accessible label

#### Scenario: Main content remains visible
- **WHEN** the main UI shell is rendered
- **THEN** the comparison-stage region remains visible in the content area and the table-selection region remains visible in the navigation area

#### Scenario: Collapsed navigation keeps minimal navbar
- **WHEN** the navigation panel is collapsed
- **THEN** the navbar does not add persistent collapsed-state labels

#### Scenario: Authenticated navbar includes account menu actions
- **WHEN** an authenticated user views the main UI shell
- **THEN** the top-right navbar shows an account menu with a logout action and no standalone logout button in the left navigation area

#### Scenario: Command grid appears above table grid in left navigation
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** the command-selection grid block appears above the table-selection grid block

#### Scenario: Command grid section includes spacer above
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** a visible spacer is rendered above the command-selection grid block to improve vertical rhythm

### Requirement: Webapp footer displays non-sensitive connection details
The webapp SHALL display configured connection details and SQL connectivity status in a fixed footer/status bar on the main UI.
The footer SHALL include the configured server identity and the configured left and right database names.
The footer SHALL include the SQL connectivity status and failure summary when applicable.
The footer SHALL NOT display the configured password.
The footer content SHALL be stable enough for deterministic unit or browser-level assertions.

#### Scenario: Footer shows configured connection context
- **WHEN** the home page is rendered with configured connection properties
- **THEN** the footer shows the configured server identity, the left and right database names, and SQL connectivity status

#### Scenario: Footer excludes password
- **WHEN** the home page footer displays connection details
- **THEN** the configured password is not rendered in the page text

### Requirement: Comparison area exposes a placeholder compare action
The webapp SHALL provide a `Compare` button in the navigation-area selection stage.
The `Compare` button SHALL be rendered above the table-selection grid.
The `Compare` button SHALL be aligned to the right within its action row.
The `Compare` action row SHALL include top margin that visually aligns the compare control block with the height rhythm of the navbar hamburger/menu row.
The `Compare` button SHALL be disabled when no eligible table is selected.
The `Compare` button SHALL be enabled when one or more eligible tables are selected.
Activating the `Compare` button SHALL execute comparison orchestration for all currently selected eligible tables.
The comparison stage SHALL render the execution outcome in the right-side content area.

#### Scenario: Compare button is disabled before selection
- **WHEN** the home page is rendered and no eligible tables are selected
- **THEN** the `Compare` button is disabled

#### Scenario: Compare button is enabled after selection
- **WHEN** a user selects at least one eligible table
- **THEN** the `Compare` button becomes enabled

#### Scenario: Compare button is positioned above table catalog
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** the `Compare` button appears above the table grid and aligned to the right

#### Scenario: Compare action row aligns with navbar rhythm
- **WHEN** the selection stage is visible next to the main content area
- **THEN** the compare action row includes top spacing that visually aligns with the navbar hamburger/menu row height

#### Scenario: Compare button executes selected-table comparison
- **WHEN** a user activates the enabled `Compare` button
- **THEN** comparison is executed for the current selected eligible tables and the right-side comparison stage updates with resulting content

### Requirement: Compare action is below business table grid and visible on resize
The webapp SHALL render the compare action below the business table grid in the left navigation panel.
The webapp SHALL keep the compare action visible when the browser viewport is resized.
The webapp SHALL include visible spacer separation between command and business table sections.

#### Scenario: Drawer order places compare below business grid
- **WHEN** the home page drawer is rendered
- **THEN** the command section appears first
- **AND** the business table grid appears before the compare action

#### Scenario: Compare action remains visible during resized viewport usage
- **WHEN** the browser viewport is resized to a smaller height
- **THEN** the compare action remains visible and reachable without losing context of selected tables

#### Scenario: Spacer separates command and business table sections
- **WHEN** the drawer is rendered
- **THEN** a spacer element is present between the command selection area and the business table selection area

