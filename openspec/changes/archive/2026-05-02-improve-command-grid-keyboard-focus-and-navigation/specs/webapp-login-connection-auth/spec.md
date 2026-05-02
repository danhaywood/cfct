## MODIFIED Requirements

### Requirement: Successful login creates session-scoped authenticated connection context
The webapp SHALL create a session-scoped authenticated connection context after successful credential and connectivity validation.
The authenticated connection context SHALL be isolated per user session and SHALL NOT be shared across sessions.
The authenticated connection context SHALL include only values needed for comparison operations and SHALL avoid exposing raw secrets in logs or UI summaries.
The webapp SHALL move keyboard focus to the command selection grid immediately after successful login transition.

#### Scenario: Authentication succeeds and session context is stored
- **WHEN** submitted login credentials can authenticate and both requested databases are reachable
- **THEN** the webapp stores authenticated connection context in the current session and marks the user as logged in

#### Scenario: Authentication failure does not create session context
- **WHEN** submitted login credentials fail authentication or database reachability checks
- **THEN** the webapp keeps the user unauthenticated and no authenticated connection context is stored

#### Scenario: Authentication success focuses command grid
- **WHEN** login succeeds and the main shell becomes interactive
- **THEN** keyboard focus is placed on the command selection grid
