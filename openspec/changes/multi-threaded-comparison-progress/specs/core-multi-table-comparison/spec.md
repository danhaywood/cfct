## MODIFIED Requirements

### Requirement: Core library compares a caller-specified set of tables
The system SHALL provide a core library API that compares a caller-specified collection of SQL Server tables between a left JDBC connection and a right JDBC connection.
The API SHALL compare exactly the requested tables.
The API SHALL support optional registration of a progress listener for table-level comparison lifecycle events.
The API SHALL execute requested table comparisons using bounded parallelism with a configurable maximum worker count.
The API SHALL emit a start event before each requested table comparison begins.
The API SHALL emit a completion or failure event after each requested table comparison finishes.
The API SHALL assemble the final multi-table result in the original request order regardless of completion order.

#### Scenario: Caller compares selected tables
- **WHEN** a caller provides left and right JDBC connections and a collection of table references
- **THEN** the core library compares each requested table

#### Scenario: Unrequested tables are not compared
- **WHEN** comparable tables exist in the databases but are not included in the request
- **THEN** the core library does not compare those unrequested tables

#### Scenario: Empty table set is rejected
- **WHEN** a caller requests a multi-table comparison with no table references
- **THEN** the core library fails with a clear validation error

#### Scenario: Comparison execution is bounded-concurrent
- **WHEN** a caller provides multiple table references and parallelism is configured above one worker
- **THEN** the core library executes table comparisons concurrently without exceeding the configured worker bound

#### Scenario: Progress listener receives per-table lifecycle notifications during concurrent execution
- **WHEN** a caller provides a progress listener and requests multiple tables
- **THEN** the core library invokes the listener for each table start and completion-or-failure lifecycle point
- **AND** completion notifications reflect actual finish order under concurrent execution

#### Scenario: Result preserves request order under concurrent execution
- **WHEN** requested tables complete in a different order than selected
- **THEN** the returned multi-table result entries remain ordered by the original request sequence
