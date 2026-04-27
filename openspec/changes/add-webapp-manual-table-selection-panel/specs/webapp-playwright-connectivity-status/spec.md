## MODIFIED Requirements

### Requirement: Headless Playwright validates home-page SQL connectivity status
The project SHALL provide headless Playwright tests for webapp home-page SQL connectivity status behavior.
Playwright tests SHALL verify both success and failure status rendering on the home page.
Playwright tests SHALL verify manual table-selection behavior including checkbox toggling, disabled ineligible tables, and selection-count feedback.
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
