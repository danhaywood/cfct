## Why

Automation clients can currently fetch a comparison JSON document, but they cannot tell which successful command the document was based on.
Including the selected command identity and timestamp lets automated consumers detect stale or racing results when commands continue to arrive while downloads or downstream processing are in flight.

## What Changes

- Extend the automation JSON download response so the top-level JSON payload includes a `command` object for the newest successful command used to derive the comparison.
- Include `command.interactionId` and `command.timestamp` fields in both populated and empty successful automation comparison JSON responses.
- Preserve the existing deterministic comparison JSON structure and error behavior aside from the added metadata fields.
- Keep the endpoint's existing overlapping-request guard, while giving clients enough metadata to perform their own race-condition checks across calls.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-automation-rest-api`: Successful automation comparison downloads report the selected latest successful command metadata.
- `json-comparison-file`: Deterministic JSON comparison output supports optional command metadata fields for automation-generated results.

## Impact

- Affects `GET /api/automation/comparison.json` response payloads and related automation service/controller tests.
- Affects JSON report serialization for webapp automation responses, including empty successful comparisons.
- May require approval fixture updates where automation JSON payloads are asserted.
- Does not change CLI inputs, authentication, endpoint path, or failure response contracts.
