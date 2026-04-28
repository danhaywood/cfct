## Context

The current Vaadin web application initializes database connectivity from configuration properties at startup time.
This model is convenient for local demos but does not support per-user authentication or runtime environment switching.
The proposed change introduces a login-gated workflow where a user enters credentials and connection metadata in the UI.
The application must keep credentials scoped to the active HTTP session and must block comparison actions until authentication is successful.
The design needs to minimize impact on the core comparison engine while changing the webapp interaction model.

## Goals / Non-Goals

**Goals:**
- Introduce a clear login flow before access to comparison UI actions.
- Pre-fill login fields from existing configuration properties while keeping all fields user-editable.
- Store authenticated connection context in session scope rather than static config props.
- Ensure logout clears sensitive session state and returns users to login.
- Keep existing comparison execution and result rendering behavior unchanged after successful login.

**Non-Goals:**
- Building enterprise identity-provider integration such as OAuth2, SSO, or LDAP.
- Redesigning comparison algorithms, output formats, or CLI behavior.
- Persisting user credentials beyond the current runtime session.

## Decisions

1. Add a dedicated login view and gate navigation to the main comparison view.
Rationale: A distinct route keeps authorization concerns explicit and simplifies redirect behavior for unauthenticated sessions.
Alternative considered: Embedding login fields directly in the main view.
Why not chosen: It complicates state transitions and weakens route-level access protection.

2. Represent authenticated state as a session-scoped connection context object.
Rationale: Session scope aligns with Vaadin UI session lifecycle and avoids global mutable state.
Alternative considered: Singleton-scoped cached credentials.
Why not chosen: It risks cross-user credential leakage and is unsafe for concurrent users.

3. Build datasource instances on demand from the authenticated session context.
Rationale: On-demand construction avoids startup dependency on fixed props and supports user-selected endpoints.
Alternative considered: Maintaining app-wide datasource beans initialized from login events.
Why not chosen: Bean lifecycle management becomes complex and introduces stale connection risk after logout.

4. Use existing configuration properties as login defaults and allow users to override before submit.
Rationale: This preserves current convenience for known environments while enabling runtime auth changes without redeploy.
Alternative considered: Ignoring configuration defaults and requiring full manual entry every session.
Why not chosen: It adds friction and regresses existing operator ergonomics.

5. Store secrets in memory only and mask them in logs and UI summaries.
Rationale: This minimizes accidental credential disclosure while keeping implementation lightweight.
Alternative considered: Encrypting and persisting credentials in local storage.
Why not chosen: Persistence is outside scope and introduces additional security obligations.

## Risks / Trade-offs

- [Risk] Increased login failure cases due to invalid host, schema, or credentials.
  → Mitigation: Validate required fields early and present clear connection test errors before entering the main UI.
- [Risk] Session timeout may surprise users and lose connection state mid-workflow.
  → Mitigation: Display a clear timeout/login-expired message and allow fast re-authentication.
- [Risk] New auth gate could break existing automated webapp tests.
  → Mitigation: Update test setup to perform login first and add focused coverage for auth transitions.
- [Trade-off] Removing config-driven auto-connect increases initial user steps.
  → Mitigation: Provide sensible defaults for non-secret fields and good form UX.

## Migration Plan

Deploy the login-gated webapp with config-prop auto-connect disabled by default.
Keep existing webapp config properties available as login defaults while removing mandatory dependency on static credentials for access.
Update README and operator docs to describe login-first usage.
Rollback by re-enabling the previous config-prop initialization path behind a temporary feature toggle if required.

## Open Questions

Should the login form support separate credentials for source and target databases or a single shared credential profile.
Should there be an explicit connection-test button, or should test-and-login be a single submit action.
What session timeout duration best balances security and usability for the expected user workflow.
