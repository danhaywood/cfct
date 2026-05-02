## MODIFIED Requirements

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
