## ADDED Requirements

### Requirement: Repository resolves touched tables from command interaction identifiers
The system SHALL provide a repository service that accepts one or more command `interactionId` values.
The repository SHALL traverse command-to-audit linkage and resolve touched tables represented by those interactions.

#### Scenario: Single interaction resolves touched tables
- **WHEN** a caller provides one known command `interactionId` with related audit rows
- **THEN** the repository returns one or more qualified table names touched by that interaction

#### Scenario: Multiple interactions resolve union of touched tables
- **WHEN** a caller provides multiple command `interactionId` values
- **THEN** the repository returns the union of qualified table names touched across all provided interactions

### Requirement: Repository parses logical type names from audit targets
The repository SHALL parse logical type names from `causewayExtAuditTrail.AuditTrailEntry.target` values using `logicalTypeName:id` format.
The repository SHALL use only the logical type prefix before the first `:` when parsing valid targets.

#### Scenario: Valid target yields logical type lookup key
- **WHEN** an audit target is `customer.Customer:123`
- **THEN** the parsed logical type name is `customer.Customer`

#### Scenario: Malformed target is ignored without failing resolution
- **WHEN** an audit target does not contain a valid `logicalTypeName:id` shape
- **THEN** that target contributes no mapping lookup key
- **AND** repository resolution continues for other valid targets

### Requirement: Repository maps logical types to qualified tables via mapping table
The repository SHALL resolve parsed logical type names through `util.LogicalTypeTableMapping`.
The repository SHALL support one logical type mapping to multiple qualified table names.
The repository SHALL return de-duplicated qualified table names in deterministic order.

#### Scenario: One logical type maps to multiple tables
- **WHEN** `util.LogicalTypeTableMapping` contains two rows for the same logical type with different qualified names
- **THEN** both qualified names are included in the repository result

#### Scenario: Missing mappings do not fail valid results
- **WHEN** some parsed logical type names are absent from `util.LogicalTypeTableMapping`
- **THEN** unmapped logical types are ignored
- **AND** mapped logical types still produce qualified table names in the result
