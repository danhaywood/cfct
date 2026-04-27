## MODIFIED Requirements

### Requirement: Webapp provides a main UI shell
The webapp SHALL render the home page inside a Vaadin AppLayout shell with a hamburger menu affordance.
The hamburger menu control SHALL have a deterministic accessible label suitable for browser-level tests.
The shell SHALL place table-selection functionality in the AppLayout navigation area.
The shell SHALL place primary selection-stage actions in the navigation area above the table-selection grid.
The shell SHALL preserve the existing main content area for comparison-stage functionality.
The shell SHALL provide a visual collapsed-navigation affordance indicating hidden actions or content remain available.

#### Scenario: Hamburger menu is available
- **WHEN** the home page is rendered
- **THEN** a hamburger menu control is visible with a deterministic accessible label

#### Scenario: Main content remains visible
- **WHEN** the main UI shell is rendered
- **THEN** the comparison-stage region remains visible in the content area and the table-selection region remains visible in the navigation area

#### Scenario: Collapsed navigation shows hidden-content affordance
- **WHEN** the navigation panel is collapsed
- **THEN** the UI shows an indicator that actions or content are available in the collapsed navigation area

### Requirement: Comparison area exposes a placeholder compare action
The webapp SHALL provide a `Compare` button in the navigation-area selection stage.
The `Compare` button SHALL be rendered above the table-selection grid.
The `Compare` button SHALL be aligned to the right within its action row.
The `Compare` button SHALL be disabled when no eligible table is selected.
The `Compare` button SHALL be enabled when one or more eligible tables are selected.
Activating the `Compare` button SHALL NOT start comparison execution in this change.

#### Scenario: Compare button is disabled before selection
- **WHEN** the home page is rendered and no eligible tables are selected
- **THEN** the `Compare` button is disabled

#### Scenario: Compare button is enabled after selection
- **WHEN** a user selects at least one eligible table
- **THEN** the `Compare` button becomes enabled

#### Scenario: Compare button is positioned above table catalog
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** the `Compare` button appears above the table grid and aligned to the right

#### Scenario: Compare button does not execute comparison
- **WHEN** a user activates the enabled `Compare` button
- **THEN** no comparison execution is started
