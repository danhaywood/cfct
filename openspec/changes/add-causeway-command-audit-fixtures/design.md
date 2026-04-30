## Context

The project is adding an auto-selection capability that will infer candidate comparison tables from command and audit history.
That workflow depends on Causeway-style command-log and audit-trail records being present in the integration fixture.
Today the SQL Server harness provisions two logical databases but does not yet include these Causeway extension tables.
This change focuses only on fixture schema support, not on footprint query logic or identifier-to-table mapping behavior.

## Goals / Non-Goals

**Goals:**
- Add `causewayExtCommandLog.CommandLogEntry` to the fixture in both logical databases.
- Add `causewayExtAuditTrail.AuditTrailEntry` to the fixture in both logical databases.
- Define primary keys exactly as required for future footprint lookups.
- Keep fixture initialization deterministic and compatible with existing harness tests.

**Non-Goals:**
- Implement command selection workflows in CLI or webapp.
- Implement auto-selection query logic or ranking.
- Implement mapping from logical identifiers to concrete table entities.
- Add non-primary-key audit-trail columns beyond the explicitly requested fixture subset.

## Decisions

Use the existing harness initialization path to add DDL for both new tables in each logical database.
This keeps setup centralized and avoids introducing a parallel fixture mechanism.

Represent `CommandLogEntry` columns with SQL Server types aligned to the request, including `uniqueidentifier`, `varchar`, and `datetime2`.
If the current fixture does not support custom enum domains, represent `replayState` as a constrained textual column to preserve semantic values while remaining portable in SQL Server DDL.

Set `CommandLogEntry` primary key to `transactionId` as requested, adding the key column to the fixture definition if it is not already present.
This preserves downstream assumptions about unique command entries.

Set `AuditTrailEntry` primary key as a composite of `interactionId`, `sequence`, `target`, and `propertyId`.
This captures the minimal uniqueness contract required for linking audit rows to command executions.

Add focused integration assertions that validate table existence and primary-key shape in both logical databases.
This provides quick feedback if fixture DDL drifts from the expected Causeway-compatible schema.

## Risks / Trade-offs

[Risk: Requested DDL includes syntax or naming inconsistencies, such as `replayState.` or omitted `transactionId` declaration] → Mitigation: Normalize to valid SQL Server DDL while preserving intent, and document the normalization in test names or fixture comments.
[Risk: Existing fixture reset logic may fail if schemas or tables already exist] → Mitigation: Use idempotent drop/create ordering consistent with current harness patterns.
[Risk: Future Causeway schema expectations may include additional columns not present now] → Mitigation: Keep tests scoped to required columns and keys so the fixture can be extended incrementally without breaking this change.
