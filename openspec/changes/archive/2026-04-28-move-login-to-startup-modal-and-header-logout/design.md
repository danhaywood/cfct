## Context

The current webapp uses a dedicated `LoginView` route and keeps logout as a standalone button in the `MainView` navbar.
The comparison shell already uses `AppLayout`, a drawer for table selection, and a guarded `beforeEnter` redirect to `/login` when unauthenticated.
The requested UX shifts login to a first-start modal so users stay on the main route while authenticating.
The requested UX also shifts logout from a persistent button to an account action in a top-right menu.

## Goals / Non-Goals

**Goals:**
- Present authentication in a modal dialog on main route entry when session context is missing.
- Keep main comparison UI inaccessible for interaction until authentication succeeds.
- Place logout inside a top-right menu component that is visible only for authenticated sessions.
- Preserve existing authentication service contracts, session storage behavior, and connectivity validation flow.

**Non-Goals:**
- Rework comparison orchestration, table discovery, or result rendering behaviors.
- Introduce role-based authorization or multi-user account management.
- Change login field semantics, required inputs, or validation messages beyond modal presentation.

## Decisions

1. Build a reusable login form component and host it inside a Vaadin `Dialog` in `MainView`.
Rationale: This keeps one source of truth for login field behavior while enabling modal presentation on the app root.
Alternative considered: Keep `LoginView` and open it in a separate navigation step.
Why not chosen: It preserves route hopping and does not meet the “first start modal” requirement.

2. Keep route-level protection in `MainView.beforeEnter`, but stop rerouting to `/login` and instead enforce modal-open state for unauthenticated sessions.
Rationale: Users remain on the home route, while protection is still enforced through disabled/hidden protected actions and a modal gate.
Alternative considered: Remove route guard and rely only on component visibility checks.
Why not chosen: Route guard behavior remains a clearer centralized protection point and avoids accidental unauthenticated interactions.

3. Replace the navbar logout button with a right-aligned account `MenuBar` action item.
Rationale: Menu placement in the top-right matches conventional app shell behavior and declutters primary navigation controls.
Alternative considered: Keep logout as a button but move it with CSS to the right.
Why not chosen: A menu structure is more extensible for future account actions and clearer to test semantically.

4. Keep `LoginView` temporarily as a compatibility route that redirects authenticated users to main and unauthenticated users to main where the modal is shown.
Rationale: This avoids breaking existing bookmarks/tests immediately while converging behavior on a single entry route.
Alternative considered: Delete `LoginView` now.
Why not chosen: Removal can be deferred to a follow-up cleanup once tests and links are updated.

## Risks / Trade-offs

- Modal focus trap or closing behavior could allow partial access to background content.
  → Mitigation: Use non-closable modal settings until successful authentication and keep protected controls disabled when unauthenticated.
- Existing browser tests that expect `/login` navigation may fail.
  → Mitigation: Update tests to assert modal presence on main route and add stable modal/menu test IDs.
- Dual entry behavior (`/` and `/login`) could cause temporary conceptual duplication.
  → Mitigation: Keep `/login` as a thin compatibility route with explicit redirect intent and plan later removal.

## Migration Plan

1. Extract shared login form logic into a component that emits submit events with `ConnectionLoginRequest`.
2. Integrate the component into a modal dialog opened from `MainView` for unauthenticated sessions.
3. Add account menu in the top-right navbar and move logout action into menu item handling.
4. Update `LoginView` behavior to redirect to main route so modal-driven login is canonical.
5. Update UI and browser tests for modal login flow and top-right logout location.

## Open Questions

No blocking open questions were identified for this change.
