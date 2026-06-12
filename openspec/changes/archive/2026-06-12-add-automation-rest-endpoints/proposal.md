## Why

Automation callers need a non-interactive way to run the webapp comparison workflow and retrieve deterministic JSON output.
The current Vaadin UI supports manual refresh and JSON download, but external schedulers and scripts need stable REST endpoints protected by simple credentials.

## What Changes

- Add a Basic-authenticated REST API for automation clients.
- Provide one endpoint that kicks off or refreshes a comparison run.
- Provide one endpoint that downloads the latest JSON comparison result produced by the automation run.
- Keep the existing Vaadin login, selection, comparison, and download behavior unchanged.
- Return clear HTTP statuses for authentication failure, no result available, in-progress runs, and comparison failures.

## Capabilities

### New Capabilities

- `webapp-automation-rest-api`: REST API for Basic-authenticated automation refresh and JSON comparison download.

### Modified Capabilities

- None.

## Impact

- Affects `cfct-webapp` by adding REST controller, automation authentication configuration, request/response contracts, and tests.
- Reuses existing comparison orchestration and JSON report formatting where possible.
- Requires configuration for automation Basic Auth credentials and default comparison inputs.
- Does not change existing core comparison APIs, report formats, CLI behavior, or Vaadin UI workflows.
