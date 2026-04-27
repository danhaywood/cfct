## MODIFIED Requirements

### Requirement: Core library compares one named table
The system SHALL provide a core library API module containing the public contracts needed to compare one caller-specified SQL Server table between a left JDBC connection and a right JDBC connection.
The system SHALL provide implementation services outside the API module that implement those public contracts.
The core library MAY use Spring wiring patterns such as components, services, and configuration in implementation modules.
The API SHALL be independent of CLI and web-specific request concepts.

#### Scenario: Caller compares one table
- **WHEN** a caller provides left and right JDBC connections and a table reference
- **THEN** the core library compares only that referenced table

#### Scenario: Core API does not require CLI or web inputs
- **WHEN** a caller invokes the core comparison API
- **THEN** the caller is not required to provide command-line arguments, web request objects, or CLI-specific wrapper objects

#### Scenario: Core API contracts are available without CLI module
- **WHEN** a caller depends on the API module
- **THEN** the caller can use public comparison request and result contracts without depending on the CLI module

#### Scenario: Core services can be Spring-managed
- **WHEN** the core comparison implementation is consumed from a Spring application context
- **THEN** its comparison services can be wired as Spring-managed beans
