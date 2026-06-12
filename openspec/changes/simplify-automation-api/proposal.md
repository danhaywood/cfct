## Why

Automation clients currently need to call one endpoint to refresh and a second endpoint to download `comparison.json`.
This split makes the download result depend on opaque latest-result state, which can return nothing even when the UI refresh path shows differences.

## What Changes

- Replace the two-step automation workflow with one authenticated download endpoint that refreshes comparison data and returns the resulting `comparison.json` in the same response.
- **BREAKING**: Remove or deprecate the separate `POST /api/automation/refresh` stateful refresh workflow for automation clients.
- **BREAKING**: Change `GET /api/automation/comparison.json` so it triggers the automation refresh before streaming the JSON body instead of returning only previously stored state.
- Preserve existing automation Basic authentication, automation connection settings, command-audit table discovery rules, JSON formatting, and concurrency protection.
- Update documentation and tests to describe the single-call automation workflow.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-automation-rest-api`: Simplify the automation REST API so a single authenticated request refreshes comparison data and downloads the fresh JSON result.

## Impact

- Affects `cfct-webapp` automation controller, comparison service, authentication tests, controller tests, and service tests.
- Affects README automation API instructions and any consumers using `POST /api/automation/refresh` followed by `GET /api/automation/comparison.json`.
- Does not require new dependencies or changes to the JSON comparison file format.
