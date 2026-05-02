## MODIFIED Requirements

### Requirement: Webapp footer displays non-sensitive connection details
The webapp SHALL display configured connection details and SQL connectivity status in a fixed footer/status bar on the main UI.
The footer SHALL include the configured server identity and the configured left and right database names.
The footer SHALL include the SQL connectivity status and failure summary when applicable.
The footer SHALL display live comparison progress status while a comparison run is active.
The footer SHALL display terminal completion or failure status when a comparison run finishes.
The footer SHALL NOT display the configured password.
The footer content SHALL be stable enough for deterministic unit or browser-level assertions.

#### Scenario: Footer shows configured connection context
- **WHEN** the home page is rendered with configured connection properties
- **THEN** the footer shows the configured server identity, the left and right database names, and SQL connectivity status

#### Scenario: Footer excludes password
- **WHEN** the home page footer displays connection details
- **THEN** the configured password is not rendered in the page text

#### Scenario: Footer shows live comparison progress
- **WHEN** a user starts comparison for selected tables
- **THEN** the footer status area updates during execution with current-table and completed-versus-total progress information

#### Scenario: Footer shows terminal comparison state
- **WHEN** comparison execution completes or fails
- **THEN** the footer status area updates to a terminal success-or-failure message for that run
