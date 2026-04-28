## 1. Session auth model and configuration refactor

- [ ] 1.1 Introduce a session-scoped authenticated connection context model that stores non-secret display fields and runtime credential material needed for execution.
- [ ] 1.2 Refactor webapp typed configuration so existing connection props become login defaults and are no longer mandatory for startup/auth access.
- [ ] 1.3 Update connection bootstrap services to build datasource access from authenticated session context instead of startup static credential props.

## 2. Login and logout user experience

- [ ] 2.1 Implement a dedicated login view with required fields for server, source database, target database, username, and password.
- [ ] 2.2 Bind existing config property values into the login form as initial editable defaults.
- [ ] 2.3 Add login submission flow that validates required fields, performs connectivity/authentication checks, and stores session auth context only on success.
- [ ] 2.4 Implement logout action that clears authenticated session state and redirects users to the login view.

## 3. Access control and main view integration

- [ ] 3.1 Gate protected comparison routes and actions so unauthenticated users are redirected to login.
- [ ] 3.2 Update main comparison view to read active connection context from session auth state rather than static configuration credentials.
- [ ] 3.3 Update footer or status bar rendering to show authenticated connection context while masking sensitive credential values.

## 4. Validation, tests, and documentation

- [ ] 4.1 Add or update tests for login validation failures, successful authentication, route protection, and logout behavior.
- [ ] 4.2 Update existing webapp tests to perform login before exercising comparison workflows.
- [ ] 4.3 Update user documentation to describe login-first usage, runtime credential entry, and expected session behavior.
