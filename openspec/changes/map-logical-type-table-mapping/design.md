## Context

The SQL Server harness already provisions Causeway command-log and audit-trail tables and seeds deterministic rows used by integration tests.
Upcoming comparison behavior needs to resolve logical type identifiers from those rows to physical SQL tables.
Causeway models can use inheritance where a logical type maps to multiple tables, so the fixture must cover one-to-many mappings and stay deterministic across left and right databases.

## Goals / Non-Goals

**Goals:**
- Add a stable fixture table `_util.LogicalTypeTableMapping` to both harness databases.
- Seed mapping rows that correspond to logical types referenced by existing seeded command and audit rows.
- Include one inheritance-style example where one logical type has multiple `qualifiedName` rows.
- Keep fixture initialization idempotent so repeated setup does not fail or duplicate rows.

**Non-Goals:**
- Implement runtime resolution logic in production comparison services.
- Introduce schema migrations outside integration-test fixture SQL.
- Change existing command or audit seed semantics beyond adding related mapping data.

## Decisions

Create `_util` schema on demand and create `_util.LogicalTypeTableMapping` with nullable `logicalTypeName` and required `qualifiedName` in each logical database.
Use idempotent setup SQL with existence checks so fixture initialization remains rerunnable in test lifecycle hooks.
Seed rows using deterministic logical type names already present in command and audit fixture targets and add one additional logical type with two qualified table names to model inheritance.
Keep mapping rows local to each database fixture initialization so left and right data can diverge later without structural coupling.

## Risks / Trade-offs

[Mapping names drift from seeded command or audit rows] → Derive mapping seed values directly from the same fixture constants and verify via integration assertions.
[Duplicate seed rows on repeated initialization] → Use delete-and-insert or guarded inserts scoped to known keys during fixture setup.
[Overfitting inheritance example to one ORM pattern] → Keep requirement generic by asserting one logical type may map to multiple physical tables, not a specific persistence framework internals.
