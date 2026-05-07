## Context

The command selection grid already supports multi-selection, keyboard navigation, and Space toggling for the focused row.
Users currently need repeated row-by-row toggles to select long contiguous command spans.
The drawer workflow depends on fast command selection because selected commands drive business-table auto-selection and compare readiness.
The implementation must preserve existing selection semantics for single-row toggles and must remain compatible with Vaadin Grid selection APIs.

## Goals / Non-Goals

**Goals:**
- Add contiguous range selection for command rows using an anchor-and-extend interaction.
- Support both pointer and keyboard-assisted range selection flows.
- Keep command-driven table union behavior unchanged while expanding selected-command input.
- Keep behavior deterministic for automated tests across sorting and filtering states.

**Non-Goals:**
- No changes to command sorting rules or filter definitions.
- No changes to business-table mapping logic beyond consuming the updated selected set.
- No bulk-select-all control or non-contiguous smart-selection heuristics in this change.

## Decisions

Use a dedicated range-selection anchor in command-grid UI state, represented by the last non-Shift row selection intent.
This decision isolates range semantics from Vaadin internal focus details and gives deterministic behavior for tests.
Alternative considered was relying only on framework default Shift-selection behavior, but explicit state is preferred because existing custom selection orchestration and downstream updates need predictable hooks.

Define Shift-click on a target row as selecting the inclusive visible-row interval between anchor and target.
This decision matches common desktop selection idioms and reduces clicks for contiguous batch selection.
Alternative considered was replacing selection with interval-only every time, but additive interval behavior with deterministic inclusive bounds better preserves user expectations in multi-select workflows.

Define Shift+Space as keyboard-assisted interval selection from anchor to currently focused row.
This decision provides parity for keyboard-first users and aligns with existing Space-based selection affordance.
Alternative considered was introducing a new shortcut key, but extending Space semantics keeps discoverability and avoids shortcut collisions.

Trigger downstream selected-command recomputation once per completed interval operation instead of per-row mutation.
This decision avoids excessive recomputation churn when large ranges are selected.
Alternative considered was per-row incremental updates, but batched updates are simpler to reason about for progress-reset and table-union side effects.

Clear or rebase anchor when filtering or sorting invalidates anchor row visibility.
This decision prevents stale anchor references from creating surprising intervals.
Alternative considered was keeping hidden anchors indefinitely, but that can generate invisible interval bounds and confusing outcomes.

## Risks / Trade-offs

[Range behavior ambiguity with existing toggles] → Document explicit anchor lifecycle and cover with interaction tests for click, Shift-click, Space, and Shift+Space.
[Performance regression on large intervals] → Batch selection-state updates and downstream table recomputation to one post-operation sync point.
[Framework event-order differences across browsers] → Prefer grid data-provider index resolution and Playwright cross-browser assertions for interval bounds.

## Migration Plan

Implement range-selection state and handlers behind current command-grid component without schema or API migrations.
Add or update tests before release to validate single selection, interval selection, and downstream table-union behavior.
Roll back by disabling interval handlers and reverting to existing per-row toggle behavior if regressions are detected.

## Open Questions

Should Shift-click replace the current selection set with the interval or add the interval to the existing set when disjoint rows were previously selected.
Should Shift+Space require explicit focus-row anchor initialization feedback in the UI for accessibility messaging.
