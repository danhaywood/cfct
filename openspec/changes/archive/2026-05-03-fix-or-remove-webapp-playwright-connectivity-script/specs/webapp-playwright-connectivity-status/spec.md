## MODIFIED Requirements

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
