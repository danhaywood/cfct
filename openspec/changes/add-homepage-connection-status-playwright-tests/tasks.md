## 1. Connection-status state and home-page rendering

- [ ] 1.1 Add a UI-facing connection-status model that captures OK or FAILED plus optional failure summary from validation outcomes.
- [ ] 1.2 Wire startup validation outcome into the connection-status model without duplicating validator logic.
- [ ] 1.3 Update the home page to render a deterministic connection-status block with explicit OK and FAILED states.
- [ ] 1.4 Add unit tests for home-page status rendering and validation-outcome mapping behavior.

## 2. Runtime behavior and configuration modes

- [ ] 2.1 Preserve fail-fast startup as the default runtime behavior for invalid SQL connectivity.
- [ ] 2.2 Add a test-focused override mode that allows startup with captured FAILED status for browser assertions.
- [ ] 2.3 Add tests validating default fail-fast behavior and test-mode behavior.

## 3. Headless Playwright and Testcontainers integration

- [ ] 3.1 Add Playwright test infrastructure for the webapp module with headless execution defaults.
- [ ] 3.2 Add a happy-path Playwright test that verifies home-page connection status OK using Testcontainers SQL Server.
- [ ] 3.3 Add a failure-path Playwright test that verifies home-page connection status FAILED and failure summary.
- [ ] 3.4 Ensure Playwright scenarios consume Testcontainers-provided SQL Server settings and remain reproducible in CI.

## 4. Scripts, documentation, and verification

- [ ] 4.1 Add or update helper scripts and Maven commands for running headless Playwright connectivity-status tests locally.
- [ ] 4.2 Update README with connection-status behavior, Playwright headless usage, and Testcontainers prerequisites.
- [ ] 4.3 Run webapp unit and integration tests plus Playwright connectivity-status tests and record verification commands.
