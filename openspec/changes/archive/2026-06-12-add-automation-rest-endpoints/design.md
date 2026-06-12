## Context

The Vaadin webapp already knows how to execute a multi-table comparison and render the latest result as JSON, YAML, and Excel for interactive users.
That workflow is session-oriented and requires a browser login, manual table selection, and UI download interaction.
Automation clients need a simpler contract that can be invoked by schedulers, deployment scripts, or monitoring jobs without driving the Vaadin UI.

The requested API has two operations.
One operation starts a comparison refresh.
The other operation downloads the latest JSON comparison produced by that automation refresh.
Both operations must be protected by HTTP Basic authentication.

## Goals / Non-Goals

**Goals:**

- Add a small REST surface dedicated to automation.
- Use HTTP Basic authentication for both automation endpoints.
- Reuse existing comparison execution and JSON rendering services rather than creating a separate comparison engine.
- Store the most recent automation comparison result in process memory for subsequent JSON download.
- Return predictable HTTP statuses and concise error payloads for automation clients.

**Non-Goals:**

- Replace the existing Vaadin login or UI comparison workflow.
- Add multi-user automation result history.
- Add long-term persistence for automation results.
- Add OAuth, API keys, or bearer-token authentication.
- Add YAML or Excel automation download endpoints in this change.

## Decisions

- Expose two endpoints under an automation namespace.
  - Proposed routes are `POST /api/automation/refresh` and `GET /api/automation/comparison.json`.
  - Rationale: the namespace keeps automation traffic separate from Vaadin routes and makes the two-operation contract explicit.
  - Alternative considered: reuse the Vaadin download URL.
  - Why not chosen: Vaadin resources are session/UI-oriented and are not a stable REST automation contract.

- Implement Basic authentication as an automation-specific guard.
  - Rationale: the requirement is limited to two endpoints and does not need to reshape the existing UI login model.
  - Alternative considered: introduce full Spring Security configuration for the application.
  - Why not chosen: it risks affecting Vaadin session behavior and is broader than the requested automation surface.

- Source automation database credentials from configuration and derive compared tables dynamically.
  - Rationale: automation clients should not send database passwords on every refresh request, and the Vaadin refresh flow already derives impacted tables from command-audit data.
  - Alternative considered: configure a static automation table list.
  - Why not chosen: it duplicates and can drift from the UI's command-driven table-selection behavior.

- Select the newest successful command for automation refresh and resolve its touched eligible business tables.
  - Rationale: this mirrors the UI refresh behavior that auto-selects the latest `OK` command, computes touched tables, and compares when eligible.
  - Alternative considered: accept selected command IDs in the refresh request.
  - Why not chosen: the requested automation contract has a single kick-off endpoint and should not require callers to understand the command catalog.

- Allow the refresh endpoint to run synchronously for the initial implementation.
  - Rationale: synchronous execution gives callers a deterministic completion signal and keeps result state simple.
  - Alternative considered: return `202 Accepted` and process comparison in the background.
  - Why not chosen: background execution needs job state, polling, cancellation, and concurrency rules that are not required by the initial two-endpoint request.

- Keep only the latest successful automation JSON result in memory.
  - Rationale: the download endpoint needs a current artifact, not a report history.
  - Alternative considered: write JSON to disk.
  - Why not chosen: disk persistence introduces file lifecycle, permissions, cleanup, and deployment concerns.

- Preserve the previous successful JSON if a later refresh fails.
  - Rationale: automation clients can still download the last known good artifact while the refresh response reports the new failure.
  - Alternative considered: clear the stored JSON on failure.
  - Why not chosen: clearing makes transient database failures more disruptive and loses useful diagnostic continuity.

## Risks / Trade-offs

- [Risk] In-memory result storage is lost on application restart.
  → Mitigation: the download endpoint returns `404 Not Found` when no successful automation result is available and clients can call refresh again.

- [Risk] Synchronous refresh can hold the HTTP request open for large comparisons.
  → Mitigation: document timeout expectations and keep asynchronous job handling as a future enhancement if needed.

- [Risk] A Basic Auth implementation could accidentally protect or disrupt Vaadin routes.
  → Mitigation: scope the authentication check to `/api/automation/**` only.

- [Risk] Concurrent refresh calls could race and produce confusing latest-result state.
  → Mitigation: serialize automation refresh execution or reject overlapping refreshes with `409 Conflict`.

## Migration Plan

No data migration is required.
New automation properties will be optional and disabled unless configured.
Existing UI users and CLI users should see no behavior change.
Rollback is a normal code revert and removal of any automation-specific properties.

## Open Questions

The initial design assumes automation refresh uses the newest successful command, matching the Vaadin refresh behavior.
If callers need per-request command selection later, that can be added as an optional refresh request body without changing the two-endpoint shape.
