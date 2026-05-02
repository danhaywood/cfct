# webapp-login-connection-auth Specification

## Purpose
TBD - created by archiving change replace-config-props-with-login-auth. Update Purpose after archive.
## Requirements
### Requirement: User can authenticate database connections from a login form
The webapp SHALL provide a login form that captures runtime connection inputs required to authenticate source and target SQL Server databases.
The login form SHALL be presented in a modal dialog on initial app entry when the current session is unauthenticated.
The login form SHALL require server identity, source database, target database, username, and password before allowing submission.
The login form SHALL pre-populate fields from existing webapp configuration properties when those properties are present.
The login form SHALL keep all pre-populated values user-editable before submission.
The login modal SHALL remain open until authentication succeeds or the session is otherwise authenticated.
The login experience SHALL include a branding area that renders the product logo/icon in a right-side placement relative to the form on standard desktop layouts.

#### Scenario: Login form enforces required connection fields
- **WHEN** a user submits the login form with one or more required fields missing
- **THEN** the webapp rejects submission and shows validation messages for each missing field

#### Scenario: Login form uses configuration values as defaults
- **WHEN** matching connection properties are configured for the webapp
- **THEN** the login form renders those property values as editable defaults

#### Scenario: Login form accepts valid runtime credentials
- **WHEN** a user submits the login form with all required connection fields present
- **THEN** the webapp starts authentication and connectivity validation for the supplied connection inputs

#### Scenario: Unauthenticated app entry opens login modal
- **WHEN** an unauthenticated user opens the main route
- **THEN** the login form is shown as an open modal dialog before comparison interactions are available

#### Scenario: Login modal shows right-side branding
- **WHEN** the login modal is rendered on a desktop-width viewport
- **THEN** a branding area with the logo/icon is visible on the right side of the login content

### Requirement: Successful login creates session-scoped authenticated connection context
The webapp SHALL create a session-scoped authenticated connection context after successful credential and connectivity validation.
The authenticated connection context SHALL be isolated per user session and SHALL NOT be shared across sessions.
The authenticated connection context SHALL include only values needed for comparison operations and SHALL avoid exposing raw secrets in logs or UI summaries.
The webapp SHALL move keyboard focus to the command selection grid immediately after successful login transition.
Connectivity validation SHALL include required target-database system object checks before session context creation.
Required target objects SHALL be verified through `INFORMATION_SCHEMA.TABLES` and MAY be implemented as either tables or views.

#### Scenario: Authentication succeeds and session context is stored
- **WHEN** submitted login credentials can authenticate, both requested databases are reachable, and required target objects are present
- **THEN** the webapp stores authenticated connection context in the current session and marks the user as logged in

#### Scenario: Authentication failure does not create session context
- **WHEN** submitted login credentials fail authentication, database reachability checks, or required target-object checks
- **THEN** the webapp keeps the user unauthenticated and no authenticated connection context is stored

#### Scenario: Authentication success focuses command grid
- **WHEN** login succeeds and the main shell becomes interactive
- **THEN** keyboard focus is placed on the command selection grid

### Requirement: Comparison features are access-controlled by authentication state
The webapp SHALL block comparison actions and protected comparison routes when no authenticated session context exists.
The webapp SHALL keep protected comparison interactions disabled or inaccessible while the unauthenticated login modal is active.
The webapp SHALL allow comparison actions immediately after authenticated session context is established.

#### Scenario: Unauthenticated access is blocked by modal gate
- **WHEN** an unauthenticated user opens the comparison workflow route
- **THEN** the webapp shows the login modal and blocks protected comparison interactions until authentication succeeds

#### Scenario: Authenticated access reaches comparison workflow
- **WHEN** a user with authenticated session context navigates to the comparison workflow
- **THEN** the webapp allows access without re-entering credentials

### Requirement: Logout clears authenticated session state
The webapp SHALL provide a logout action that removes authenticated connection context from the active session.
The logout action SHALL be available from a top-level menu in the top-right navbar area.
The webapp SHALL return users to an unauthenticated state after logout and require re-authentication before any subsequent comparison action.

#### Scenario: Logout invalidates authenticated session context
- **WHEN** a logged-in user activates logout from the top-level menu
- **THEN** the authenticated connection context is removed from session scope and protected workflows become inaccessible

#### Scenario: Post-logout comparison attempt requires login
- **WHEN** a user who has logged out tries to run or open comparison features
- **THEN** the webapp shows login and requires new authentication before comparison workflows continue

