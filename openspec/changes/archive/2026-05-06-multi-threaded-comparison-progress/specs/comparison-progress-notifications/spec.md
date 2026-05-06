## MODIFIED Requirements

### Requirement: Library emits deterministic table-level progress events
The system SHALL emit progress events when each table comparison starts and when each table comparison completes or fails.
Each progress event SHALL include table identity, lifecycle phase, completed-table count, and total-table count.
The system SHALL emit start events in deterministic request-table order.
The system SHALL emit completion and failure events as each table actually finishes.
The completed-table count in completion and failure events SHALL increase monotonically until it equals total-table count.

#### Scenario: Progress events are emitted for each table
- **WHEN** a caller compares three tables with a registered listener
- **THEN** the listener receives start and completion-or-failure events for each table

#### Scenario: Progress counts advance predictably
- **WHEN** table comparison progresses from first completion to subsequent completions
- **THEN** completion counters and total-table count in events reflect monotonic progress toward total completion

#### Scenario: Completion events reflect asynchronous finish order
- **WHEN** two tables are compared concurrently and the second requested table finishes first
- **THEN** the completion event for the second requested table may be observed before the first requested table completion event
