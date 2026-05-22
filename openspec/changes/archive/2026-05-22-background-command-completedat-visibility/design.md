## Context

The command grid currently shows replay-state, member, timestamp, and interactionId columns.
Some command rows have replay state `UNDEFINED`, which represents background work not yet mapped into a user-friendly status.
Background completion is already captured in database state via `completedAt`, but the webapp does not surface it.

## Goals / Non-Goals

**Goals:**
- Surface `completedAt` in command-grid data and UI as a first-class column after `timestamp`.
- Derive user-facing replay-state labels for `UNDEFINED` rows from `completedAt` presence.
- Keep existing command-grid ordering, filtering, and selection behavior stable.

**Non-Goals:**
- Changing persistence semantics of command replay states.
- Introducing new replay states in database storage.
- Altering compare orchestration or table-selection behavior.

## Decisions

- Extend `CommandCatalogEntry` to carry `completedAt` and expose it to grid rendering.
- Update SQL discovery query and row mapping to select and map `completedAt` as ISO text.
- Keep raw replay-state values in model state, but render `UNDEFINED` as `BGRND:PEND` or `BGRND:DONE` in the UI layer.
- Place `completedAt` column immediately after `timestamp` to preserve chronological context.
- Update unit and UI tests that assert command-grid column order and replay-state values.

## Risks / Trade-offs

[Risk] Existing tests or fixtures might assume old column count and order.
→ Mitigation: update all relevant assertions and add explicit completedAt ordering coverage.

[Risk] Null and empty completedAt handling could be inconsistent across fixtures.
→ Mitigation: normalize mapping and rendering checks to treat null/blank as empty.

[Risk] Derived labels might hide raw `UNDEFINED` values needed for troubleshooting.
→ Mitigation: keep raw replay state in model and only transform for displayed value.

## Migration Plan

No data migration is required.
Deploy as an application update with accompanying tests.
Rollback is a standard app rollback because schema is unchanged.

## Open Questions

Should `completedAt` support command-grid filtering in a follow-up change.
