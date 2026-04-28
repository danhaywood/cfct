## MODIFIED Requirements

### Requirement: DataSource-driven execution preserves existing comparison outcomes
DataSource-based execution SHALL preserve the same comparison result semantics as prior Connection-based flow.
The migration SHALL NOT change key discovery, row matching, difference detection, or report payload content.
DataSource-based execution SHALL provide in-memory per-table comparison result structures for webapp-driven compare actions.

#### Scenario: Existing fixture comparisons remain deterministic after migration
- **WHEN** existing integration fixtures are compared through DataSource-based execution
- **THEN** the reported comparison summaries and deterministic approval outputs remain unchanged

#### Scenario: DataSource execution returns in-memory multi-table result payload
- **WHEN** the webapp executes comparison for selected tables through datasource-managed orchestration
- **THEN** the execution returns in-memory per-table comparison results that can be rendered without intermediate file marshalling
