## ADDED Requirements

### Requirement: Manual business table grid excludes system support tables
The webapp SHALL exclude command-log, audit-trail, and logical-type mapping tables from the manual business table grid.
The exclusion SHALL include `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
Excluded system support tables SHALL not appear in manual table-grid filtering or selection results.

#### Scenario: Business table grid omits command and audit support tables
- **WHEN** the home page loads manual table-selection data
- **THEN** `causewayExtCommandLog.CommandLogEntry` and `causewayExtAuditTrail.AuditTrailEntry` are not shown in the manual table grid

#### Scenario: Business table grid omits logical-type mapping support table
- **WHEN** the home page loads manual table-selection data
- **THEN** `util.LogicalTypeTableMapping` is not shown in the manual table grid

#### Scenario: Remaining business tables still participate in grid interactions
- **WHEN** the user filters or selects rows in the manual table grid
- **THEN** only non-excluded business tables are considered for filtering matches and selection state
