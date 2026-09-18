## ADDED Requirements

### Requirement: Automation response includes independent execution timing observations
Every successful settled automation comparison response SHALL include a top-level `executionTiming` object for the selected foreground command scope.
The object SHALL contain separate foreground and background sections.
The foreground section SHALL contain independent app-a and app-b observations.
The background section SHALL contain independent app-a and app-b observation arrays built from each side's terminal descendant set.
Timing observations SHALL conform to the command execution timing capability and SHALL contain no target bookmarks.
The execution timing object SHALL be additive and SHALL NOT alter existing business, audit, background-command, or HTTP outcome semantics.

#### Scenario: Settled comparison includes execution timing
- **WHEN** an authenticated automation comparison settles for the selected foreground command
- **THEN** the successful JSON response includes `executionTiming.foreground.appA` and `executionTiming.foreground.appB`
- **AND** the response includes `executionTiming.background.appA` and `executionTiming.background.appB` arrays

#### Scenario: No background commands exist
- **WHEN** the selected foreground command has no background descendants on either side
- **THEN** both execution timing background arrays are empty
- **AND** foreground timing observations remain present

#### Scenario: Background identifiers differ across sides
- **WHEN** app-a and app-b terminal background observations have different interaction identifiers
- **THEN** the response includes each side's observations independently
- **AND** the endpoint does not reject or correlate them by identifier

#### Scenario: No business tables are resolved
- **WHEN** the selected foreground command resolves no eligible business tables
- **THEN** the successful empty business comparison response still includes execution timing for the foreground command and its terminal background descendants

#### Scenario: Latest-command startup refresh includes execution timing
- **WHEN** the automation endpoint refreshes the latest completed foreground command during a startup re-check
- **THEN** the response includes execution timing derived from that command scope

#### Scenario: Timing differs without business or audit divergence
- **WHEN** app-a and app-b command durations differ
- **AND** business tables and audit structure agree
- **THEN** the response retains clean business and audit outcomes
- **AND** exposes the differing raw timing observations without a timing pass/fail classification
