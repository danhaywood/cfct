## MODIFIED Requirements

### Requirement: Webapp validates configured SQL Server connectivity and databases
The webapp SHALL validate SQL Server connectivity and database reachability using runtime login credentials instead of startup-time static credentials.
The webapp SHALL execute connectivity validation during login or explicit connection test flow before granting access to comparison workflows.
The webapp SHALL validate that required target-database system objects are present using `INFORMATION_SCHEMA.TABLES` in the target database context.
The required target objects SHALL include `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
The required-object check SHALL accept both `BASE TABLE` and `VIEW` entries for each required object.
The webapp SHALL fail login with clear diagnostics when connectivity fails, authentication fails, requested databases are missing, or required target objects are missing.

#### Scenario: Valid runtime credentials allow authentication to complete
- **WHEN** runtime login credentials can connect to the SQL Server endpoint, both requested databases exist, and required target objects are present as tables or views
- **THEN** connectivity validation succeeds and the user is authenticated for comparison workflows

#### Scenario: Invalid runtime credentials fail authentication
- **WHEN** runtime login credentials are invalid or one requested database is unavailable
- **THEN** authentication fails with a clear validation error and comparison workflows remain inaccessible

#### Scenario: Missing required target system objects fail authentication
- **WHEN** runtime login credentials are valid and requested databases exist but one or more required target objects are missing
- **THEN** authentication fails with a clear validation error that lists missing schema-qualified object names
