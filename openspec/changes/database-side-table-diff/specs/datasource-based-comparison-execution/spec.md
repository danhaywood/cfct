## MODIFIED Requirements

### Requirement: DataSource-driven execution preserves existing comparison outcomes
DataSource-based execution SHALL preserve the same comparison result semantics as prior Connection-based flow.
The migration SHALL NOT change key discovery, row matching, difference detection, or report payload content.
DataSource-based execution SHALL provide in-memory per-table comparison result structures for webapp-driven compare actions.
DataSource-based execution SHALL generate and run one composed SQL diff query per compared table that combines left-only, right-only, and value-different branches.
The composed query SHALL execute through datasource-managed JDBC connections.
The composed query SHALL remain executable when the compared databases are configured with SQL Server compatibility level 100.
The execution SHALL avoid fetching full left and right table rowsets into application memory.

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
