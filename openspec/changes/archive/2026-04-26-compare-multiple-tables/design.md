## Context

The project direction is still library first, then CLI, and potentially webapp later.
The single-table comparison work establishes the core unit of comparison: compare one `TableRef` between left and right JDBC connections and return a structured result.
This change adds orchestration for a caller-specified set of tables while keeping the same explicit table-selection model.

The fixture suite is also moving toward table-oriented resource directories.
To test multi-table comparison realistically, the fixture set should contain three good comparable tables, and the multi-table test should compare two of them.

## Goals / Non-Goals

**Goals:**

- Add a core API that accepts a collection of table references.
- Compare exactly the requested tables, in deterministic request order.
- Reuse the existing single-table comparison service for each requested table.
- Return a structured multi-table result containing one single-table result per requested table.
- Render multi-table results deterministically for tests and future CLI output.
- Extend fixtures to include three good comparable tables with `_BK` unique indexes.
- Test comparing two selected tables from the available three good fixtures.

**Non-Goals:**

- Do not auto-discover all tables with `_BK` indexes.
- Do not add table include/exclude pattern matching.
- Do not add CLI or webapp behavior.
- Do not change single-table comparison semantics.
- Do not introduce cross-database support beyond SQL Server.
- Do not compare schemas.

## Decisions

### Orchestrate existing single-table comparison

The multi-table comparer should delegate each requested table to the existing single-table comparer.
This keeps metadata discovery, row reading, comparison rules, and ignored-column behavior in one place.
The multi-table layer should be thin orchestration plus aggregate result modeling.

Alternative considered: implement a bulk metadata and row-reading path immediately.
This was rejected because it would add complexity before there is a proven need for cross-table optimization.

### Require explicit table selection

The API should accept a caller-provided collection of `TableRef` values.
It should not scan the database for all `_BK` indexes in this change.
This keeps the next step small and gives callers precise control over scope.

Alternative considered: discover and compare all tables with `_BK` indexes.
This was rejected because it introduces discovery policy, ordering, and filtering questions that belong in a later change.

### Preserve deterministic ordering

The multi-table result should preserve the caller's table order, and the renderer should output table sections in that order.
This makes Approval tests stable and keeps the API predictable.

Alternative considered: sort tables alphabetically.
This was rejected because callers may choose order intentionally, and sorting can be added later as an option if needed.

### Add three good table fixtures but compare two

The fixture set should include three valid comparable table fixtures to prove the test environment can hold more comparable tables than a single test selects.
The multi-table integration test should compare two of those tables, showing that the API compares the requested set rather than every available good table.

Possible table fixtures are `PurchaseOrder`, `Supplier`, and `Product`.
Each should have an identity `id`, a natural business key column marked by a `_BK` unique index, `version DATETIME2(3)`, and a small deterministic data set.

## Risks / Trade-offs

- Multi-table result models may need richer summary information later → Start with one result per table and add summaries when the CLI or webapp needs them.
- A table comparison failure could stop later tables from being compared → For this first slice, fail fast and leave partial-result/error aggregation to a later change if needed.
- More fixtures can slow integration tests → Keep additional tables small and deterministic.
- Fixture names might imply domain modeling beyond current needs → Keep `Supplier` and `Product` simple and focused on comparison mechanics.
