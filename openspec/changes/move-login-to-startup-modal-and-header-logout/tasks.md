## 1. Login modal refactor

- [x] 1.1 Extract current login form fields and submit behavior into a reusable UI component that can be embedded in a Vaadin dialog.
- [x] 1.2 Add a modal dialog in `MainView` that opens automatically for unauthenticated sessions and hosts the reusable login form component.
- [x] 1.3 Ensure modal configuration blocks dismissal without successful authentication and keeps protected comparison actions inaccessible while unauthenticated.
- [x] 1.4 Update authentication success handling to close modal and initialize authenticated table-selection state without full route hopping.

## 2. Navbar account menu and logout relocation

- [x] 2.1 Replace the existing standalone navbar logout button with a top-right `MenuBar` account menu container in `MainView`.
- [x] 2.2 Add a logout menu item that clears authenticated session context and returns the UI to unauthenticated modal-login state.
- [x] 2.3 Remove any left-navigation logout affordance and keep left drawer focused on selection-stage controls.

## 3. Route compatibility and UX hardening

- [x] 3.1 Update `MainView.beforeEnter` and related auth checks so unauthenticated access is gated by modal behavior rather than reroute to `/login`.
- [x] 3.2 Convert `LoginView` to compatibility behavior that redirects to main route so modal login remains the canonical entry experience.
- [x] 3.3 Add or update deterministic `data-testid` markers for login modal container, account menu, and logout menu item.

## 4. Verification and regression coverage

- [x] 4.1 Update Vaadin/browser tests to assert unauthenticated main-route entry shows login modal and blocks comparison interactions.
- [x] 4.2 Update tests to assert authenticated sessions show top-right account menu logout and no legacy standalone logout button in navigation.
- [x] 4.3 Run module tests and relevant UI test suite to verify login, logout, and protected-access regressions are covered.
