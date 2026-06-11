# datasource-based-comparison-execution Specification

## Purpose
TBD - created by archiving change rework-webapp-to-use-datasource. Update Purpose after archive.
## Requirements
### Requirement: Webapp comparison execution uses DataSource-managed connections
The webapp SHALL execute comparison requests by acquiring JDBC connections from configured `DataSource` beans.
The webapp SHALL open and close these connections within service execution boundaries.
The webapp SHALL avoid storing long-lived `Connection` instances in web-layer state.

#### Scenario: Comparison execution acquires and closes left and right connections
- **WHEN** the webapp starts a comparison run
- **THEN** it acquires left and right connections from configured DataSources and closes both connections after execution

#### Scenario: Connection acquisition failure surfaces a clear webapp error
- **WHEN** either DataSource cannot provide a connection
- **THEN** the webapp reports a clear failure without leaking partially-opened resources

### Requirement: DataSource-driven execution preserves existing comparison outcomes
DataSource-based execution SHALL preserve the same comparison result semantics as prior Connection-based flow.
The migration SHALL NOT change key discovery, row matching, difference detection, or report payload content.
DataSource-based execution SHALL provide in-memory per-table comparison result structures for webapp-driven compare actions.
DataSource-based execution SHALL generate and run one composed SQL diff query per compared table that combines left-only, right-only, and value-different branches.
The composed query SHALL execute through datasource-managed JDBC connections.
The composed query SHALL remain executable when the compared databases are configured with SQL Server compatibility level 100.
The execution SHALL avoid fetching full left and right table rowsets into application memory.
The execution layer SHALL support opt-in SQL statement tracing using datasource-proxy wrappers that log emitted SQL statements before execution.

#### Scenario: Existing fixture comparisons remain deterministic after migration
- **WHEN** existing integration fixtures are compared through DataSource-based execution
- **THEN** the reported comparison summaries and deterministic approval outputs remain unchanged

#### Scenario: DataSource execution returns in-memory multi-table result payload
- **WHEN** the webapp executes comparison for selected tables through datasource-managed orchestration
- **THEN** the execution returns in-memory per-table comparison results that can be rendered without intermediate file marshalling

#### Scenario: Diff query returns only non-matching or changed rows
- **WHEN** datasource-based execution compares a table where most rows are equal between source and target
- **THEN** the SQL execution returns only left-only, right-only, and value-different rows for mapping into comparison results

#### Scenario: Diff query executes under compatibility level 100
- **WHEN** datasource-based execution runs against SQL Server databases configured with compatibility level 100
- **THEN** the generated query executes successfully without syntax errors related to unsupported language features

#### Scenario: SQL tracing is available for verification
- **WHEN** SQL tracing is enabled for a comparison run
- **THEN** emitted SQL statements are logged before execution so operators can verify generated SQL text

### Requirement: Command reselection cancels active comparison execution
The webapp SHALL cancel an in-progress comparison run when a user selects an individual command row.
The cancellation SHALL complete before the newly selected command is applied to comparison eligibility state.
The cancellation flow SHALL use the same user-visible cancellation semantics as explicit comparison cancel actions.
After cancellation from command reselection, transient comparison progress indicators and stale status report content SHALL be cleared.

#### Scenario: Selecting a command during compare cancels active run
- **WHEN** a comparison run is in progress
- **AND** the user selects an individual command row
- **THEN** the in-progress comparison run is cancelled
- **AND** the new command selection is applied after cancellation

#### Scenario: Reselection cancellation clears transient comparison state
- **WHEN** a comparison run is cancelled due to command row selection
- **THEN** progress indicators for the cancelled run are cleared
- **AND** stale comparison status report content is removed before the next compare action

