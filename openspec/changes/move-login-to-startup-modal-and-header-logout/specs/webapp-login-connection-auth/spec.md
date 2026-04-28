## MODIFIED Requirements

### Requirement: User can authenticate database connections from a login form
The webapp SHALL provide a login form that captures runtime connection inputs required to authenticate source and target SQL Server databases.
The login form SHALL be presented in a modal dialog on initial app entry when the current session is unauthenticated.
The login form SHALL require server identity, source database, target database, username, and password before allowing submission.
The login form SHALL pre-populate fields from existing webapp configuration properties when those properties are present.
The login form SHALL keep all pre-populated values user-editable before submission.
The login modal SHALL remain open until authentication succeeds or the session is otherwise authenticated.

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
