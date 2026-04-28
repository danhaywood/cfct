## ADDED Requirements

### Requirement: User can authenticate database connections from a login form
The webapp SHALL provide a login form that captures runtime connection inputs required to authenticate source and target SQL Server databases.
The login form SHALL require server identity, source database, target database, username, and password before allowing submission.
The login form SHALL pre-populate fields from existing webapp configuration properties when those properties are present.
The login form SHALL keep all pre-populated values user-editable before submission.

#### Scenario: Login form enforces required connection fields
- **WHEN** a user submits the login form with one or more required fields missing
- **THEN** the webapp rejects submission and shows validation messages for each missing field

#### Scenario: Login form uses configuration values as defaults
- **WHEN** matching connection properties are configured for the webapp
- **THEN** the login form renders those property values as editable defaults

#### Scenario: Login form accepts valid runtime credentials
- **WHEN** a user submits the login form with all required connection fields present
- **THEN** the webapp starts authentication and connectivity validation for the supplied connection inputs

### Requirement: Successful login creates session-scoped authenticated connection context
The webapp SHALL create a session-scoped authenticated connection context after successful credential and connectivity validation.
The authenticated connection context SHALL be isolated per user session and SHALL NOT be shared across sessions.
The authenticated connection context SHALL include only values needed for comparison operations and SHALL avoid exposing raw secrets in logs or UI summaries.

#### Scenario: Authentication succeeds and session context is stored
- **WHEN** submitted login credentials can authenticate and both requested databases are reachable
- **THEN** the webapp stores authenticated connection context in the current session and marks the user as logged in

#### Scenario: Authentication failure does not create session context
- **WHEN** submitted login credentials fail authentication or database reachability checks
- **THEN** the webapp keeps the user unauthenticated and no authenticated connection context is stored

### Requirement: Comparison features are access-controlled by authentication state
The webapp SHALL block comparison actions and protected comparison routes when no authenticated session context exists.
The webapp SHALL redirect unauthenticated users to the login view before they can access comparison workflows.
The webapp SHALL allow comparison actions immediately after authenticated session context is established.

#### Scenario: Unauthenticated access is redirected to login
- **WHEN** an unauthenticated user attempts to open the comparison workflow
- **THEN** the webapp redirects the user to the login view

#### Scenario: Authenticated access reaches comparison workflow
- **WHEN** a user with authenticated session context navigates to the comparison workflow
- **THEN** the webapp allows access without re-entering credentials

### Requirement: Logout clears authenticated session state
The webapp SHALL provide a logout action that removes authenticated connection context from the active session.
The webapp SHALL return users to the login view after logout.
The webapp SHALL require re-authentication before any subsequent comparison action after logout.

#### Scenario: Logout invalidates authenticated session context
- **WHEN** a logged-in user activates logout
- **THEN** the authenticated connection context is removed from session scope and protected workflows become inaccessible

#### Scenario: Post-logout comparison attempt requires login
- **WHEN** a user who has logged out tries to run or open comparison features
- **THEN** the webapp redirects the user to login and requires new authentication
