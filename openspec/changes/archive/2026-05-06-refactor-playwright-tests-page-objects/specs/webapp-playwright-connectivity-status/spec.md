## ADDED Requirements

### Requirement: Playwright tests use reusable page objects for UI interaction
The Playwright connectivity-status test suite SHALL route UI selectors and user interactions through page-object classes instead of duplicating direct selector logic in each test.
Page-object classes SHALL provide workflow-oriented methods for navigation-shell interaction, footer-status interaction, table-selection interaction, and compare-result interaction.
Playwright scenario tests SHALL keep behavior assertions explicit while consuming page-object methods for setup and interaction steps.

#### Scenario: Connectivity-status happy path executes through page objects
- **WHEN** a headless Playwright test runs the happy-path connectivity and compare workflow
- **THEN** the test invokes page-object methods for UI interactions and completes existing assertions without direct duplicated selector choreography

#### Scenario: Connectivity-status failure path executes through page objects
- **WHEN** a headless Playwright test runs a failure-path workflow for missing database or invalid connectivity
- **THEN** the test invokes page-object methods for interaction and retains existing failure-status assertions
