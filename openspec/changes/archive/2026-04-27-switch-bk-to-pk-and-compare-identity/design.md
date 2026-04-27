## Context

Current metadata discovery treats a single unique index ending `_BK` as the business key and default options ignore `id` and `version` during comparison.
Recent fixture and webapp work added selection and Playwright behavior that also assumes `_BK` eligibility.
The target data model now standardizes on `_PK` naming for primary-key-style constraints or unique indexes, and identity values are required as first-class comparable values.
This is a cross-cutting behavior change because it touches core comparison semantics, fixture contracts, webapp eligibility rules, and automated assertions.

## Goals / Non-Goals

**Goals:**
- Change default key-discovery suffix from `_BK` to `_PK` across core comparison flows.
- Ensure identity columns are compared by default unless the caller explicitly ignores them.
- Keep deterministic failure behavior when no matching key index exists or when multiple matching key indexes exist.
- Update fixture SQL and tests so baseline scenarios represent `_PK` conventions and identity-aware outcomes.
- Update manual-selection eligibility and Playwright expectations to `_PK` rule.

**Non-Goals:**
- Adding mixed fallback behavior that auto-accepts both `_BK` and `_PK` during normal execution.
- Redesigning table-difference report formats beyond key-name and compared-column content changes.
- Replacing current selection workflow or connectivity validation architecture.

## Decisions

### Decision: Use `_PK` as the new default key suffix in comparison options
Default `ComparisonOptions` will set business-key suffix to `_PK`.
This preserves explicit configurability while aligning the default with current schema conventions.
Alternative considered was dual-suffix discovery (`_PK` then `_BK`), which was rejected because it hides convention drift and can mask ambiguous metadata.

### Decision: Remove implicit `id` ignore from default ignored columns
Default ignored columns will no longer include `id`.
Identity values will participate in comparison unless explicitly listed in ignore options by caller intent.
Alternative considered was a conditional rule that ignores identity unless part of key columns, which was rejected for being harder to reason about and less transparent.

### Decision: Keep strict single-index suffix matching semantics
Metadata discovery remains strict about exactly one matching unique index suffix.
Missing or ambiguous matches still fail with explicit table-specific diagnostics.
Alternative considered was selecting first match by ordinal or index id, which was rejected because it introduces silent nondeterminism.

### Decision: Update fixtures and tests to represent identity-aware expectations
Fixture definitions will rename business-key indexes to `_PK` and maintain realistic identity primary-key columns.
Comparison tests will assert identity differences as real differences unless ignore options are explicitly set.
Alternative considered was preserving old fixture naming with mapping indirection, which was rejected because it weakens executable specification value.

### Decision: Apply `_PK` eligibility rule to manual selection and browser tests
Webapp table-catalog eligibility logic will use `_PK` detection instead of `_BK`.
Playwright scenarios will validate disabled-state behavior based on `_PK` absence and active behavior when `_PK` is present.
Alternative considered was deferring webapp eligibility migration, which was rejected because it creates inconsistent behavior between comparison core and selection UI.

## Risks / Trade-offs

- [Behavior change may increase reported differences due to identity comparison] → Document default change clearly and provide explicit ignore examples for callers needing legacy behavior.
- [Existing schemas still using `_BK` will fail under new default] → Require explicit suffix override in callers that must remain on `_BK` temporarily.
- [Fixture and approval outputs will churn] → Regenerate approvals in one scoped update with clear migration notes.
- [Webapp and Playwright assertions may become brittle during suffix transition] → Use deterministic selector and metadata assertions tied to `_PK` rule.

## Migration Plan

Change core defaults first, then update metadata-reader assertions and single-table/multi-table tests.
Update fixture SQL resources and expected outputs to `_PK` naming and identity-aware differences.
Update webapp eligibility service and Playwright fixture preparation to `_PK` conventions.
Refresh documentation and examples that reference `_BK` defaults.
If rollback is needed, revert default option values and fixture naming together to avoid mixed-convention inconsistency.

## Open Questions

Do we need a temporary compatibility flag for `_BK` in wrapper scripts or webapp configuration examples during transition.
Should version remain in default ignored columns or move to explicit ignore-only behavior in a follow-up change.
