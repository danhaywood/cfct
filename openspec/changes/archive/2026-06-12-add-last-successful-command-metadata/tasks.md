## 1. Automation Metadata Flow

- [x] 1.1 Update `AutomationComparisonService` to select and retain the newest successful `CommandCatalogEntry` instead of only its interaction ID.
- [x] 1.2 Extend `LatestAutomationResult` to expose selected command metadata alongside the JSON, completion time, and table count.
- [x] 1.3 Ensure both populated and empty successful automation refresh results carry command metadata from the same entry used for touched-table resolution.

## 2. JSON Response Shape

- [x] 2.1 Add a metadata injection path that adds a top-level `command` object with `interactionId` and `timestamp` fields to successful automation JSON payloads without changing failure payloads.
- [x] 2.2 Preserve existing comparison JSON fields and deterministic ordering when metadata is added.
- [x] 2.3 Keep the automation controller response headers, filename behavior, content type, and conflict handling unchanged.

## 3. Tests and Fixtures

- [x] 3.1 Update `AutomationComparisonServiceTest` to assert the selected newest successful command metadata for populated comparisons.
- [x] 3.2 Update empty-comparison automation tests to assert `command.interactionId` and `command.timestamp` are included when no eligible touched tables resolve.
- [x] 3.3 Update `AutomationControllerTest` to assert successful downloads include the new nested command metadata fields.
- [x] 3.4 Add or update JSON serialization tests to verify metadata injection preserves comparison content and does not affect error responses.

## 4. Verification

- [x] 4.1 Run the relevant webapp automation test suite.
- [x] 4.2 Run the broader Maven test set needed to validate JSON comparison output and automation endpoint behavior.
- [x] 4.3 Run `openspec status --change "add-last-successful-command-metadata"` and confirm the change is apply-ready.
