## ADDED Requirements

### Requirement: Automation API is protected by Basic authentication
The webapp SHALL protect automation REST endpoints with HTTP Basic authentication.
The webapp SHALL validate Basic credentials against automation-specific configuration.
The webapp SHALL reject missing or invalid automation credentials with `401 Unauthorized`.
The webapp SHALL include a `WWW-Authenticate` challenge for Basic authentication failures.
The webapp SHALL NOT require a Vaadin UI session or Vaadin login state for successful automation API access.
The webapp SHALL scope automation Basic authentication to automation endpoints only.

#### Scenario: Missing automation credentials are rejected
- **WHEN** a client calls an automation endpoint without an `Authorization` header
- **THEN** the webapp responds with `401 Unauthorized`
- **AND** the response includes a Basic authentication challenge

#### Scenario: Invalid automation credentials are rejected
- **WHEN** a client calls an automation endpoint with invalid Basic credentials
- **THEN** the webapp responds with `401 Unauthorized`

#### Scenario: Valid automation credentials allow endpoint access
- **WHEN** a client calls an automation endpoint with configured valid Basic credentials
- **THEN** the webapp allows the automation endpoint handler to process the request

#### Scenario: UI authentication remains separate
- **WHEN** a client authenticates to the automation API with valid Basic credentials
- **THEN** the webapp does not create or require a Vaadin authenticated UI session

### Requirement: Automation client can refresh comparison JSON
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

### Requirement: Automation client can download latest JSON comparison
The webapp SHALL provide `GET /api/automation/comparison.json` to download the latest successful automation comparison JSON.
The download endpoint SHALL return `application/json` content.
The download endpoint SHALL return a download-friendly filename in response headers.
The download endpoint SHALL return `404 Not Found` when no successful automation comparison result is available.
The download endpoint SHALL NOT trigger a comparison refresh.

#### Scenario: Download returns latest JSON
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** a successful automation refresh has completed
- **THEN** the webapp responds with `200 OK`
- **AND** the response body is the latest stored JSON comparison result
- **AND** the response content type is `application/json`

#### Scenario: Download before refresh returns not found
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** no successful automation refresh has completed since application startup
- **THEN** the webapp responds with `404 Not Found`

#### Scenario: Download does not refresh comparison
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **THEN** the webapp returns the stored result without starting comparison execution
