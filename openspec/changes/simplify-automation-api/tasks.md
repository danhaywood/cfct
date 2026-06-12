## 1. Service and Controller Behavior

- [x] 1.1 Update `AutomationComparisonService` so the public automation execution method returns the freshly generated JSON result for the current request without requiring callers to read previously cached state.
- [x] 1.2 Preserve the existing automation enabled check, connection context creation, dynamic table resolution, JSON comparison execution, completion timestamp, filename metadata, and table count metadata.
- [x] 1.3 Preserve the existing concurrency guard so an overlapping automation refresh/download returns a conflict result instead of corrupting state.
- [x] 1.4 Update `AutomationController` so `GET /api/automation/comparison.json` triggers the refresh/download execution and returns the JSON bytes with `application/json` and attachment filename headers.
- [x] 1.5 Remove the `POST /api/automation/refresh` mapping and any controller response types that exist only for the separate refresh endpoint.
- [x] 1.6 Ensure disabled automation, execution failures, and concurrent requests return concise JSON error payloads with the expected HTTP statuses.

## 2. Tests

- [x] 2.1 Update `AutomationControllerTest` so Basic-auth success, failure, and conflict coverage targets `GET /api/automation/comparison.json` as the single automation endpoint.
- [x] 2.2 Replace controller tests for download-before-refresh and non-refreshing downloads with tests proving the GET endpoint invokes refresh and streams the fresh JSON response.
- [x] 2.3 Update `AutomationComparisonServiceTest` to assert the service returns the current refresh result and no longer requires a latest-result lookup API for successful downloads.
- [x] 2.4 Add or adjust tests for failure cases where command discovery, touched-table resolution, or comparison execution fail during the single GET workflow.
- [x] 2.5 Add or adjust tests confirming overlapping automation GET requests are rejected with `409 Conflict` and do not corrupt comparison state.

## 3. Documentation and Validation

- [x] 3.1 Update README automation API examples to show a single authenticated `GET /api/automation/comparison.json` call that refreshes and downloads the JSON.
- [x] 3.2 Remove README instructions that require `POST /api/automation/refresh` before downloading `comparison.json`.
- [x] 3.3 Run the affected `cfct-webapp` automation tests and fix regressions.
- [x] 3.4 Run the relevant project validation command or module test suite before marking the change complete.
