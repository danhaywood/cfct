## Context

`MainView` currently renders three footer spans for connection state, connection summary, and comparison progress.
Users can trigger compare, change command filters and selections, change business table filters and selections, and use a Clear action in the drawer.
The existing terminal progress message can remain after these drawer interactions, which makes the UI appear to report stale run status.
The requested behavior keeps one status channel (`comparisonProgressSummary`) and uses visual outcome cues for success or failure.

## Goals / Non-Goals

**Goals:**
- Remove connection status state and summary spans from the footer status panel.
- Make comparison progress text the single status output shown in the footer status area.
- Reset comparison progress output whenever drawer interactions invalidate previous run context.
- Add deterministic success and failure background styles for terminal progress outcomes.
- Keep Playwright assertions aligned with the new footer contract.

**Non-Goals:**
- Changing datasource validation or backend connectivity checks.
- Changing comparison execution order, progress event payloads, or API contracts.
- Redesigning the full AppLayout shell beyond targeted footer status behavior.

## Decisions

1.
Use `comparisonProgressSummary` as the sole footer status component and remove `connectionStatusState` plus `connectionStatusSummary` from layout construction and updates.
This removes redundant and now-unneeded status text while preserving progress reporting.
Alternative considered: hide legacy spans conditionally.
This was rejected because dead components still complicate state management and tests.

2.
Introduce a small UI-state helper in `MainView` to clear progress text and remove outcome style classes when selection context changes.
Selection context changes include command-grid filter changes, command row selection changes, business-table filter changes, business-table selection changes, and command Clear.
Alternative considered: only clear on Clear button.
This was rejected because stale status can still survive ordinary selection-parameter edits.

3.
Apply explicit success and failure style classes (or tokenized background styles) to `comparisonProgressSummary` only on terminal events.
Non-terminal running updates use neutral styling.
Alternative considered: text-only success/failure wording.
This was rejected because the requirement asks for obvious visual differentiation.

4.
Update Playwright expectations to remove connectivity-status assertions and validate progress-summary reset plus terminal background cues.
Alternative considered: keep old assertions and add compatibility hooks.
This was rejected because it preserves obsolete UI contract.

## Risks / Trade-offs

[Risk] Progress reset triggers too aggressively and clears useful in-flight feedback.
→ Mitigation: reset only on user-initiated drawer parameter mutations and clear action, not on passive render refreshes.

[Risk] Background colors conflict with theme contrast or accessibility expectations.
→ Mitigation: use Vaadin-friendly theme variables or accessible contrast values, and assert via deterministic class hooks rather than raw color values where possible.

[Risk] Existing tests and screenshot baselines fail broadly after footer content removal.
→ Mitigation: update assertions and regenerate targeted screenshots in the same change.

## Migration Plan

Implement UI changes behind existing `MainView` behavior without configuration flags.
Update and run unit/browser tests that assert footer status text and styling.
Refresh Playwright screenshots affected by footer status content.
Rollback is a direct code revert of this change set if regressions are found.

## Open Questions

Should terminal success/failure styling persist until next compare, or clear immediately on any drawer edit.
This proposal assumes immediate clear on qualifying drawer edits and on Clear action.
