## Why

The current login form in the left navigation competes with table selection and comparison controls, which makes first-time access and authenticated usage harder to understand.
Moving authentication to a startup modal and moving logout to a top-right menu action creates a clearer entry flow and aligns account actions with common webapp navigation conventions.

## What Changes

- Show authentication in a modal dialog when the app starts for unauthenticated sessions instead of rendering login controls in the left navigation area.
- Keep protected comparison content inaccessible until modal login succeeds.
- Add a top-level top-right user menu action for logout.
- Remove logout from left-side navigation controls.
- Preserve current session-scoped authentication context and post-logout re-authentication behavior.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-login-connection-auth`: Change the login interaction model to a startup modal dialog for unauthenticated sessions, and specify logout access from a top-level menu action.
- `webapp-main-ui-layout`: Change authenticated shell layout expectations so left navigation focuses on comparison selection and top-level menu hosts account actions.

## Impact

- Vaadin views and layout components that currently render login and logout actions in the left panel.
- Authentication flow wiring for route guards and initial app entry.
- Browser-level tests for login visibility, protected access, and logout location/behavior.
- Minimal adjustments to UI composition only, with no intended changes to core comparison API contracts.
