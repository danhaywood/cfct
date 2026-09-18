## ADDED Requirements

### Requirement: Read command execution timing observations independently per side
CFCT SHALL read execution timing observations for the selected foreground command independently from app-a and app-b.
CFCT SHALL read terminal background descendant observations from each side's own descendant set.
Each observation SHALL include its scope, logical member identifier, replay status, start time, completion time, nullable duration milliseconds, and side-local interaction identity.
A background observation SHALL include its side-local parent interaction identity when available.
CFCT SHALL NOT require app-a and app-b background interaction or transaction identifiers to match.

#### Scenario: Foreground timing is available on both sides
- **WHEN** a settled comparison selects a foreground command whose app-a and app-b command-log entries contain valid start and completion timestamps
- **THEN** CFCT returns one foreground observation for each side
- **AND** both observations identify the logical member and their side-local command identities

#### Scenario: Background timing is discovered independently
- **WHEN** the selected foreground command has terminal background descendants on app-a and app-b
- **THEN** CFCT returns each side's observations from that side's own descendant set
- **AND** CFCT does not pair or reject observations based on child interaction identifier equality

#### Scenario: Failed background command retains timing facts
- **WHEN** a background descendant is terminal with a failed replay status
- **THEN** CFCT includes its timing observation and failed status
- **AND** existing failed-background outcome handling remains unchanged

### Requirement: Calculate duration from command-local execution timestamps
CFCT SHALL calculate `durationMillis` as the elapsed time from an observation's `startedAt` to its `completedAt`.
CFCT SHALL NOT use the command's nominal replay timestamp or the interval between different commands to calculate duration.
CFCT SHALL represent duration as unavailable when either execution timestamp is absent or completion precedes start.
CFCT SHALL NOT substitute zero for an unavailable duration.

#### Scenario: Replay clock advances between commands
- **WHEN** nominal replay timestamps contain seconds, minutes, or hours between consecutive commands
- **AND** each command has realistic start and completion timestamps
- **THEN** each duration is calculated only from that command's own start and completion timestamps

#### Scenario: Completion timestamp is absent
- **WHEN** a timing observation has no completion timestamp
- **THEN** its duration is unavailable
- **AND** the observation is not reported as a zero-duration command

#### Scenario: Timestamp interval is invalid
- **WHEN** an observation's completion timestamp precedes its start timestamp
- **THEN** its duration is unavailable

### Requirement: Exclude targets and preserve deterministic timing output
Execution timing observations SHALL omit target bookmarks and target object identifiers.
Background timing observations SHALL be ordered deterministically by logical member identifier, start time, and side-local interaction identifier using null-safe ordering.
Interaction identifiers in timing output SHALL be treated as side-local deduplication identities rather than cross-side correlation keys.

#### Scenario: Timing observation is serialized
- **WHEN** CFCT serializes a foreground or background timing observation
- **THEN** the observation contains no target bookmark or target object identifier
- **AND** it retains its logical member identifier

#### Scenario: Repeated timing retrieval is stable
- **WHEN** the same settled command scope is read repeatedly without database changes
- **THEN** CFCT returns background timing observations in the same order

### Requirement: Keep timing independent from comparison outcomes
Execution timing availability and movement SHALL NOT alter business-table `hasDifferences`, audit-trail `hasDifferences`, background command status, or automation HTTP outcome.
CFCT SHALL expose timing facts without classifying them as matched, warned, failed, improved, or regressed.

#### Scenario: Durations differ between applications
- **WHEN** app-a and app-b report different durations for the same logical member identifier
- **THEN** CFCT exposes both timing facts
- **AND** the difference does not alter business or audit comparison outcomes

#### Scenario: Timing is incomplete
- **WHEN** one or more timing observations have unavailable duration
- **THEN** CFCT exposes the available facts
- **AND** timing incompleteness alone does not fail the automation comparison
