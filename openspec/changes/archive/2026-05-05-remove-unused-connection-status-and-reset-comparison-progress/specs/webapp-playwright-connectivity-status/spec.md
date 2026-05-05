## MODIFIED Requirements

### Requirement: Headless Playwright validates home-page SQL connectivity status
The project SHALL provide headless Playwright tests for webapp home-page status-footer behavior.
Playwright tests SHALL verify that connection status state and summary labels are not rendered on the home page.
Playwright tests SHALL verify comparison progress terminal success and failure rendering in the footer status area, including outcome styling cues.
Playwright tests SHALL verify manual table-selection behavior including checkbox toggling, disabled ineligible tables, and omission of redundant selection labels.
Playwright tests SHALL verify the happy path for the AppLayout shell, connection-details footer content, navigation-area table-selection Grid sorting and filtering, ineligible non-selectable rows, and right-aligned navigation-area `Compare` button enablement.
Playwright tests SHALL validate collapsed-navigation state behavior without requiring persistent collapsed-state labels.
Playwright tests SHALL refresh screenshot baselines for the updated expanded navigation layout.
Playwright tests SHALL include a screenshot baseline for the collapsed navigation state.
Playwright tests SHALL verify compare execution creates dynamic per-table result tabs in the right-side comparison stage.
Playwright tests SHALL verify each result tab renders a comparison grid with paired left/right value presentation and status color cues.
Playwright tests SHALL verify compared-table filtering and JSON/Excel download controls are present after compare execution.
Playwright tests SHALL verify deterministic tab distinction between changed and unchanged compared tables.
Playwright tests SHALL verify a differences-only checkbox can hide unchanged compared-table tabs.
Playwright tests SHALL verify drawer selection-parameter changes and Clear action remove stale comparison progress status text.
Playwright tests SHALL run against Testcontainers-provisioned SQL Server scenarios to preserve reproducibility.
Playwright screenshot outputs used in documentation SHALL remain current for user workflows and SHALL not require a branding-logo-only screenshot.

#### Scenario: Playwright verifies connection status labels are absent
- **WHEN** a headless Playwright test opens the home page
- **THEN** the test asserts that connection status state and summary labels are not rendered in the footer status area

#### Scenario: Playwright verifies terminal success footer styling
- **WHEN** a headless Playwright test runs a successful compare workflow
- **THEN** the test asserts that the footer shows a terminal success message with success background styling

#### Scenario: Playwright verifies terminal failure footer styling
- **WHEN** a headless Playwright test triggers a compare workflow that fails
- **THEN** the test asserts that the footer shows a terminal failure message with failure background styling

#### Scenario: Playwright verifies stale status reset on drawer parameter mutation
- **WHEN** a prior comparison status report is visible and the test changes command-grid or business-table selection parameters
- **THEN** the test asserts that the prior comparison status report is cleared

#### Scenario: Playwright verifies stale status reset on clear action
- **WHEN** a prior comparison status report is visible and the test activates the command-section Clear control
- **THEN** the test asserts that the prior comparison status report is cleared

#### Scenario: Playwright verifies manual selection interactions
- **WHEN** a headless Playwright test interacts with the table-selection panel
- **THEN** the test asserts that eligible table checkboxes toggle, ineligible rows are not selectable, and redundant selection labels are absent

#### Scenario: Playwright verifies main UI happy path
- **WHEN** a headless Playwright test opens the home page for a valid webapp instance and interacts with the updated UI
- **THEN** the test asserts that the hamburger menu is visible, the footer displays non-sensitive connection details, Grid sorting and filtering work without an apply-filter button, and the navigation-area `Compare` button enables after selecting an eligible table

#### Scenario: Playwright verifies compare result tabs and grids
- **WHEN** a headless Playwright test triggers compare with multiple selected eligible tables
- **THEN** the test asserts that one result tab per selected table is rendered and each tab exposes a comparison grid with paired-side values

#### Scenario: Playwright verifies changed versus unchanged result-tab distinction
- **WHEN** compared results include at least one changed business table and one unchanged business table
- **THEN** the test asserts changed-table tabs expose difference styling hooks and unchanged-table tabs do not

#### Scenario: Playwright verifies differences-only filter behavior
- **WHEN** the user enables the differences-only checkbox in result controls
- **THEN** unchanged compared-table tabs are hidden and changed compared-table tabs remain visible

#### Scenario: Playwright captures expanded navigation screenshot
- **WHEN** a headless Playwright test opens the home page in expanded navigation mode
- **THEN** the test captures a refreshed baseline screenshot matching the updated layout

#### Scenario: Playwright captures collapsed navigation screenshot
- **WHEN** a headless Playwright test collapses the navigation panel
- **THEN** the test captures a baseline screenshot for the collapsed navigation state

#### Scenario: Playwright screenshot set excludes branding-only image
- **WHEN** screenshot outputs are reviewed for README workflow documentation
- **THEN** no branding-logo-only screenshot is required in the maintained set

### Requirement: Playwright execution is scriptable for local and CI usage
The project SHALL provide a documented and executable command or helper script that runs the connectivity-status Playwright tests headlessly.
The documented execution path SHALL be verified against the current repository layout and SHALL not reference stale or non-functional script locations.
The execution path SHALL be compatible with non-interactive CI environments.

#### Scenario: Headless Playwright command executes in CI-style environment
- **WHEN** the documented Playwright command is run in a non-interactive environment with required dependencies
- **THEN** connectivity-status browser tests execute without requiring a visible browser session

#### Scenario: Documented command path is valid in repository
- **WHEN** a contributor follows the README command for connectivity-status Playwright tests from a clean checkout
- **THEN** the command resolves to an existing script or direct executable path and begins test execution without path-not-found failures
