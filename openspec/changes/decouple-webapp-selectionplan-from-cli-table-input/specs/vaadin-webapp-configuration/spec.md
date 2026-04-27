## MODIFIED Requirements

### Requirement: Webapp configuration models the same logical inputs as CLI
The webapp SHALL provide typed configuration properties that represent server, username, password, left database, right database, env-file path, output format, and output file.
The webapp SHALL load defaults from `application.yml` and allow externalized overrides through Spring configuration sources.
The webapp SHALL document how each shared webapp property maps to the equivalent CLI argument concept.
The webapp SHALL treat table selection as a strategy concern and SHALL NOT require parity with CLI table-input flags.

#### Scenario: Webapp properties bind shared execution settings from application.yml
- **WHEN** the webapp starts with configured values in `application.yml`
- **THEN** typed configuration properties are populated with server, credential, database, env-file, and output settings

#### Scenario: Webapp properties can be overridden externally
- **WHEN** a deploy environment provides overriding Spring configuration values
- **THEN** the webapp resolves those values over defaults from `application.yml`

## REMOVED Requirements

### Requirement: Webapp validates configuration source combinations for table selection
**Reason**: Table-selection behavior is moving behind `SelectionPlan` strategies and no longer depends on CLI-style configuration source combinations.
**Migration**: Replace direct configuration-source conflict checks with `SelectionPlan` implementations that resolve deterministic `List<TableRef>` values.

## ADDED Requirements

### Requirement: Webapp resolves table targets through SelectionPlan strategies
The webapp SHALL define a `SelectionPlan` abstraction that resolves comparison targets as `List<TableRef>`.
The webapp SHALL provide an initial explicit selection-plan implementation that stores concrete `TableRef` values.
The webapp SHALL allow future automated selection-plan implementations without changing the comparison execution contract.
The webapp SHALL consume resolved `List<TableRef>` output from `SelectionPlan` when preparing comparison execution.

#### Scenario: Explicit selection plan resolves concrete tables
- **WHEN** the webapp uses the explicit selection-plan implementation with configured concrete `TableRef` values
- **THEN** the plan resolves those values as the comparison table list

#### Scenario: Selection plan output is used for execution preparation
- **WHEN** the webapp prepares a comparison run
- **THEN** it reads table targets from `SelectionPlan` output instead of CLI table-input structures

#### Scenario: Automated selection plan can be added later
- **WHEN** a new automated selection-plan implementation is introduced
- **THEN** it can plug into the same `SelectionPlan` contract and return `List<TableRef>` without changing core execution interfaces
