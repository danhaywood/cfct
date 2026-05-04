## Context

The current webapp comparison experience shows MATCH rows by default, which dilutes visual focus when users are investigating differences.
The comparison grid currently lacks first-class column sorting and value filtering controls for rapid inspection workflows.
Users also need clearer call-to-action emphasis for `Compare`, and the left drawer layout can create overlap pressure between the business table grid and action controls.
The main shell uses Vaadin AppLayout, making drawer behavior and responsive sizing a cross-cutting UI concern.

## Goals / Non-Goals

**Goals:**
- Default result exploration to difference-focused behavior by hiding MATCH rows unless explicitly requested.
- Add deterministic sorting and value filtering behavior in the comparison results grid.
- Promote `Compare` as the primary action with stronger visual hierarchy and guaranteed non-overlap with table-selection content.
- Introduce end-user drawer-width resizing with bounded limits that preserve responsive layout behavior.

**Non-Goals:**
- No changes to comparison engine semantics, SQL extraction logic, or backend comparison result structure.
- No new authentication, authorization, or API-level contract changes.
- No redesign of command-selection behavior beyond layout constraints needed to prevent overlap with compare controls.

## Decisions

1.
Adopt `differences-focused by default` rendering for the right-hand result grid.
A new `Show MATCH rows` checkbox will control inclusion of MATCH rows in the current tab model.
This approach keeps full result fidelity available while reducing default visual noise.
Alternative considered: keeping MATCH visible and relying only on filters.
That alternative was rejected because it still requires extra user effort on every run.

2.
Use native Vaadin Grid capabilities for column sorting and per-column value filtering where possible.
Sorting will be enabled on relevant data columns and integrated with existing deterministic row rendering.
Filtering will be implemented as lightweight header controls tied to the active tab dataset.
Alternative considered: custom external filter panels.
That alternative was rejected because it increases layout complexity and weakens discoverability.

3.
Rework drawer action layout so `Compare` is a visually primary control and cannot be obscured by the business table selection grid.
The compare action will remain in a dedicated row with stable spacing and z-order, and the business table grid container will respect reserved action space.
Alternative considered: moving compare to the top navbar.
That alternative was rejected because compare context is tightly coupled to left-panel table selection.

4.
Implement user-resizable navigation drawer width with explicit min/max bounds.
The resize interaction will update drawer width state in-session and immediately reflow both drawer and content areas.
Alternative considered: static preset widths.
That alternative was rejected because datasets and user display sizes vary significantly.

## Risks / Trade-offs

[Risk] Added grid controls may increase header density on smaller screens.
→ Mitigation: use compact controls, responsive breakpoints, and horizontal scroll behavior already required by results tabs.

[Risk] Drawer resize behavior may interact poorly with AppLayout collapse states.
→ Mitigation: constrain resize to expanded drawer mode, preserve fallback defaults, and test collapse/expand transitions.

[Risk] Stronger compare button styling could conflict with existing theme tokens.
→ Mitigation: implement with existing design-system variables and verify dark/light theme compatibility.

## Migration Plan

This change is UI-behavioral and can ship without data migration.
Rollout will be done in one release with regression coverage for keyboard and resize scenarios.
Rollback can be performed by reverting the feature branch because no persisted schema changes are introduced.

## Open Questions

Should drawer width preference persist only per session or across browser sessions.
Should MATCH-row visibility preference persist per tab, per run, or globally for the page session.