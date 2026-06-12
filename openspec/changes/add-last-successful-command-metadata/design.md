## Context

The automation REST endpoint currently performs a fresh comparison refresh for each authenticated `GET /api/automation/comparison.json` call.
During refresh, `AutomationComparisonService` discovers the command catalog, selects the newest command with replay state `OK`, resolves touched business tables for that interaction, and returns the JSON produced by the existing comparison formatter.
The service already rejects overlapping refreshes with a `409 Conflict`, but an automation client still cannot verify that the JSON it received corresponds to the command it expected after additional commands arrive.

The command catalog entries already expose `interactionId` and `timestamp`, so the change should propagate the selected `CommandCatalogEntry` metadata through the automation result and inject it into successful JSON responses.

## Goals / Non-Goals

**Goals:**

- Include the selected newest successful command metadata as `command.interactionId` and `command.timestamp` in every successful automation JSON response.
- Preserve existing comparison details, stable ordering, filename behavior, authentication, and failure handling.
- Ensure empty successful comparisons include the same command metadata as populated comparisons.
- Keep the implementation testable without adding new infrastructure or external dependencies.

**Non-Goals:**

- Do not change the selection rule for the newest successful command.
- Do not add optimistic concurrency parameters, request preconditions, or client-provided expected command IDs.
- Do not alter CLI output unless it already uses the same metadata-aware rendering path by explicit design.
- Do not change error payloads, Basic authentication, or the endpoint path.

## Decisions

- Carry command metadata as part of the automation result rather than deriving it in the controller.
  The service owns command discovery and selection, so returning command metadata from `AutomationComparisonService` keeps the controller focused on HTTP concerns.
  The alternative was to have the controller inspect the command catalog separately, but that could select a different command and reintroduce the race condition this change is meant to guard against.

- Select and retain the full newest successful `CommandCatalogEntry` before resolving tables.
  The existing helper returns only an interaction ID, which is enough for table resolution but loses timestamp metadata.
  Returning the entry keeps the selected identifier and timestamp from the same catalog row.

- Inject metadata into a top-level `command` JSON object after comparison formatting.
  Reusing the existing formatter preserves all detailed comparison behavior and avoids broad model changes.
  The implementation should parse the formatted JSON as an object, add a stable `command` object with `interactionId` and `timestamp` fields, and serialize it with the project's existing JSON configuration or deterministic formatting style.
  The alternative was to add metadata to the core comparison model, but that would broaden the scope into non-automation comparison outputs.

- Use stable nested field names `command.interactionId` and `command.timestamp`.
  These names group command audit metadata without crowding the comparison result's top-level namespace.
  The timestamp value should be the selected command timestamp exactly as represented by the command catalog entry, not the refresh completion time.

## Risks / Trade-offs

- JSON reserialization may change whitespace in automation responses.
  Mitigation: assert semantic JSON content in controller tests and update only automation-specific expectations where byte-level formatting is not part of the contract.

- Core comparison output tests could be affected if metadata injection is placed in the shared formatter.
  Mitigation: keep injection in the automation response path unless a narrow formatter extension is introduced with explicit tests.

- Command timestamps are currently represented as strings in `CommandCatalogEntry`.
  Mitigation: pass the selected catalog timestamp through unchanged to avoid timezone or formatting regressions.

- Empty comparison JSON must not omit metadata.
  Mitigation: build empty successful responses through the same metadata injection path used for populated comparisons.

## Migration Plan

No data migration is required.
Deploying the change adds a top-level `command` JSON object to successful automation responses.
Existing clients that ignore unknown JSON fields continue to work, while race-aware clients can start validating `command.interactionId` and `command.timestamp`.
Rollback removes those fields and returns to the previous successful response shape.

## Open Questions

None.
