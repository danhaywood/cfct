## 1. Webapp connectivity validation infrastructure

- [ ] 1.1 Add a webapp service that validates JDBC connectivity using configured server, credentials, and database names.
- [ ] 1.2 Add validation logic that checks configured left and right databases exist and are reachable.
- [ ] 1.3 Add clear error mapping/messages for unreachable server, authentication issues, and missing databases.
- [ ] 1.4 Wire connectivity validation into webapp startup flow with a profile/property approach as decided in implementation.

## 2. Testcontainers-backed validation coverage

- [ ] 2.1 Add webapp-focused SQL Server Testcontainers test support for connectivity validation scenarios.
- [ ] 2.2 Add positive test coverage for successful connectivity and existing left/right databases.
- [ ] 2.3 Add negative test coverage for missing configured database.
- [ ] 2.4 Add negative test coverage for connectivity failure (unreachable or invalid connection settings).

## 3. Scripts and documentation

- [ ] 3.1 Add or update helper scripts for running webapp connectivity-validation tests locally with Docker.
- [ ] 3.2 Update README with connectivity-validation behavior, failure expectations, and local test commands.
- [ ] 3.3 Document explicitly that Playwright/browser E2E is out of scope for this change.

## 4. Verification

- [ ] 4.1 Run webapp module tests including connectivity-validation coverage.
- [ ] 4.2 Run reactor tests for affected modules to ensure no regressions.
- [ ] 4.3 Verify webapp startup behavior for both valid and invalid connectivity settings in local/dev workflow.
