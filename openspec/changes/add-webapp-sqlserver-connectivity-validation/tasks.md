## 1. Webapp connectivity validation infrastructure

- [x] 1.1 Add a webapp service that validates JDBC connectivity using configured server, credentials, and database names.
- [x] 1.2 Add validation logic that checks configured left and right databases exist and are reachable.
- [x] 1.3 Add clear error mapping/messages for unreachable server, authentication issues, and missing databases.
- [x] 1.4 Wire connectivity validation into webapp startup flow with a profile/property approach as decided in implementation.

## 2. Testcontainers-backed validation coverage

- [x] 2.1 Add webapp-focused SQL Server Testcontainers test support for connectivity validation scenarios.
- [x] 2.2 Add positive test coverage for successful connectivity and existing left/right databases.
- [x] 2.3 Add negative test coverage for missing configured database.
- [x] 2.4 Add negative test coverage for connectivity failure (unreachable or invalid connection settings).

## 3. Scripts and documentation

- [x] 3.1 Add or update helper scripts for running webapp connectivity-validation tests locally with Docker.
- [x] 3.2 Update README with connectivity-validation behavior, failure expectations, and local test commands.
- [x] 3.3 Document explicitly that Playwright/browser E2E is out of scope for this change.

## 4. Verification

- [x] 4.1 Run webapp module tests including connectivity-validation coverage.
- [x] 4.2 Run reactor tests for affected modules to ensure no regressions.
- [x] 4.3 Verify webapp startup behavior for both valid and invalid connectivity settings in local/dev workflow.
