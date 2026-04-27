## ADDED Requirements

### Requirement: Webapp provides a main UI shell
The webapp SHALL render the home page inside a main UI shell with a hamburger menu affordance.
The hamburger menu control SHALL have a deterministic accessible label suitable for browser-level tests.
The shell SHALL preserve the existing home page content area for connectivity and table-selection functionality.

#### Scenario: Hamburger menu is available
- **WHEN** the home page is rendered
- **THEN** a hamburger menu control is visible with a deterministic accessible label

#### Scenario: Main content remains visible
- **WHEN** the main UI shell is rendered
- **THEN** the connectivity status and table-selection region remain visible in the content area

### Requirement: Webapp footer displays non-sensitive connection details
The webapp SHALL display configured connection details in a footer on the main UI.
The footer SHALL include the configured server identity and the configured left and right database names.
The footer SHALL NOT display the configured password.
The footer content SHALL be stable enough for deterministic unit or browser-level assertions.

#### Scenario: Footer shows configured connection context
- **WHEN** the home page is rendered with configured connection properties
- **THEN** the footer shows the configured server identity and the left and right database names

#### Scenario: Footer excludes password
- **WHEN** the home page footer displays connection details
- **THEN** the configured password is not rendered in the page text

### Requirement: Table selection exposes a placeholder compare action
The webapp SHALL provide a `Compare` button in the left-hand table-selection area.
The `Compare` button SHALL be disabled when no eligible table is selected.
The `Compare` button SHALL be enabled when one or more eligible tables are selected.
Activating the `Compare` button SHALL NOT start comparison execution in this change.

#### Scenario: Compare button is disabled before selection
- **WHEN** the home page is rendered and no eligible tables are selected
- **THEN** the `Compare` button is disabled

#### Scenario: Compare button is enabled after selection
- **WHEN** a user selects at least one eligible table
- **THEN** the `Compare` button becomes enabled

#### Scenario: Compare button does not execute comparison
- **WHEN** a user activates the enabled `Compare` button
- **THEN** no comparison execution is started
