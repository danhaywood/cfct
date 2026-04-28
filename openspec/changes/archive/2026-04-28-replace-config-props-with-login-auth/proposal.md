## Why

The web app currently depends on database connection properties loaded from configuration, which prevents end users from supplying their own credentials at runtime.
We need an authenticated login flow so users can connect interactively without rebuilding or redeploying the application for different environments.

## What Changes

- Add a login experience in the Vaadin web app where users enter connection details before using comparison features.
- Replace startup-time config-property connection bootstrapping for interactive users with session-scoped authenticated connection state.
- Pre-populate login form fields from existing webapp configuration properties as editable defaults.
- Persist login state only for the active user session and provide logout/re-authentication behavior.
- Protect comparison actions so they are unavailable until login succeeds.

## Capabilities

### New Capabilities
- `webapp-login-connection-auth`: Interactive user login and session authentication for source and target database connections.

### Modified Capabilities
- `vaadin-webapp-configuration`: Change requirements from mandatory config-provided runtime connection props to login-gated runtime credential entry.

## Impact

- Vaadin UI flow and navigation lifecycle.
- Webapp connection initialization, default-value mapping, and credential handling code.
- Session management and authorization checks around comparison actions.
- User-facing documentation for starting and using the web app.
