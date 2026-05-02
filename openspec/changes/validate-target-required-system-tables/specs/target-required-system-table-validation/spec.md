## ADDED Requirements

### Requirement: Target database required system objects are validated via INFORMATION_SCHEMA
The webapp SHALL validate required target-database system objects during login-time connectivity validation.
The validation SHALL query `INFORMATION_SCHEMA.TABLES` in the target database context.
The validation SHALL treat both `BASE TABLE` and `VIEW` as valid representations of required objects.
The validation SHALL require `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
The validation SHALL fail when one or more required objects are missing and SHALL report the missing object names.

#### Scenario: Required objects present as tables or views pass validation
- **WHEN** the target database contains each required object as either a base table or a view
- **THEN** required-object validation succeeds

#### Scenario: Missing required objects fail validation with clear detail
- **WHEN** one or more required objects are absent from `INFORMATION_SCHEMA.TABLES`
- **THEN** required-object validation fails and reports the missing schema-qualified object names
