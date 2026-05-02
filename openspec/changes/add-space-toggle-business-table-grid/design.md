## Context

Command-grid keyboard behavior already supports focused-row Space toggling.
Manual business table selection still relies primarily on checkbox clicks.
The request is to bring parity so keyboard selection is consistent across both grids.

## Goals / Non-Goals

**Goals:**
- Add Space-key toggling for focused eligible rows in the manual business table grid.
- Preserve existing ineligible-row behavior and eligibility guards.
- Reuse existing selection-state update paths to avoid duplicated state transitions.
- Keep current arrow-key navigation behavior stable.

**Non-Goals:**
- Redesign manual table grid layout.
- Change compare readiness semantics.
- Introduce global hotkeys outside the focused business grid context.

## Decisions

- Add explicit key handling for Space in the manual table grid focus context.
This mirrors command-grid interaction and keeps behavior discoverable.
Alternative considered was relying only on native grid keyboard handling.
That was rejected because explicit toggling behavior is required and test expectations should be deterministic.

- Route Space-triggered toggles through the same state mutation flow used by checkbox toggles.
This keeps compare-readiness and command-overlay interactions consistent.

- Ignore Space toggles when focused row is ineligible.
This preserves existing non-selectable behavior guarantees.

## Risks / Trade-offs

- [Focus tracking may differ across browsers] → Keep tests at unit level with deterministic focus hooks and retain browser smoke assertions where available.
- [Space handling conflicts with default scroll behavior] → Scope handling to focused grid context only and prevent unintended propagation.
