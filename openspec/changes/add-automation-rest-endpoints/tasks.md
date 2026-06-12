## 1. Automation configuration and authentication

- [ ] 1.1 Add typed webapp configuration properties for enabling automation, Basic Auth username, and Basic Auth password.
- [ ] 1.2 Add typed webapp configuration properties for automation comparison inputs, including source database, target database, and selected tables or table-selection strategy.
- [ ] 1.3 Implement an automation Basic Auth guard scoped only to `/api/automation/**`.
- [ ] 1.4 Ensure missing or invalid Basic credentials return `401 Unauthorized` with a Basic authentication challenge.

## 2. Automation comparison state and execution

- [ ] 2.1 Add an automation comparison service that builds the comparison request from automation configuration.
- [ ] 2.2 Reuse existing comparison orchestration and JSON report formatting for automation refresh execution.
- [ ] 2.3 Store the latest successful automation JSON result in process memory with enough metadata for download headers or status responses.
- [ ] 2.4 Preserve the previous successful JSON result when a later automation refresh fails.
- [ ] 2.5 Prevent overlapping refreshes from corrupting latest-result state by serializing execution or returning `409 Conflict`.

## 3. REST endpoints

- [ ] 3.1 Add `POST /api/automation/refresh` for authenticated automation refresh execution.
- [ ] 3.2 Return a concise success response from refresh after the JSON result is available.
- [ ] 3.3 Return concise error payloads and appropriate HTTP statuses for refresh failures and overlapping refresh attempts.
- [ ] 3.4 Add `GET /api/automation/comparison.json` for authenticated latest JSON download.
- [ ] 3.5 Return `application/json`, a download-friendly filename, and `404 Not Found` when no successful result is available.

## 4. Tests and documentation

- [ ] 4.1 Add controller or integration tests for missing, invalid, and valid Basic credentials.
- [ ] 4.2 Add tests proving refresh stores deterministic JSON from existing formatter output.
- [ ] 4.3 Add tests proving download returns the latest stored JSON and does not trigger a refresh.
- [ ] 4.4 Add tests for no-result download, refresh failure behavior, and overlapping refresh behavior.
- [ ] 4.5 Document automation endpoint URLs, Basic Auth configuration, refresh behavior, and JSON download behavior.

## 5. Validation

- [ ] 5.1 Run relevant `cfct-webapp` unit and integration tests for the automation REST API.
- [ ] 5.2 Run relevant comparison/report formatter tests affected by automation reuse.
- [ ] 5.3 Run `openspec validate add-automation-rest-endpoints --strict` and resolve any proposal/spec/task issues.
