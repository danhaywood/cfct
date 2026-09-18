## ADDED Requirements

### Requirement: Compare foreground audit structure by semantic key
The system SHALL retrieve audit records associated with the selected foreground command on each database side.
The system SHALL group foreground records by audit target and audited member identifier.
The system SHALL compare the occurrence count for every semantic key present on either side.
The system SHALL NOT use audit row identity, timestamp, transaction sequence, or value fields when determining equality in this comparison mode.

#### Scenario: Foreground audit structure agrees
- **WHEN** app-a and app-b contain the same occurrence count for every target and audited member identifier associated with the selected foreground interaction
- **THEN** the foreground audit comparison reports no differences

#### Scenario: Foreground audit record is missing
- **WHEN** app-a contains an occurrence for a target and audited member identifier that app-b does not contain
- **THEN** the foreground audit comparison reports that semantic key with its app-a and app-b counts

#### Scenario: Foreground duplicate counts differ
- **WHEN** both sides contain the same foreground semantic key but with different occurrence counts
- **THEN** the foreground audit comparison reports the differing counts without discarding duplicate records

### Requirement: Compare completed background audit structure without correlating child identifiers
The system SHALL retrieve audit records using each side's own completed background-descendant interaction identifiers.
The system SHALL aggregate background audit records separately on each side.
The system SHALL compare background records by audit target and audited member identifier without requiring child interaction identifiers to agree across sides.
The system SHALL exclude pending and failed background descendants from the compared audit record set.

#### Scenario: Different child identifiers produce equivalent audit structure
- **WHEN** corresponding completed background work has different interaction identifiers on app-a and app-b
- **AND** both sides contain the same semantic-key occurrence counts
- **THEN** the background audit comparison reports no differences

#### Scenario: Background audit structure differs
- **WHEN** completed background descendants produce different occurrence counts for a target and audited member identifier
- **THEN** the background audit comparison reports that semantic key with its app-a and app-b counts

#### Scenario: No completed background descendants exist
- **WHEN** neither side has completed background descendants for the selected foreground command
- **THEN** the background audit comparison reports zero records and no differences

### Requirement: Report deterministic independent audit results
The system SHALL report foreground and background audit comparison scopes separately.
Each scope SHALL report the total audit-record count for app-a and app-b and a deterministic list of semantic-key count differences.
The combined audit result SHALL report whether either scope has differences.
Audit comparison results SHALL remain independent from business-table comparison results.

#### Scenario: Only audit structure differs
- **WHEN** the business tables compare equal
- **AND** either audit scope has a semantic-key count difference
- **THEN** the audit comparison reports differences
- **AND** the business-table comparison result remains unchanged

#### Scenario: Difference ordering is deterministic
- **WHEN** multiple semantic keys have differing counts
- **THEN** the reported differences are ordered deterministically by target and audited member identifier
