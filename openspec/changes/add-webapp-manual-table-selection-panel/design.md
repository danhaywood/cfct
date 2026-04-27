## Context

The webapp now exposes connectivity status, but table-target choice is still effectively static and not user-driven.
The target production shape includes large schemas with hundreds of tables where only a small subset should be compared per run.
A two-stage user flow is needed so users can select tables first and then trigger comparison second.
This change intentionally focuses on the left-hand selection stage and leaves the right-hand comparison presentation for later.

## Goals / Non-Goals

**Goals:**
- Provide a left-side table-selection panel that lists discovered tables with checkboxes.
- Represent `_BK` eligibility clearly by disabling and greying out ineligible tables.
- Provide immediate feedback on selection count so users know what will be compared.
- Preserve a clean contract for a later auto-selection mechanism plus manual include and exclude controls.
- Extend headless Playwright tests to validate selection interactions and eligibility behavior.

**Non-Goals:**
- Implementing final auto-selection logic in this change.
- Building the final right-side comparison result layout for all selected tables.
- Optimizing for very large-table virtualized rendering beyond what is needed for initial manual selection behavior.
- Redefining comparison engine semantics or schema-diff rules.

## Decisions

### Decision: Introduce an explicit table-catalog view model for UI selection
The UI will use a dedicated table-catalog model containing table identity, eligibility, and selected state.
This isolates UI concerns from comparison execution internals and simplifies future auto-selection integration.
Alternative considered was binding checkboxes directly to `SelectionPlan` output, which was rejected because it conflates selection intent with execution payload.

### Decision: Represent `_BK` gating as a first-class eligibility flag
Eligibility will be computed per table and surfaced as `eligible=true|false` with a reason string for ineligible rows.
The panel will render ineligible rows as greyed out with disabled checkboxes.
Alternative considered was filtering out ineligible tables completely, which was rejected because users need visibility into why a table cannot currently be selected.

### Decision: Keep selection as stage one and comparison trigger as stage two
The page will establish explicit stage semantics where table selection does not automatically execute comparison.
A run action boundary will be maintained even if the right-hand results area is still placeholder-level in this change.
Alternative considered was immediate compare-on-click behavior, which was rejected because it scales poorly and obscures user intent.

### Decision: Design for future auto-selection plus manual include and exclude
Selection state will support layering so an auto-selected baseline can be manually adjusted later.
This avoids a redesign when auto-selection arrives and keeps user override behavior additive.
Alternative considered was implementing manual-only state with no layering semantics, which was rejected because it creates migration churn.

### Decision: Extend existing Playwright suite instead of creating a separate browser test stack
The current headless Playwright infrastructure will be expanded with table-selection assertions.
This keeps CI entry points stable and reuses existing Testcontainers-backed environment setup.
Alternative considered was a separate UI-only mock backend test harness, which was rejected because it would drift from runtime behavior.

## Risks / Trade-offs

- [Table discovery latency may impact perceived responsiveness] → Add loading-state messaging and keep initial dataset bounded in tests.
- [Eligibility detection for `_BK` may vary by naming or metadata conventions] → Capture rule assumptions explicitly and include representative fixture tables in tests.
- [Selection-state complexity can grow when auto-selection is added] → Define a layered selection model now and validate merge semantics with unit tests.
- [Playwright tests may become flaky if selectors depend on layout details] → Use stable `data-testid` attributes and semantic assertions on state and count text.

## Migration Plan

Add table-catalog and selection-state plumbing behind current view composition without changing comparison execution defaults initially.
Introduce left-panel rendering with checkboxes, disabled styling, and selection-count feedback.
Add Playwright and unit tests for selection, ineligible behavior, and feedback updates.
Document the two-stage workflow and known scope boundary for right-side comparison rendering.
Rollback is straightforward by removing the left-panel controls and restoring prior static behavior if required.

## Open Questions

Should `_BK` eligibility be defined by key-name suffix only, or by richer metadata inspection for business-key semantics.
How should manual include and exclude semantics combine with future auto-selection defaults when conflicts exist.
What maximum table count should trigger virtualization in the selection grid for responsiveness.
