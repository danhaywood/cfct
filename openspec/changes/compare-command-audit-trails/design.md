## Context

CFCT currently reads `CommandLogEntry` and `AuditTrailEntry` independently on both database sides to resolve the union of touched business tables.
The automation service selects a common successful foreground command, discovers each side's background descendants, waits for pending descendants, and includes completed descendants in the business-table footprint.
A missing or extra audit record can leave business data equal, so the existing comparison cannot detect regressions in audit generation itself.

The two sides share the replayed foreground interaction identifier.
Background command identifiers are generated independently and cannot be correlated across sides.
Causeway's audit `sequence` identifies a transaction or execution event within an interaction and is not a semantic property-change row number.
The same target and audited member can therefore occur more than once and must not be stored in a single-value map.

## Goals / Non-Goals

**Goals:**

- Compare the structure of audit records produced by the selected foreground command.
- Compare the structure of audit records produced by completed background descendants without correlating their generated identifiers.
- Detect missing, extra, and duplicate semantic audit observations.
- Report audit divergence independently from business-table divergence.
- Keep response ordering and diagnostics deterministic.
- Preserve the existing union-of-both-sides business-table footprint.

**Non-Goals:**

- Comparing `postValue` in this increment.
- Applying masks, timestamp tolerance, fuzzy matching, or large-value heuristics.
- Treating transaction sequence, audit timestamps, generated row identifiers, or child interaction identifiers as semantic equality fields.
- Pairing individual background commands across applications.
- Changing the meaning of the existing top-level `hasDifferences` field.

## Decisions

### Compare semantic-key occurrence counts

CFCT will project each audit row to a descriptor containing its interaction identifier, sequence, target, and audited member identifier.
The canonical fixture uses `AuditTrailEntry.propertyId` as the audited member identifier, while compatibility views for older schemas can expose an equivalent property or member column under the canonical name.

For each scope, records will be grouped by `(target, memberIdentifier)` and occurrence counts will be compared between app-a and app-b.
The foreground query is restricted by the shared foreground interaction identifier.
The background query is restricted by each side's independently discovered completed descendant identifiers, after which those identifiers are excluded from matching.

Occurrence counts are preferred to a single-value map because the same semantic key can legitimately occur in multiple transactions or background commands.
A total-count-only comparison was rejected because one missing observation can be concealed by one unrelated extra observation.

### Keep foreground and background scopes separate

The result will contain separate foreground and background summaries.
This prevents an extra foreground observation from cancelling a missing background observation with the same semantic key and gives clients actionable provenance.

Each scope will report total counts for app-a and app-b plus deterministic differences containing target, member identifier, app-a count, and app-b count.
An empty scope compares equal when both sides contain no records.

### Add a dedicated audit comparison service

A SQL Server audit repository will retrieve canonical audit descriptors for a supplied side and collection of interaction identifiers.
A database-independent comparison service will group and compare those descriptors.
The automation orchestration will invoke these services after selecting the foreground command and classifying completed background descendants.

This separation keeps SQL naming and compatibility-view concerns out of the comparison algorithm and leaves room for a later descriptor containing `postValue`.
Reusing the generic table comparer was rejected because it compares whole physical tables, requires physical business keys, and would include non-semantic audit columns.

### Extend the automation JSON additively

Successful `comparison.json` responses will include a top-level `auditTrailComparison` object with an independent `hasDifferences` field and foreground and background scope results.
The existing top-level `hasDifferences`, `differingTables`, and `comparedTables` fields will continue to describe only business-table comparison.
The audit object will also be present for commands with no eligible business-table footprint.

The endpoint already performs the startup re-check by refreshing the latest completed foreground command, so integrating audit comparison into every successful refresh applies the same behavior to post-replay and startup calls.

### Preserve union-based business-table selection

Business tables will continue to be selected as the union of foreground and completed-background tables resolved independently from both sides.
No intersection will be introduced.
Consequently, an audit record omitted by the candidate does not remove a table that the reference side resolved, while a candidate-only audit record can add an unexpected table to the comparison.

## Risks / Trade-offs

- [Matching only occurrence counts does not detect changed post-values] → Clearly identify the initial comparison mode in the JSON and retain an extensible descriptor and result shape for a later post-value phase.
- [A compatibility view exposes the wrong member semantics] → Define and test the canonical audited-member column as the property/member whose value changed, not the command action that initiated the change.
- [Duplicate semantic keys make diagnostics less specific] → Compare counts rather than overwriting duplicates and retain sequence in repository-level diagnostics for troubleshooting.
- [Large background groups increase query size] → Use parameterized set-based queries and short-circuit empty interaction collections.
- [An additive field affects strict external JSON consumers] → Preserve all existing fields and semantics and document the new field as additive.

## Migration Plan

Deploy CFCT before enabling Aregress enforcement.
Database installations using legacy command or audit tables must expose the canonical columns through the same compatibility-view mechanism used for command and audit footprint resolution.
Rollback consists of deploying the prior CFCT version; existing business comparison behavior and response fields remain unchanged.

## Open Questions

Post-value comparison, member-specific normalization, timestamp tolerance, and large-value handling are deferred to a follow-up change informed by real comparison results.
