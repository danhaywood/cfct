## ADDED Requirements

### Requirement: Library exposes optional comparison progress listener registration
The system SHALL provide an optional callback or listener registration point for multi-table comparison execution.
The listener contract SHALL be available in public API service contracts for use by CLI and webapp consumers.
The system SHALL execute successfully when no listener is registered.

#### Scenario: Consumer registers listener
- **WHEN** a caller configures a multi-table comparison request with a progress listener
- **THEN** the library invokes that listener with progress events during execution

#### Scenario: Consumer does not register listener
- **WHEN** a caller runs multi-table comparison without a progress listener
- **THEN** comparison execution completes with unchanged behavior and without listener-related errors

### Requirement: Library emits deterministic table-level progress events
The system SHALL emit progress events when each table comparison starts and when each table comparison completes or fails.
Each progress event SHALL include table identity, lifecycle phase, completed-table count, and total-table count.
The system SHALL emit events in deterministic request-table order for non-terminal start and completion events.

#### Scenario: Progress events are emitted for each table
- **WHEN** a caller compares three tables with a registered listener
- **THEN** the listener receives start and completion-or-failure events for each table

#### Scenario: Progress counts advance predictably
- **WHEN** table comparison progresses from first to second table
- **THEN** completion counters and total-table count in events reflect monotonic progress toward total completion
