## 1. Session auth model and configuration refactor

- [x] 1.1 Introduce a session-scoped authenticated connection context model that stores non-secret display fields and runtime credential material needed for execution.
- [x] 1.2 Refactor webapp typed configuration so existing connection props become login defaults and are no longer mandatory for startup/auth access.
- [x] 1.3 Update connection bootstrap services to build datasource access from authenticated session context instead of startup static credential props.

## 2. Login and logout user experience

- [x] 2.1 Implement a dedicated login view with required fields for server, source database, target database, username, and password.
- [x] 2.2 Bind existing config property values into the login form as initial editable defaults.
- [x] 2.3 Add login submission flow that validates required fields, performs connectivity/authentication checks, and stores session auth context only on success.
- [x] 2.4 Implement logout action that clears authenticated session state and redirects users to the login view.

## 3. Access control and main view integration

- [x] 3.1 Gate protected comparison routes and actions so unauthenticated users are redirected to login.
- [x] 3.2 Update main comparison view to read active connection context from session auth state rather than static configuration credentials.
- [x] 3.3 Update footer or status bar rendering to show authenticated connection context while masking sensitive credential values.

## 4. Validation, tests, and documentation

- [x] 4.1 Add or update tests for login validation failures, successful authentication, route protection, and logout behavior.
- [x] 4.2 Update existing webapp tests to perform login before exercising comparison workflows.
- [x] 4.3 Update user documentation to describe login-first usage, runtime credential entry, and expected session behavior.
