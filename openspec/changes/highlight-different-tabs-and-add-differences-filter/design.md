## Context

The comparison stage currently renders one tab per compared table and allows text filtering by table name.
Users must open each tab to discover whether differences exist.
The Playwright fixture currently focuses on differing Supplier and Product examples and does not include an explicit business table with zero differences.

## Goals / Non-Goals

**Goals:**
- Surface difference presence directly in tab styling so changed tables are obvious at a glance.
- Provide a deterministic checkbox that filters result tabs to only those with differences.
- Add a stable equal-result business table fixture (`CustomerAddress`) to demonstrate unchanged-table behavior.
- Add deterministic Playwright assertions for the new color/filter workflow.

**Non-Goals:**
- Changing comparison algorithms or row-difference classification logic is out of scope.
- Introducing user-configurable color themes is out of scope.
- Reworking existing compared-table text filter semantics is out of scope.

## Decisions

Compute per-table difference status from existing `TableComparisonViewResult` row statuses and expose a boolean flag for tab rendering decisions.
Assign deterministic CSS classes or attributes on tab elements for changed versus unchanged tables, and keep class names stable for tests.
Add a `Differences only` checkbox in the result actions area that composes with existing compared-table text filtering.
Treat side-only rows as differences for both tab-coloring and checkbox filtering semantics.
Extend the SQL Server test fixture with a new `_PK` keyed `CustomerAddress` table in both databases with identical seeded rows.
Update Playwright tests to compare at least one changed table and one unchanged table, then assert tab styling and checkbox filtering outcomes.

## Risks / Trade-offs

[Color-only signaling may be insufficient for some users] → Add deterministic attribute/class hooks so future icon/text augmentation is easy without breaking tests.
[Filter interaction complexity with existing text filter] → Define deterministic composition order and assert it in tests.
[Fixture drift across left/right DB setup] → Seed `CustomerAddress` rows from one shared fixture recipe for both sides.
