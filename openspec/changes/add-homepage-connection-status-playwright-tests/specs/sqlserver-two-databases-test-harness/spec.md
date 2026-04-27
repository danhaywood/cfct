## ADDED Requirements

### Requirement: Harness supports browser-level connectivity-status testing
The SQL Server harness SHALL provide deterministic connection settings consumable by headless Playwright tests.
The harness SHALL support a happy-path setup where both configured logical databases exist.
The harness SHALL support failure-path setups for missing databases and unreachable or invalid connection settings.

#### Scenario: Harness drives happy-path browser connectivity test
- **WHEN** Playwright runs against the webapp configured with harness-backed valid SQL Server settings
- **THEN** the browser test observes home-page connection status OK

#### Scenario: Harness drives failure-path browser connectivity test
- **WHEN** Playwright runs against the webapp with harness-backed invalid connectivity or missing-database settings
- **THEN** the browser test observes home-page connection status FAILED with failure detail
