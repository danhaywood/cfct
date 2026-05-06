## ADDED Requirements

### Requirement: Playwright SQL Server fixture is exposed as an injected service
The Playwright SQL Server fixture support SHALL be implemented as a regular Spring-managed service/bean.
Playwright tests and test-support classes SHALL use dependency injection to access fixture operations.
Static fixture entry points for Playwright SQL Server setup SHALL not be required for scenario execution.

#### Scenario: Playwright test context injects fixture service
- **WHEN** Playwright integration tests start with Spring test context
- **THEN** fixture setup and scenario preparation are executed through an injected fixture bean

#### Scenario: Playwright scenarios run without static fixture calls
- **WHEN** connectivity-status Playwright tests execute deterministic harness-backed scenarios
- **THEN** fixture lifecycle and data setup complete without invoking static `PlaywrightSqlServerFixture` methods
