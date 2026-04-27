## ADDED Requirements

### Requirement: Repository provides a Vaadin webapp module scaffold
The repository SHALL provide a Maven module named `sqlcomparer-webapp`.
The webapp module SHALL package a Spring Boot application with Vaadin Flow integration.
The webapp module SHALL start successfully with a minimal bootstrap route suitable for future UI development.
The webapp module SHALL use a stable Vaadin Flow release line, selecting the latest stable line identified during proposal work.

#### Scenario: Webapp module is present in source tree
- **WHEN** the project modules are inspected
- **THEN** a `sqlcomparer-webapp` module exists with its own `pom.xml` and Spring Boot application entry point

#### Scenario: Webapp application starts with minimal route
- **WHEN** the webapp module is started in a local development environment
- **THEN** Spring Boot and Vaadin initialize successfully and render a minimal placeholder view

### Requirement: Webapp configuration models the same logical inputs as CLI
The webapp SHALL provide typed configuration properties that represent server, username, password, left database, right database, table selection, env-file path, output format, and output file.
The webapp SHALL load defaults from `application.yml` and allow externalized overrides through Spring configuration sources.
The webapp SHALL document how each webapp property maps to the equivalent CLI argument concept.

#### Scenario: Webapp properties bind from application.yml
- **WHEN** the webapp starts with configured values in `application.yml`
- **THEN** typed configuration properties are populated with server, credential, database, table, env-file, and output settings

#### Scenario: Webapp properties can be overridden externally
- **WHEN** a deploy environment provides overriding Spring configuration values
- **THEN** the webapp resolves those values over defaults from `application.yml`

### Requirement: Webapp validates configuration source combinations for table selection
The webapp SHALL support table selection by inline list or table-file path configuration.
The webapp SHALL reject invalid configurations that set incompatible table selection sources simultaneously.
The webapp SHALL produce a clear startup validation message when table selection configuration is invalid.

#### Scenario: Valid single table source is accepted
- **WHEN** only inline table list or only table-file path is configured
- **THEN** webapp configuration validation passes for table source selection

#### Scenario: Conflicting table sources are rejected
- **WHEN** both inline table list and table-file path are configured simultaneously
- **THEN** webapp startup fails with a clear validation error identifying the conflict
