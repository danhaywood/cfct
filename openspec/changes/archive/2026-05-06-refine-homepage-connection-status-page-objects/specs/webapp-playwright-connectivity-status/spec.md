## MODIFIED Requirements

### Requirement: Playwright tests use reusable page objects for UI interaction
The Playwright connectivity-status test suite SHALL route UI selectors and user interactions through page-object classes instead of duplicating direct selector logic in each test.
Page-object classes SHALL provide workflow-oriented methods for navigation-shell interaction, footer-status interaction, table-selection interaction, and compare-result interaction.
Playwright scenario tests SHALL keep behavior assertions explicit while consuming page-object methods for setup and interaction steps.
Playwright scenario tests SHALL NOT call raw Playwright `Page` selector or wait APIs directly for domain interaction choreography.
Page-object readiness methods SHALL use intent-revealing semantics that describe business readiness outcomes instead of low-level control-state tuple checks.

#### Scenario: Connectivity-status happy path executes through page objects
- **WHEN** a headless Playwright test runs the happy-path connectivity and compare workflow
- **THEN** the test invokes page-object methods for UI interactions and completes existing assertions without direct duplicated selector choreography

#### Scenario: Connectivity-status failure path executes through page objects
- **WHEN** a headless Playwright test runs a failure-path workflow for missing database or invalid connectivity
- **THEN** the test invokes page-object methods for interaction and retains existing failure-status assertions

#### Scenario: Connectivity-status scenario tests avoid direct raw Page selector choreography
- **WHEN** a connectivity-status scenario test performs selection and compare setup interactions
- **THEN** the scenario test uses only page-object methods and does not directly call raw `Page` selector or wait APIs for domain interactions

#### Scenario: Connectivity-status readiness checks use semantic page-object methods
- **WHEN** a connectivity-status scenario test waits for selection or compare controls to become actionable
- **THEN** the wait is expressed through intention-revealing page-object methods rather than low-level multi-checkbox state helper signatures
