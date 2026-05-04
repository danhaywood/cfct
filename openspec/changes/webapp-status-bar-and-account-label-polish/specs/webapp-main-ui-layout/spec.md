## MODIFIED Requirements

### Requirement: Webapp provides a main UI shell
The webapp SHALL render the home page inside a Vaadin AppLayout shell with a hamburger menu affordance.
The hamburger menu control SHALL have a deterministic accessible label suitable for browser-level tests.
The shell SHALL place table-selection functionality in the AppLayout navigation area.
The shell SHALL place command-selection functionality in the AppLayout navigation area above the table-selection grid.
The shell SHALL place primary selection-stage actions in the navigation area above the table-selection grid.
The shell SHALL preserve the existing main content area for comparison-stage functionality.
The shell SHALL keep the navbar minimal without persistent collapsed-state labels.
The shell SHALL provide a top-right account menu area in the navbar for authenticated session actions.
The shell SHALL render the account menu top-level label with authenticated username context when available.
The shell SHALL place logout inside the account menu instead of the left navigation area.
The shell SHALL add spacer treatment above the command-selection grid block in the left navigation area.
The shell SHALL render compact branding in the navbar, including a small logo/icon and visible `CFCT` name.

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

#### Scenario: Account menu shows username context
- **WHEN** an authenticated user has a resolvable username
- **THEN** the top-right account menu label includes that username

#### Scenario: Command grid appears above table grid in left navigation
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** the command-selection grid block appears above the table-selection grid block

#### Scenario: Command grid section includes spacer above
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** a visible spacer is rendered above the command-selection grid block to improve vertical rhythm

#### Scenario: Navbar shows compact CFCT branding
- **WHEN** an authenticated user views the main UI shell
- **THEN** the navbar displays a compact logo/icon and `CFCT` brand label with deterministic test hooks

### Requirement: Webapp footer displays non-sensitive connection details
The webapp SHALL display configured connection details and SQL connectivity status in a fixed footer/status bar on the main UI.
The footer SHALL include the configured left and right database names.
The footer SHALL NOT display the configured server JDBC URL.
The footer SHALL include the SQL connectivity status and failure summary when applicable.
The footer SHALL display live comparison progress status while a comparison run is active.
The footer SHALL display terminal completion or failure status when a comparison run finishes.
The footer SHALL NOT display the configured password.
The footer SHALL apply consistent horizontal spacing and horizontal padding between footer labels and values.
The footer content SHALL be stable enough for deterministic unit or browser-level assertions.

#### Scenario: Footer shows configured connection context
- **WHEN** the home page is rendered with configured connection properties
- **THEN** the footer shows the left and right database names and SQL connectivity status
- **AND** the footer does not show the server JDBC URL

#### Scenario: Footer excludes password
- **WHEN** the home page footer displays connection details
- **THEN** the configured password is not rendered in the page text

#### Scenario: Footer shows live comparison progress
- **WHEN** a user starts comparison for selected tables
- **THEN** the footer status area updates during execution with current-table and completed-versus-total progress information

#### Scenario: Footer shows terminal comparison state
- **WHEN** comparison execution completes or fails
- **THEN** the footer status area updates to a terminal success-or-failure message for that run

#### Scenario: Footer labels maintain readable horizontal spacing
- **WHEN** the fixed footer is visible during normal page usage
- **THEN** connection, status, and progress labels are separated by consistent horizontal padding and gap spacing

### Requirement: Compare action is below business table grid and visible on resize
The webapp SHALL render the compare action below the business table grid in the left navigation panel.
The webapp SHALL keep the compare action visible when the browser viewport is resized.
The webapp SHALL include visible spacer separation between command and business table sections.
The webapp SHALL reserve vertical clearance so the fixed footer/status bar does not overlap the compare action row.

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

#### Scenario: Compare action clears fixed footer area
- **WHEN** the fixed footer is visible and the user scrolls or resizes the drawer area
- **THEN** compare action controls remain visually above the footer with no overlap