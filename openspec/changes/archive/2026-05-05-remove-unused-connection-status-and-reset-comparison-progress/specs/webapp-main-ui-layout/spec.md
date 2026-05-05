## MODIFIED Requirements

### Requirement: Webapp footer displays non-sensitive connection details
The webapp SHALL display configured connection details and comparison progress status in a fixed footer/status bar on the main UI.
The footer SHALL include the configured left and right database names.
The footer SHALL NOT display the configured server JDBC URL.
The footer SHALL NOT render connection status state text.
The footer SHALL NOT render connection status summary text.
The footer SHALL display live comparison progress status while a comparison run is active.
The footer SHALL display terminal completion or failure status when a comparison run finishes.
The footer SHALL apply an explicit success background treatment for terminal successful completion.
The footer SHALL apply an explicit failure background treatment for terminal failure completion.
The footer SHALL clear previously rendered comparison status text and terminal outcome styling when drawer selection parameters change.
The footer SHALL clear previously rendered comparison status text and terminal outcome styling when the command-section Clear action is triggered.
The footer SHALL NOT display the configured password.
The footer SHALL apply consistent horizontal spacing and horizontal padding between footer labels and values.
The footer content SHALL be stable enough for deterministic unit or browser-level assertions.

#### Scenario: Footer shows configured connection context without connectivity status labels
- **WHEN** the home page is rendered with configured connection properties
- **THEN** the footer shows the left and right database names
- **AND** the footer does not show the server JDBC URL
- **AND** the footer does not show connection status state or summary labels

#### Scenario: Footer excludes password
- **WHEN** the home page footer displays connection details
- **THEN** the configured password is not rendered in the page text

#### Scenario: Footer shows live comparison progress
- **WHEN** a user starts comparison for selected tables
- **THEN** the footer status area updates during execution with current-table and completed-versus-total progress information

#### Scenario: Footer shows terminal comparison state with outcome background
- **WHEN** comparison execution completes or fails
- **THEN** the footer status area updates to a terminal success-or-failure message for that run
- **AND** the status area applies success styling for successful completion and failure styling for failed completion

#### Scenario: Footer clears prior comparison status after drawer parameter change
- **WHEN** a prior comparison status message is visible
- **AND** the user changes command-grid or business-table selection parameters in the drawer
- **THEN** the footer clears the previous comparison status message and terminal outcome background styling

#### Scenario: Footer clears prior comparison status on clear action
- **WHEN** a prior comparison status message is visible
- **AND** the user activates the command-section Clear control
- **THEN** the footer clears the previous comparison status message and terminal outcome background styling

#### Scenario: Footer labels maintain readable horizontal spacing
- **WHEN** the fixed footer is visible during normal page usage
- **THEN** connection and progress labels are separated by consistent horizontal padding and gap spacing
