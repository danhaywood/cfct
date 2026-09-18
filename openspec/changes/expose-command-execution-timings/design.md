## Context

CFCT already selects the foreground command used by an automation comparison and independently discovers app-a and app-b background descendants.
The command log stores `logicalMemberIdentifier`, `startedAt`, `completedAt`, replay status, interaction identifier, parent interaction identifier, and target bookmark.
Nominal command timestamps can jump between replayed commands because the application clock is advanced to the recording timeline, while elapsed time between `startedAt` and `completedAt` remains realistic.
Aregress needs raw timing observations for durable performance reports, but performance movement must remain independent from CFCT's business-table and audit outcomes.

## Goals / Non-Goals

**Goals:**

- Expose per-side foreground and terminal-background timing observations for each settled automation comparison.
- Derive realistic command duration from each observation's own start and completion timestamps.
- Preserve enough side-local identity for polling clients to deduplicate observations.
- Keep the payload deterministic, additive, target-free, and independent from pass/fail semantics.

**Non-Goals:**

- Do not rank, aggregate, graph, or classify performance movement.
- Do not correlate app-a and app-b background interaction identifiers.
- Do not correlate observations by target bookmark.
- Do not calculate suite elapsed time from nominal command timestamps.
- Do not change business-table, audit, pending-background, or failed-background outcomes.

## Decisions

### Add a separate execution-timing result

Add a top-level `executionTiming` object rather than embedding timings into business or audit comparison models.
The object contains `foreground.appA`, `foreground.appB`, `background.appA[]`, and `background.appB[]` observations.
This follows the existing separation between business comparison and audit comparison and allows timing consumers to evolve independently.

### Expose raw observations rather than pre-aggregated comparison metrics

Each observation contains side-local interaction identity, parent identity where applicable, logical member identifier, replay status, start time, completion time, and nullable duration milliseconds.
CFCT does not calculate percentage change, median, totals, or regression status.
Aregress and offline report tooling can therefore aggregate one or many suites without losing individual samples.

### Calculate duration from startedAt and completedAt

When both timestamps exist and completion is not before start, `durationMillis` is their elapsed duration.
When either timestamp is absent or the interval is invalid, `durationMillis` is absent rather than zero or clamped.
Nominal command `timestamp` is not used in duration calculation because replay can advance the application clock between commands.

### Discover each side independently

The foreground observation is read from each side for the selected foreground interaction identifier.
Background observations are read from each side's own terminal descendant set and are not paired by child interaction identifier.
Side-local identifiers remain in the payload only so clients can deduplicate repeated polling results.
Consumers correlate and aggregate performance by logical member identifier.

### Exclude target data at the source

Timing DTOs contain no target bookmark or target object identifier.
This prevents generated timing artifacts and reports from accidentally exposing business object identities.
The logical member identifier remains sufficient for performance aggregation.

### Use deterministic ordering

Background observations are ordered by logical member identifier, start time, and side-local interaction identifier with null-safe ordering.
Deterministic output supports stable approval tests and reproducible downstream artifacts.

### Reuse the selected foreground and background scope

Timing retrieval uses the same foreground command and side-specific descendant sets already established for the settled comparison.
It does not perform a second latest-command selection and cannot silently report a different command scope.

## Risks / Trade-offs

- [Risk] Missing or invalid timestamps produce incomplete observations. → Preserve the observation and status with an absent duration so downstream tools can report incomplete data explicitly.
- [Risk] Adding interaction identifiers could tempt consumers to pair background commands across sides. → Document them as side-local deduplication keys and expose no cross-side match structure.
- [Risk] Additional command-log queries increase automation refresh time. → Query only the selected foreground and known terminal descendant identifiers and reuse existing authenticated data-source contexts.
- [Risk] Large descendant sets increase response size. → Keep timing DTOs compact and omit target, command DTO, result, and exception payloads.
- [Risk] Older consumers do not understand `executionTiming`. → Keep the field additive and preserve all existing fields and semantics.

## Migration Plan

1. Add API timing model types and the timing-reader SPI.
2. Implement SQL Server timing reads against both configured data sources.
3. Compose timing observations from the already selected foreground and descendant scopes.
4. Add `executionTiming` to successful automation JSON responses.
5. Add unit, integration, controller, and deterministic JSON coverage.
6. Deploy CFCT before enabling Aregress timing capture; older Aregress versions ignore the additive field.
7. Roll back by deploying the previous CFCT version because no database migration or persistent state changes are introduced.

## Open Questions

None.
