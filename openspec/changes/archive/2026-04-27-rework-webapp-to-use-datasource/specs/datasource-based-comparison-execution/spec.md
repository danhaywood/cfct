## ADDED Requirements

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

#### Scenario: Existing fixture comparisons remain deterministic after migration
- **WHEN** existing integration fixtures are compared through DataSource-based execution
- **THEN** the reported comparison summaries and deterministic approval outputs remain unchanged
