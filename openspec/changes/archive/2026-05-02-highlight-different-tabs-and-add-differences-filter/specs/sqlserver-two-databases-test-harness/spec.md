## MODIFIED Requirements

### Requirement: Harness supports browser-level connectivity-status testing
The SQL Server harness SHALL provide deterministic connection settings consumable by headless Playwright tests.
The harness SHALL support a happy-path setup where both configured logical databases exist.
The harness SHALL support failure-path setups for missing databases and unreachable or invalid connection settings.
The harness SHALL include a deterministic comparable `dbo.CustomerAddress` business table fixture with a `_PK` unique index in both logical databases.
The harness SHALL seed `dbo.CustomerAddress` with identical left and right rows so unchanged-table comparison behavior is testable.

#### Scenario: Harness drives happy-path browser connectivity test
- **WHEN** Playwright runs against the webapp configured with harness-backed valid SQL Server settings
- **THEN** the browser test observes home-page connection status OK

#### Scenario: Harness drives failure-path browser connectivity test
- **WHEN** Playwright runs against the webapp with harness-backed invalid connectivity or missing-database settings
- **THEN** the browser test observes home-page connection status FAILED with failure detail

#### Scenario: Harness provides unchanged comparable CustomerAddress fixture
- **WHEN** happy-path browser fixture setup initializes left and right logical databases
- **THEN** both databases contain `dbo.CustomerAddress` with matching schema, `_PK` business-key index, and identical seeded rows
