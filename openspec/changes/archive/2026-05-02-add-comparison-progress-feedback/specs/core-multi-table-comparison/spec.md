## MODIFIED Requirements

### Requirement: Core library compares a caller-specified set of tables
The system SHALL provide a core library API that compares a caller-specified collection of SQL Server tables between a left JDBC connection and a right JDBC connection.
The API SHALL compare exactly the requested tables.
The API SHALL support optional registration of a progress listener for table-level comparison lifecycle events.
The API SHALL emit a start event before each requested table comparison begins.
The API SHALL emit a completion or failure event after each requested table comparison finishes.

#### Scenario: Caller compares selected tables
- **WHEN** a caller provides left and right JDBC connections and a collection of table references
- **THEN** the core library compares each requested table

#### Scenario: Unrequested tables are not compared
- **WHEN** comparable tables exist in the databases but are not included in the request
- **THEN** the core library does not compare those unrequested tables

#### Scenario: Empty table set is rejected
- **WHEN** a caller requests a multi-table comparison with no table references
- **THEN** the core library fails with a clear validation error

#### Scenario: Progress listener receives per-table lifecycle notifications
- **WHEN** a caller provides a progress listener and requests multiple tables
- **THEN** the core library invokes the listener for each table start and completion-or-failure lifecycle point in request order
