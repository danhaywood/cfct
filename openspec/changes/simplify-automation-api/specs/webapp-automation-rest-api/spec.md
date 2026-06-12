## MODIFIED Requirements

### Requirement: Automation client can download latest JSON comparison
The webapp SHALL provide `GET /api/automation/comparison.json` as the automation client's single-call JSON comparison endpoint.
The endpoint SHALL run an automation comparison refresh before returning a response body.
The endpoint SHALL use configured automation connection inputs.
The endpoint SHALL derive compared tables from command-audit data using the same newest-successful-command and touched-eligible-business-table rules as the Vaadin refresh workflow.
The endpoint SHALL execute comparison through the same comparison orchestration and JSON report formatting used by the webapp comparison workflow.
The endpoint SHALL return the refreshed comparison JSON in the same HTTP response when comparison execution succeeds.
The endpoint SHALL return `application/json` content.
The endpoint SHALL return a download-friendly filename in response headers for successful JSON downloads.
The endpoint SHALL return a concise error response when command discovery, touched-table resolution, or comparison execution fails.
The endpoint SHALL prevent overlapping automation refresh/download requests from corrupting comparison state.

#### Scenario: Download refreshes and returns JSON
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** the command catalog contains a newest successful command that touches eligible business tables
- **AND** comparison execution succeeds
- **THEN** the webapp responds with `200 OK`
- **AND** the response body is the refreshed JSON comparison result
- **AND** the response content type is `application/json`
- **AND** the response includes a download-friendly filename

#### Scenario: Download derives tables from newest successful command
- **WHEN** an authenticated automation download starts
- **THEN** the webapp discovers the command catalog
- **AND** selects the newest command whose replay state is `OK`
- **AND** resolves touched eligible business tables from that command before comparing

#### Scenario: Download uses existing comparison formatting
- **WHEN** an authenticated automation download succeeds
- **THEN** the returned JSON payload uses the same deterministic JSON comparison format as existing webapp JSON downloads

#### Scenario: Download failure reports error
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** command discovery, touched-table resolution, or comparison execution fails
- **THEN** the webapp responds with an error status and concise error payload

#### Scenario: Download without impacted eligible tables reports error
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** no newest successful command or no touched eligible business table can be resolved
- **THEN** the webapp responds with an error status and concise error payload

#### Scenario: Concurrent download is rejected
- **WHEN** an automation refresh/download is already running
- **AND** another authenticated client calls `GET /api/automation/comparison.json`
- **THEN** the webapp rejects the second request with `409 Conflict`
- **AND** comparison state remains consistent

## REMOVED Requirements

### Requirement: Automation client can refresh comparison JSON
**Reason**: The separate refresh endpoint creates a stateful two-step workflow where downloads depend on an in-memory latest-result cache that may be empty or stale.
**Migration**: Automation clients MUST call `GET /api/automation/comparison.json` directly; that endpoint refreshes and returns the comparison JSON in one response.

The webapp SHALL provide `POST /api/automation/refresh` to run a comparison refresh for automation clients.
The refresh endpoint SHALL use configured automation connection inputs.
The refresh endpoint SHALL derive compared tables from command-audit data using the same newest-successful-command and touched-eligible-business-table rules as the Vaadin refresh workflow.
The refresh endpoint SHALL execute comparison through the same comparison orchestration and JSON report formatting used by the webapp comparison workflow.
The refresh endpoint SHALL store the latest successful JSON comparison result for later download.
The refresh endpoint SHALL return a success response only after the refreshed comparison result is available for download.
The refresh endpoint SHALL return a concise error response when comparison execution fails.
The refresh endpoint SHALL prevent overlapping automation refreshes from corrupting latest-result state.

#### Scenario: Refresh stores latest JSON result
- **WHEN** an authenticated automation client posts to `/api/automation/refresh`
- **AND** the command catalog contains a newest successful command that touches eligible business tables
- **AND** comparison execution succeeds
- **THEN** the webapp stores the refreshed JSON comparison result
- **AND** the response indicates that the refresh completed successfully

#### Scenario: Refresh derives tables from newest successful command
- **WHEN** an authenticated automation refresh starts
- **THEN** the webapp discovers the command catalog
- **AND** selects the newest command whose replay state is `OK`
- **AND** resolves touched eligible business tables from that command before comparing

#### Scenario: Refresh uses existing comparison formatting
- **WHEN** an authenticated automation refresh succeeds
- **THEN** the stored JSON payload uses the same deterministic JSON comparison format as existing webapp JSON downloads

#### Scenario: Refresh failure reports error
- **WHEN** an authenticated automation client posts to `/api/automation/refresh`
- **AND** command discovery, touched-table resolution, or comparison execution fails
- **THEN** the webapp responds with an error status and concise error payload
- **AND** any previously successful JSON result remains available for download

#### Scenario: Refresh without impacted eligible tables reports error
- **WHEN** an authenticated automation client posts to `/api/automation/refresh`
- **AND** no newest successful command or no touched eligible business table can be resolved
- **THEN** the webapp responds with an error status and concise error payload
- **AND** no new JSON result is stored

#### Scenario: Concurrent refresh is rejected or serialized
- **WHEN** an automation refresh is already running
- **AND** another authenticated client posts to `/api/automation/refresh`
- **THEN** the webapp either serializes the second refresh or rejects it with `409 Conflict`
- **AND** latest-result state remains consistent
