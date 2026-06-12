## Context

The webapp currently exposes an automation REST API with Basic authentication under `/api/automation`.
Automation clients call `POST /api/automation/refresh` to populate an in-memory latest-result cache, then call `GET /api/automation/comparison.json` to download that cached JSON.
The UI refresh workflow can show differences while the automation download returns no payload when the latest-result cache is empty or stale.
The comparison execution, table discovery, JSON rendering, and concurrency guard already exist in `AutomationComparisonService` and should be reused.

## Goals / Non-Goals

**Goals:**

- Make `GET /api/automation/comparison.json` perform the refresh and return the fresh JSON result in a single request.
- Remove the need for automation clients to understand or depend on server-side latest-result state.
- Preserve Basic authentication and automation-specific connection configuration.
- Preserve existing newest-successful-command and touched-business-table discovery rules.
- Preserve deterministic JSON output and download-friendly response headers.
- Keep concurrency behavior explicit when a refresh/download is already running.

**Non-Goals:**

- Do not change the Vaadin UI refresh workflow.
- Do not change the JSON comparison schema.
- Do not add asynchronous job polling or persisted automation result storage.
- Do not change the automation credential configuration model.

## Decisions

### Decision: Make the download endpoint refresh synchronously

`GET /api/automation/comparison.json` will call the same refresh execution path currently used by `POST /api/automation/refresh`, then stream the resulting JSON from that execution.
This removes ambiguity about whether a cached latest result exists and makes each automation download self-contained.

Alternative considered: keep both endpoints and have clients retry the download after refresh.
That keeps the stateful failure mode that prompted this change, so it is rejected.

### Decision: Retain a small result record for response metadata only

The service can continue returning a result record containing JSON, completion timestamp, and table count, because the controller needs JSON bytes and a filename.
The record should represent the current request outcome rather than a required pre-existing latest-result cache.

Alternative considered: return raw JSON directly from the service.
That would duplicate timestamp and filename construction in the controller or discard useful download metadata.

### Decision: Treat the old refresh endpoint as removed from the automation contract

The implementation should remove the controller mapping and README instructions for `POST /api/automation/refresh` unless a short compatibility period is explicitly chosen during implementation.
The spec marks this as a breaking API simplification so automation clients migrate to a single call.

Alternative considered: keep `POST /api/automation/refresh` as an undocumented alias.
That reduces breakage but preserves an API surface the change is intended to eliminate.

### Decision: Preserve the existing concurrency guard

The existing `AtomicBoolean` refresh guard should continue to prevent overlapping refresh/download executions from corrupting or overloading comparison state.
When another refresh/download is in progress, the endpoint should return `409 Conflict` with a concise JSON error payload.

Alternative considered: serialize concurrent requests.
Serialization may make clients wait behind long-running comparisons and is not necessary for this expedient simplification.

## Risks / Trade-offs

- Breaking existing automation clients that call `POST /api/automation/refresh` first → Update README and tests to document `GET /api/automation/comparison.json` as the only supported automation workflow.
- GET now has side effects and may take longer than a normal download → Document that this automation endpoint is an authenticated command-style download endpoint that refreshes before returning content.
- Refresh failures now prevent a download response even if an older successful result exists → This is intentional because clients asked for the fresh comparison for the current state rather than unknown cached state.
- Long-running comparisons can trigger client or proxy timeouts → Keep the existing synchronous behavior and concise failure responses, and avoid adding asynchronous job management in this change.
