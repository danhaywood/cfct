## Context

Current comparison SQL classifies matched-row differences before any client-side post-processing.
Existing ignore-column behavior can suppress entire columns, but it cannot scrub volatile substrings inside otherwise meaningful values.
The requested behavior requires preserving SQL-side candidate discovery while applying Java-side normalization before final difference emission.
The solution must integrate with current SPI composition patterns used for ignore-column advisors and SQL Server extended-property metadata.

## Goals / Non-Goals

**Goals:**
- Introduce a pluggable `ColumnValueNormalizer` SPI in the API module so normalization behavior is extensible.
- Provide a default SQL Server-aware normalizer that reads `cfct.normalizeMask` and replaces matching fragments with the configured mask text.
- Apply normalization after SQL returns value-different candidate rows and before row differences are added to the result model.
- Suppress candidate row differences when all compared column values are equal after normalization.
- Keep normalization deterministic and transparent for testability.

**Non-Goals:**
- Rewriting SQL difference predicates to perform masking logic in the database.
- Adding regex-style wildcard DSLs beyond the provided mask string semantics.
- Changing business-key discovery or ignore-column advisor contracts.

## Decisions

### Decision: Add `ColumnValueNormalizer` SPI alongside existing advisor SPIs.
The API module will expose a `ColumnValueNormalizer` contract that receives column context and left/right values and returns normalized values used for comparison output.
This mirrors existing extension patterns and allows additional normalizers without changing comparison orchestration.
Alternative considered: embedding mask logic directly into core comparator without SPI.
That alternative was rejected because it couples one normalization strategy to the engine and blocks future customizers.

### Decision: Implement mask-based normalization using SQL Server extended property `cfct.normalizeMask`.
A default implementation will resolve the configured mask per column and scrub matching substrings by replacing each match with the mask literal.
Mask interpretation will use Java date/time parsing helpers to build a matcher for known temporal tokens and preserve all non-matching text.
Alternative considered: literal substring replacement using the mask as plain text.
That alternative was rejected because mask text describes a pattern, not a literal value present in source data.

### Decision: Keep SQL-side candidate detection unchanged and normalize in Java only.
The SQL query will continue returning potential value-different rows based on raw compared values.
Core code will normalize candidate column values in memory and then re-evaluate equality to decide whether to emit each differing column and row.
Alternative considered: pushing normalization into SQL select projections and predicates.
That alternative was rejected because SQL Server pattern handling for this use case is complex, less portable, and harder to test than Java logic.

### Decision: Compose multiple normalizers and apply them in deterministic order.
Core comparison will inject `List<ColumnValueNormalizer>` and apply each normalizer sequentially for each compared value pair.
A stable order gives predictable outputs for Approval and character tests.
Alternative considered: single normalizer instance.
That alternative was rejected because composition aligns with existing SPI strategy and future extensibility.

## Risks / Trade-offs

[Mask parsing ambiguity for token variants] → Mitigation: support a documented token subset first, add focused tests for accepted patterns, and fail closed by leaving values unchanged when parsing is unsupported.
[Client-side normalization cost on large diff candidates] → Mitigation: run normalization only for rows already flagged by SQL as value-different and short-circuit once a non-normalizable hard difference is confirmed.
[Unexpected suppression hiding real differences] → Mitigation: ensure only exact post-normalization equality suppresses output and add characterization tests with mixed masked and non-masked segments.
[Extended property lookup overhead] → Mitigation: reuse existing metadata-fetch paths and cache per-column normalize mask alongside other column metadata.

## Migration Plan

No data migration is required.
Release as backward-compatible default behavior with no configured mask meaning no normalization.
Document `cfct.normalizeMask` usage and example patterns in developer docs.
Rollback is achieved by removing or disabling the normalizer bean or removing extended properties.

## Open Questions

Should unsupported mask formats log diagnostic warnings or remain silent no-ops.
Should the SPI contract expose side information about whether a normalization occurred for observability.
