## ADDED Requirements

### Requirement: Harness fixture enforces command-to-audit interaction linkage
The harness SHALL enforce referential integrity between Causeway audit and command records.
`causewayExtAuditTrail.AuditTrailEntry.interactionId` SHALL reference `causewayExtCommandLog.CommandLogEntry.interactionId`.

#### Scenario: Audit interaction identifiers must exist in command log
- **WHEN** the harness initializes fixture schema
- **THEN** each inserted audit-trail row must reference an existing command-log interaction identifier

### Requirement: Harness fixture seeds representative command and audit sample rows
The harness SHALL seed deterministic sample command rows in both logical databases.
The fixture SHALL include a `registerProduct` command entry whose target is a `Supplier` aggregate.
The left logical database SHALL set `replayState` to `EXPORTED` for the seeded command.
The right logical database SHALL set `replayState` to `PENDING` for the seeded command.
The left logical database SHALL include audit-trail entries tied to that interaction identifier representing creation or mutation footprint on a `Product` aggregate.
The right logical database SHALL include no audit-trail entries for that seeded interaction identifier.

#### Scenario: Register-product command footprint is present on left
- **WHEN** fixture data is initialized for the left logical database
- **THEN** command-log data includes a `logicalMemberIdentifier` for `registerProduct` targeting a supplier with replay state `EXPORTED`
- **AND** audit-trail data includes one or more rows for the same `interactionId` targeting a product

#### Scenario: Register-product command is pending on right without audit footprint
- **WHEN** fixture data is initialized for the right logical database
- **THEN** command-log data includes the same `registerProduct` command targeting a supplier with replay state `PENDING`
- **AND** audit-trail data includes no rows for that `interactionId`
