## MODIFIED Requirements

### Requirement: Logout clears authenticated session state
The webapp SHALL provide a logout action that removes authenticated connection context from the active session.
The logout action SHALL be available from a top-level menu in the top-right navbar area.
The top-level account menu label SHALL include authenticated username context when available.
The webapp SHALL return users to an unauthenticated state after logout and require re-authentication before any subsequent comparison action.

#### Scenario: Logout invalidates authenticated session context
- **WHEN** a logged-in user activates logout from the top-level menu
- **THEN** the authenticated connection context is removed from session scope and protected workflows become inaccessible

#### Scenario: Post-logout comparison attempt requires login
- **WHEN** a user who has logged out tries to run or open comparison features
- **THEN** the webapp shows login and requires new authentication before comparison workflows continue

#### Scenario: Top-level account menu includes username context
- **WHEN** a user is authenticated and a username is available in session context
- **THEN** the top-right account menu label includes that username