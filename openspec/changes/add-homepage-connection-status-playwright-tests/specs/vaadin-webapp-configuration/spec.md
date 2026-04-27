## ADDED Requirements

### Requirement: Home page surfaces SQL connectivity validation status
The webapp home page SHALL display SQL connectivity validation status as an explicit state that is either OK or FAILED.
The webapp SHALL display a concise failure summary when the status is FAILED.
The rendered status block SHALL be stable enough for deterministic browser-level assertions.

#### Scenario: Home page shows OK for successful validation
- **WHEN** startup SQL connectivity validation succeeds for configured server and databases
- **THEN** the home page shows connection status OK and no failure summary

#### Scenario: Home page shows FAILED for validation errors
- **WHEN** startup SQL connectivity validation reports a connectivity, authentication, or missing-database error
- **THEN** the home page shows connection status FAILED with a concise failure summary
