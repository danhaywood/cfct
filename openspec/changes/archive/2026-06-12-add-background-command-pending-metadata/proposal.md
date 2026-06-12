## Why

Some foreground commands can enqueue background commands, and automation clients need a lightweight signal that related work may still be pending before they trust a comparison download as final.
CFCT cannot fully coordinate those asynchronous workflows, but it can expose whether command-audit data still contains pending background commands at the time the automation JSON is produced.

## What Changes

- Extend successful `GET /api/automation/comparison.json` responses with a top-level `backgroundCommands` object.
- Include `backgroundCommands.pending` as a numeric count of pending background commands visible in the command catalog at refresh time.
- Keep existing comparison content and `command` metadata unchanged.
- Preserve existing authentication, endpoint path, download headers, conflict handling, and error response behavior.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-automation-rest-api`: Successful automation comparison downloads report how many background commands are still pending.
- `json-comparison-file`: Automation-generated JSON comparison output supports optional background command status metadata.

## Impact

- Affects automation JSON response payloads and related automation service/controller tests.
- May require adding or extending command-catalog logic to count entries with `executeIn` indicating background execution and a pending replay state.
- Does not change CLI behavior, Vaadin UI behavior, authentication, or failure response contracts.
