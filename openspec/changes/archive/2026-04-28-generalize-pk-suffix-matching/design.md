## Context

The current key-discovery path in core comparison logic inspects database metadata and infers the business key from index or constraint naming.
The existing matching behavior is stricter than needed and can reject valid names that include contextual prefixes before the PK marker.
A representative failing pattern is `PurchaseOrder__reference__PK`, where the business key intent is clear from the suffix.
The change must preserve compatibility with existing naming conventions and avoid altering unrelated comparison semantics.

## Goals / Non-Goals

**Goals:**
- Generalize PK/business-key name matching so detection depends on a PK suffix rule rather than full-name equality.
- Ensure both index names and constraint names are evaluated consistently.
- Preserve existing successful detections and add tests for expanded suffix cases.

**Non-Goals:**
- Introducing new user-facing configuration for naming rules.
- Changing table-selection, row-diff, or report formatting behavior.
- Reworking metadata access strategy beyond what is needed for suffix matching.

## Decisions

- Adopt a normalized suffix predicate for key name matching.
  - Decision: match candidate names case-insensitively after normalization and accept when the identifier ends with `_PK`.
  - Rationale: this captures both legacy names and extended composite names without requiring schema-specific prefixes.
  - Alternative considered: regex-based configurable patterns.
  - Why not chosen: adds complexity and configuration burden for a narrowly scoped requirement.

- Apply the same predicate to both index and constraint metadata sources.
  - Decision: centralize name-checking in a shared helper used by both paths.
  - Rationale: avoids drift and ensures consistent behavior regardless of metadata source order.
  - Alternative considered: duplicate logic in each retrieval path.
  - Why not chosen: increases maintenance risk and regression probability.

- Add regression-focused tests at the lowest feasible level plus fixture coverage.
  - Decision: add unit tests around the suffix predicate and update integration-style fixture tests that exercise metadata-driven key discovery.
  - Rationale: unit tests protect edge cases quickly, while fixture tests validate end-to-end behavior against realistic metadata.
  - Alternative considered: fixture-only testing.
  - Why not chosen: slower diagnostics and weaker coverage of corner-case normalization logic.

## Risks / Trade-offs

- [Risk] Overly broad suffix matching could accept non-key names that accidentally end with `_PK`.
  → Mitigation: retain existing metadata type filtering so only index/constraint candidates are evaluated.

- [Risk] Case or delimiter normalization differences across database vendors could cause inconsistent matching.
  → Mitigation: normalize identifier casing and keep delimiter handling minimal and deterministic.

- [Trade-off] Broader acceptance may make key inference less explicit than strict full-name matching.
  → Mitigation: codify intended examples in tests and document suffix-based rule in spec delta.

## Migration Plan

No runtime migration or data migration is required.
The change is code-only and can be released with standard test validation.
Rollback is a normal code revert if unexpected key selection behavior appears in production.

## Open Questions

No blocking open questions are identified.
If future datasets reveal false positives, follow-up work can introduce additional heuristics while keeping suffix behavior as the baseline.
