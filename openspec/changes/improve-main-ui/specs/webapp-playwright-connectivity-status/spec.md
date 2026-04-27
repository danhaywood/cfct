## MODIFIED Requirements

### Requirement: Headless Playwright validates home-page SQL connectivity status
The project SHALL provide headless Playwright tests for webapp home-page SQL connectivity status behavior.
Playwright tests SHALL verify both success and failure status rendering on the home page.
Playwright tests SHALL verify manual table-selection behavior including checkbox toggling, disabled ineligible tables, and selection-count feedback.
Playwright tests SHALL verify the happy path for the AppLayout shell, connection-details and status footer, navigation-area table-selection Grid sorting and filtering, ineligible non-selectable rows, and `Compare` button enablement.
Playwright tests SHALL run against Testcontainers-provisioned SQL Server scenarios to preserve reproducibility.

#### Scenario: Playwright verifies OK status
- **WHEN** a headless Playwright test opens the home page for a webapp instance with valid SQL connectivity configuration
- **THEN** the test asserts that the home page renders connection status OK

#### Scenario: Playwright verifies FAILED status
- **WHEN** a headless Playwright test opens the home page for a webapp instance with invalid SQL connectivity configuration
- **THEN** the test asserts that the home page renders connection status FAILED and a failure summary

#### Scenario: Playwright verifies manual selection interactions
- **WHEN** a headless Playwright test interacts with the table-selection panel
- **THEN** the test asserts that eligible table checkboxes toggle, ineligible rows are not selectable, and selected-table feedback updates correctly

#### Scenario: Playwright verifies main UI happy path
- **WHEN** a headless Playwright test opens the home page for a valid webapp instance and interacts with the updated UI
- **THEN** the test asserts that the hamburger menu is visible, the footer displays non-sensitive connection details and SQL connectivity status, Grid sorting and filtering work without an apply-filter button, ineligible rows are non-selectable, and the `Compare` button enables after selecting an eligible table

### Requirement: Playwright execution is scriptable for local and CI usage
The project SHALL provide a documented command or helper script that runs the connectivity-status Playwright tests headlessly.
The execution path SHALL be compatible with non-interactive CI environments.

#### Scenario: Headless Playwright command executes in CI-style environment
- **WHEN** the documented Playwright command is run in a non-interactive environment with required dependencies
- **THEN** connectivity-status browser tests execute without requiring a visible browser session
