# webapp-automation-rest-api Specification

## Purpose
TBD - created by archiving change add-automation-rest-endpoints. Update Purpose after archive.
## Requirements
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

### Requirement: Automation client can download latest JSON comparison
The webapp SHALL provide `GET /api/automation/comparison.json` as the automation client's single-call JSON comparison endpoint.
The endpoint SHALL run an automation comparison refresh before returning a response body.
The endpoint SHALL use configured automation connection inputs.
The endpoint SHALL derive compared tables from command-audit data using the same newest-successful-command and touched-eligible-business-table rules as the Vaadin refresh workflow.
The endpoint SHALL execute comparison through the same comparison orchestration and JSON report formatting used by the webapp comparison workflow when one or more eligible business tables are resolved.
The endpoint SHALL return the refreshed comparison JSON in the same HTTP response when comparison execution succeeds.
The endpoint SHALL return a successful empty comparison JSON response when the newest successful command resolves no eligible touched business tables.
The endpoint SHALL return `application/json` content.
The endpoint SHALL return a download-friendly filename in response headers for successful JSON downloads.
The endpoint SHALL return a concise error response when command discovery or comparison execution fails.
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
- **WHEN** an authenticated automation download succeeds with one or more eligible compared tables
- **THEN** the returned JSON payload uses the same deterministic JSON comparison format as existing webapp JSON downloads

#### Scenario: Download returns empty comparison for safe non-changing command
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** the newest successful command resolves no eligible touched business tables
- **THEN** the webapp responds with `200 OK`
- **AND** the response body has `hasDifferences` set to `false`
- **AND** the response body has `differingTables` set to an empty array
- **AND** the response body has `comparedTables` set to an empty array

#### Scenario: Download failure reports error
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** command discovery or comparison execution fails
- **THEN** the webapp responds with an error status and concise error payload

#### Scenario: Download without a successful command reports error
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** no newest successful command can be resolved
- **THEN** the webapp responds with an error status and concise error payload

#### Scenario: Concurrent download is rejected
- **WHEN** an automation refresh/download is already running
- **AND** another authenticated client calls `GET /api/automation/comparison.json`
- **THEN** the webapp rejects the second request with `409 Conflict`
- **AND** comparison state remains consistent

