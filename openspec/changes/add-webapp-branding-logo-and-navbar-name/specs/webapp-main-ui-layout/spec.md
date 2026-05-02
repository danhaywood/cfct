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

#### Scenario: Command grid appears above table grid in left navigation
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** the command-selection grid block appears above the table-selection grid block

#### Scenario: Command grid section includes spacer above
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** a visible spacer is rendered above the command-selection grid block to improve vertical rhythm

#### Scenario: Navbar shows compact CFCT branding
- **WHEN** an authenticated user views the main UI shell
- **THEN** the navbar displays a compact logo/icon and `CFCT` brand label with deterministic test hooks
