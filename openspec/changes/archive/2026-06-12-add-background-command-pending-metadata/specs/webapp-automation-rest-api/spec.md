## MODIFIED Requirements

### Requirement: Automation client can download latest JSON comparison
The webapp SHALL provide `GET /api/automation/comparison.json` as the automation client's single-call JSON comparison endpoint.
The endpoint SHALL run an automation comparison refresh before returning a response body.
The endpoint SHALL use configured automation connection inputs.
The endpoint SHALL derive compared tables from command-audit data using the same newest-successful-command and touched-eligible-business-table rules as the Vaadin refresh workflow.
The endpoint SHALL execute comparison through the same comparison orchestration and JSON report formatting used by the webapp comparison workflow when one or more eligible business tables are resolved.
The endpoint SHALL return the refreshed comparison JSON in the same HTTP response when comparison execution succeeds.
The endpoint SHALL return a successful empty comparison JSON response when the newest successful command resolves no eligible touched business tables.
The endpoint SHALL include a top-level `command` JSON object in every successful comparison response.
The `command` object SHALL include `interactionId` and `timestamp` fields for the selected newest successful command.
The endpoint SHALL populate `command.interactionId` and `command.timestamp` from the same command catalog entry used to resolve touched business tables.
The endpoint SHALL include a top-level `backgroundCommands` JSON object in every successful comparison response.
The `backgroundCommands` object SHALL include a numeric `pending` field.
The endpoint SHALL populate `backgroundCommands.pending` with the count of visible command catalog entries whose execution mode is background and whose replay state is pending.
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
- **AND** the response body includes `command.interactionId` and `command.timestamp` fields for the selected newest successful command
- **AND** the response body includes a numeric `backgroundCommands.pending` field
- **AND** the response content type is `application/json`
- **AND** the response includes a download-friendly filename

#### Scenario: Download derives tables from newest successful command
- **WHEN** an authenticated automation download starts
- **THEN** the webapp discovers the command catalog
- **AND** selects the newest command whose replay state is `OK`
- **AND** resolves touched eligible business tables from that command before comparing

#### Scenario: Download reports selected command metadata
- **WHEN** an authenticated automation download succeeds
- **THEN** the returned JSON payload includes `command.interactionId` equal to the newest successful command interaction identifier
- **AND** the returned JSON payload includes `command.timestamp` equal to that same command's command timestamp

#### Scenario: Download reports pending background command count
- **WHEN** an authenticated automation download succeeds
- **AND** the command catalog snapshot contains pending background commands
- **THEN** the returned JSON payload includes `backgroundCommands.pending` equal to the number of pending background command entries

#### Scenario: Download reports zero pending background commands
- **WHEN** an authenticated automation download succeeds
- **AND** the command catalog snapshot contains no pending background commands
- **THEN** the returned JSON payload includes `backgroundCommands.pending` set to `0`

#### Scenario: Download uses existing comparison formatting
- **WHEN** an authenticated automation download succeeds with one or more eligible compared tables
- **THEN** the returned JSON payload uses the same deterministic JSON comparison format as existing webapp JSON downloads
- **AND** the returned JSON payload includes the selected command metadata fields
- **AND** the returned JSON payload includes the background command status fields

#### Scenario: Download returns empty comparison for safe non-changing command
- **WHEN** an authenticated automation client calls `GET /api/automation/comparison.json`
- **AND** the newest successful command resolves no eligible touched business tables
- **THEN** the webapp responds with `200 OK`
- **AND** the response body has `hasDifferences` set to `false`
- **AND** the response body has `differingTables` set to an empty array
- **AND** the response body has `comparedTables` set to an empty array
- **AND** the response body includes `command.interactionId` and `command.timestamp` fields for the selected newest successful command
- **AND** the response body includes a numeric `backgroundCommands.pending` field

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
