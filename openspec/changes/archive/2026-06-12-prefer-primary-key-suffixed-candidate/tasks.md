## 1. Core metadata key selection

- [x] 1.1 Introduce or update a SQL Server key-candidate representation that includes object name, key columns, and primary-key status.
- [x] 1.2 Update core business-key discovery to collect distinct suffix-matching unique index or constraint candidates with literal case-insensitive suffix semantics.
- [x] 1.3 Implement selection precedence that uses the sole matching candidate, or the sole matching primary-key candidate when multiple candidates exist.
- [x] 1.4 Preserve clear missing-key and unresolved-ambiguity `MetadataException` messages with candidate names.

## 2. Webapp manual eligibility discovery

- [x] 2.1 Update manual table-catalog SQL or mapping so table-level extended properties cannot multiply key-candidate counts.
- [x] 2.2 Update manual eligibility discovery to use distinct literal `_PK` suffix candidates rather than SQL wildcard-style `LIKE` semantics.
- [x] 2.3 Apply the same primary-key preference rule to manual eligibility that core metadata discovery uses.
- [x] 2.4 Keep metadata-disabled tables ineligible before key-candidate eligibility is applied.

## 3. Tests and fixtures

- [x] 3.1 Add core metadata tests for a `_PK` primary key plus unrelated non-`_PK` unique constraint such as `ApplicationUser__username__UNQ`.
- [x] 3.2 Add core metadata tests for primary-key preference when multiple `_PK` candidates exist and exactly one is primary.
- [x] 3.3 Add core metadata tests proving unresolved multiple non-primary `_PK` candidates still fail clearly.
- [x] 3.4 Add webapp catalog tests proving extended properties do not multiply a single `_PK` candidate into an ambiguity.
- [x] 3.5 Add webapp catalog tests for literal suffix matching and unrelated unique constraints.

## 4. Validation

- [x] 4.1 Run relevant `cfct-impl` tests covering SQL Server metadata discovery.
- [x] 4.2 Run relevant `cfct-webapp` tests covering manual table catalog eligibility.
- [x] 4.3 Run impacted integration tests for single-table comparison metadata discovery.
- [x] 4.4 Run `openspec validate prefer-primary-key-suffixed-candidate --strict` and resolve any proposal/spec/task issues.
