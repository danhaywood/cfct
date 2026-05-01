## Context

The left navigation business table grid is populated from SQL Server metadata discovery.
Recent footprint work added command and audit support tables plus logical-type mapping tables to test fixtures.
Those support tables should not appear as selectable business tables in the manual selection grid.

## Goals / Non-Goals

**Goals:**
- Remove command, audit, and logical-type mapping tables from the manual business table grid.
- Keep table-grid behavior unchanged for business tables that remain visible.
- Make exclusion rules deterministic and testable.

**Non-Goals:**
- Change command-selection grid behavior.
- Remove or rename system tables in fixture schemas.
- Alter comparison execution semantics for user-selected business tables.

## Decisions

Apply exclusion rules in table-catalog discovery so filtered rows never reach the manual table selection state.
Exclude by fully qualified schema and table identity for known support tables: `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
Keep all existing eligibility and business-key checks for rows that pass the exclusion filter.
Update unit tests and browser-level assertions to verify excluded tables are absent from the business grid.

## Risks / Trade-offs

[Future support tables may leak into the business grid] → Centralize exclusion list and extend tests when new support tables are introduced.
[Over-filtering hides legitimate business tables] → Match by exact schema/table pairs rather than broad name fragments.
[Behavior drift between fixture and production schemas] → Keep exclusions explicit and environment-independent.

## Migration Plan

Implement exclusion in `SqlServerTableCatalogService` before mapping to `TableCatalogEntry`.
Update webapp tests to assert absence of the excluded support tables.
Run webapp tests to confirm grid behavior remains stable.

## Open Questions

Whether exclusion rules should become configurable via properties in a follow-up change.
