## ADDED Requirements

### Requirement: Harness fixture enforces command-to-audit interaction linkage
The harness SHALL enforce referential integrity between Causeway audit and command records.
`causewayExtAuditTrail.AuditTrailEntry.interactionId` SHALL reference `causewayExtCommandLog.CommandLogEntry.interactionId`.

#### Scenario: Audit interaction identifiers must exist in command log
- **WHEN** the harness initializes fixture schema
- **THEN** each inserted audit-trail row must reference an existing command-log interaction identifier

### Requirement: Harness fixture seeds representative command and audit sample rows
The harness SHALL seed deterministic sample rows in command-log and audit-trail tables in both logical databases.
The fixture SHALL include a `registerProduct` command entry whose target is a `Supplier` aggregate.
The fixture SHALL include audit-trail entries tied to that interaction identifier representing creation or mutation footprint on a `Product` aggregate.

#### Scenario: Register-product command footprint is present
- **WHEN** fixture data is initialized for a logical database
- **THEN** command-log data includes a `logicalMemberIdentifier` for `registerProduct` targeting a supplier
- **AND** audit-trail data includes one or more rows for the same `interactionId` targeting a product
