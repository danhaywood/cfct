## Context

CFCT currently discovers comparison row identity from SQL Server unique indexes or unique constraints whose names end with the configured business-key suffix, defaulting to `_PK`.
The core reader builds candidate objects from `sys.indexes`, `sys.index_columns`, and `sys.columns`, then rejects more than one suffix-matching candidate as ambiguous.
The webapp table catalog uses a separate SQL aggregate to decide whether each table is eligible for manual selection.
That aggregate can diverge from core behavior because it counts matching index rows after joins to table-level extended properties and uses SQL `LIKE '%_PK'`, where `_` is a wildcard rather than a literal underscore.

The motivating table has a primary key constraint `ApplicationUser_PK` on stable aligned `id` values and a separate unique constraint `ApplicationUser__username__UNQ`.
The `id` primary key is the correct logical row identity for this database comparison.
The webapp should therefore consider the table eligible and the core comparison should select the primary key if it is the sole safe `_PK` primary-key candidate.

## Goals / Non-Goals

**Goals:**

- Make key-candidate discovery consistent between manual table eligibility and core comparison execution.
- Treat `_PK` as a literal case-insensitive suffix rule rather than a SQL wildcard pattern.
- Prevent unrelated table-level extended properties from multiplying a single candidate into an apparent ambiguity.
- Prefer exactly one `_PK`-suffixed primary-key candidate when multiple suffix-matching unique objects exist.
- Preserve clear ambiguity errors when no single primary-key candidate disambiguates multiple suffix matches.

**Non-Goals:**

- Add a new user-facing key-selection configuration mechanism.
- Infer keys from unique constraints that do not match the configured suffix.
- Prefer non-`_PK` business unique constraints such as `username` over a valid `_PK` primary key.
- Change row-diff SQL generation, report rendering, or manual selection interaction semantics.

## Decisions

- Represent discovered key objects with enough metadata to distinguish primary-key candidates from other unique candidates.
  - Rationale: primary-key preference requires knowing whether each suffix-matching unique object is the table primary key.
  - Alternative considered: continue collecting only names and columns.
  - Why not chosen: name-only collection cannot safely distinguish real primary keys from other unique constraints.

- Apply primary-key preference only after suffix filtering.
  - Rationale: the configured suffix remains the naming contract, and primary-key status is only a tiebreaker among matching candidates.
  - Alternative considered: always prefer the database primary key even if its name does not end with `_PK`.
  - Why not chosen: that would weaken the existing explicit eligibility convention and could unexpectedly select technical keys on tables intentionally lacking a configured comparison key.

- Treat exactly one suffix-matching primary-key candidate as authoritative when multiple suffix-matching candidates exist.
  - Rationale: a primary key is the strongest database-declared row identity and resolves common false ambiguity where additional unique constraints also match broad naming conventions.
  - Alternative considered: keep failing on any multiple suffix matches.
  - Why not chosen: it blocks valid tables where the primary-key candidate is sufficient and intentional.

- Keep ambiguity when multiple suffix-matching primary-key candidates or only multiple non-primary suffix-matching candidates are found.
  - Rationale: the system should not guess when SQL Server metadata does not identify a single best candidate.
  - Alternative considered: choose the first candidate in metadata order.
  - Why not chosen: metadata order is not a safe user-facing selection rule.

- Update webapp eligibility discovery to avoid row multiplication and literal-suffix mismatch.
  - Rationale: the manual grid should be a preview of what the core comparison can execute, not a looser or stricter approximation.
  - Alternative considered: keep the aggregate and change only the count to `COUNT(DISTINCT ...)`.
  - Why not chosen: `COUNT(DISTINCT ...)` helps extended-property multiplication but does not by itself expose primary-key preference or literal suffix semantics clearly.

## Risks / Trade-offs

- [Risk] A table with a stable non-primary `_PK` unique constraint and a primary key also ending `_PK` will now use the primary key.
  → Mitigation: this applies only when both candidates match the explicit suffix and exactly one is the primary key; tests should document this as intentional precedence.

- [Risk] Webapp and core discovery could drift again if implemented separately.
  → Mitigation: share the suffix predicate where module boundaries permit and mirror candidate-selection rules with focused tests in both modules.

- [Risk] Some existing tests expect ambiguity whenever more than one `_PK` object exists.
  → Mitigation: update those tests to distinguish resolvable ambiguity with exactly one primary key from unresolved ambiguity with multiple non-primary candidates.

## Migration Plan

No data migration is required.
Existing databases with a valid `_PK` primary key become eligible without schema changes.
Rollback is a normal code revert if primary-key preference proves too permissive.

## Open Questions

No blocking open questions remain.
The motivating `ApplicationUser.id` values are confirmed stable and aligned between compared databases, so the `_PK` primary key is the correct logical row identity for this use case.
