## 1. Maven module restructuring

- [x] 1.1 Add a new Playwright page-object module to the root reactor modules list.
- [x] 1.2 Create the new module POM and source layout for reusable page-object classes.
- [x] 1.3 Update integration-test module dependencies to consume the new page-object module.
- [x] 1.4 Verify dependency direction keeps page-object support scoped to browser-test usage.

## 2. Page-object extraction and test refactor

- [x] 2.1 Identify shared selector and interaction patterns in existing Playwright connectivity-status tests.
- [x] 2.2 Implement page-object classes for app shell, footer status, table selection, and compare results workflows.
- [x] 2.3 Refactor Playwright tests to call page-object methods while preserving existing assertions and scenarios.
- [x] 2.4 Remove redundant direct selector choreography from refactored test classes.

## 3. Fixture service conversion

- [x] 3.1 Refactor `PlaywrightSqlServerFixture` from static utility methods into a Spring-managed service/bean.
- [x] 3.2 Update test configuration and consumers to inject the fixture bean through dependency injection.
- [x] 3.3 Remove static fixture call sites from Playwright tests and supporting helpers.
- [x] 3.4 Confirm fixture lifecycle still provides deterministic left/right SQL Server setup for all scenarios.

## 4. Validation and cleanup

- [x] 4.1 Run module-focused tests to validate new page-object module compilation and dependency wiring.
- [x] 4.2 Run Playwright connectivity-status integration tests to confirm happy-path and failure-path behavior remains intact.
- [x] 4.3 Run full `mvn test` and `mvn verify` from repository root in a Docker-enabled environment.
- [x] 4.4 Update any developer documentation that references old static fixture usage or prior module locations.
