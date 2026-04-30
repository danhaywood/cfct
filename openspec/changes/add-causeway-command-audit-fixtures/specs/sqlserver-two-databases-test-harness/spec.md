## ADDED Requirements

### Requirement: Harness fixture includes Causeway command-log table in both logical databases
The harness SHALL create a `causewayExtCommandLog.CommandLogEntry` table in the left logical database.
The harness SHALL create a `causewayExtCommandLog.CommandLogEntry` table in the right logical database.
Each table SHALL include command-log columns required for footprint-oriented selection setup, including interaction, execution mode, logical member identifier, timestamp, target, and replay-state data.
Each table SHALL define `transactionId` as the primary key.

#### Scenario: Command-log table exists in both logical databases
- **WHEN** the harness initializes fixture schema for a test run
- **THEN** both logical databases contain `causewayExtCommandLog.CommandLogEntry` with primary key `transactionId`

### Requirement: Harness fixture includes Causeway audit-trail table in both logical databases
The harness SHALL create a `causewayExtAuditTrail.AuditTrailEntry` table in the left logical database.
The harness SHALL create a `causewayExtAuditTrail.AuditTrailEntry` table in the right logical database.
Each table SHALL include `interactionId`, `sequence`, `target`, and `propertyId` columns.
Each table SHALL define a composite primary key across `interactionId`, `sequence`, `target`, and `propertyId`.

#### Scenario: Audit-trail table exists in both logical databases with composite key
- **WHEN** the harness initializes fixture schema for a test run
- **THEN** both logical databases contain `causewayExtAuditTrail.AuditTrailEntry` with composite primary key (`interactionId`, `sequence`, `target`, `propertyId`)
