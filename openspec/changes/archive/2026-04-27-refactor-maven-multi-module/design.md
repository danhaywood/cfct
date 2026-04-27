## Context

The repository is currently a single Spring Boot Maven module.
That module contains public comparison model types, comparison implementation services, SQL Server-specific readers, report renderers, JSON request loading, the Spring Boot application entry point, unit tests, Testcontainers harness code, and integration tests.

This structure works for early development but makes dependency direction unclear.
It also makes it harder to consume comparison APIs without pulling in CLI/Spring Boot application concerns or Docker-backed integration-test support.

## Goals / Non-Goals

**Goals:**
- Convert the root project to a Maven parent/aggregator build.
- Introduce four modules: `sqlcomparer-api`, `sqlcomparer-impl`, `sqlcomparer-cli`, and `sqlcomparer-integration-tests`.
- Keep package names stable unless a package move is needed to maintain a clean module boundary.
- Move public API/model contracts into the API module.
- Move comparison implementations, SQL Server readers, config request loading, and report renderers into the implementation module.
- Move the Spring Boot application entry point and executable packaging to the CLI module.
- Move Docker/Testcontainers harness code, integration-test fixtures, approval files, and integration tests to the integration-test module.
- Keep root `mvn test` and `mvn verify` behavior working.
- Preserve existing user-visible comparison behavior.

**Non-Goals:**
- Redesign the comparison API semantics.
- Add a new CLI command surface beyond preserving the current Spring Boot application scaffold.
- Add support for databases other than SQL Server.
- Rename Maven group IDs or Java packages as a broad cleanup.
- Change JSON, text, or Excel report formats.

## Decisions

### Use a parent aggregator POM at the repository root

The root `pom.xml` should become a `pom`-packaging parent that lists child modules in dependency order.
It should own shared properties, dependency management, plugin management, and shared build encoding/source settings.
This keeps version declarations centralized and prevents each module from repeating Spring Boot, POI, Testcontainers, ApprovalTests, and SQL Server JDBC versions.

The main alternative was keeping the current single module and using packages as boundaries.
That would not enforce dependency direction or allow integration tests and CLI packaging to evolve independently.

### Create an API module for stable comparison contracts

`sqlcomparer-api` should contain public contract types and interfaces that consumers need in order to request comparisons or consume structured results.
This includes table/column/key/value result records, comparison request records, exceptions that are part of the API surface, and service interfaces such as metadata and row readers where they are part of the comparison abstraction.
The API module should avoid Spring Boot, SQL Server JDBC, POI, Jackson, Picocli, Testcontainers, and ApprovalTests dependencies unless a type explicitly requires a small general-purpose library.

### Create an implementation module for comparison behavior and renderers

`sqlcomparer-impl` should depend on `sqlcomparer-api`.
It should contain the core comparison services, SQL Server-specific readers, JSON request loading, configured comparison service, and report renderers.
Its dependencies should include the implementation libraries needed for those responsibilities, such as Spring context/stereotypes, Jackson, POI, and SQL Server JDBC if compile-time SQL Server support is needed.
Tests for pure implementation behavior should stay in this module.

This module keeps the implementation usable without requiring the Spring Boot executable application or Testcontainers integration harness.

### Create a CLI module for the Spring Boot application

`sqlcomparer-cli` should depend on `sqlcomparer-impl` and contain `SqlComparerApplication`.
The Spring Boot Maven plugin should move to this module because it is the executable application module.
Picocli should also be owned by this module if or when command-line wiring is added.
This keeps executable application concerns out of API and implementation modules.

### Create an integration-test module for Docker-backed verification

`sqlcomparer-integration-tests` should depend on `sqlcomparer-impl` and possibly `sqlcomparer-cli` only if it explicitly tests application startup.
It should contain `SqlServerTestHarness`, `DatabaseSide`, integration tests, SQL fixture resources, approval files, logging properties, and generated workbook test outputs.
Failsafe configuration should live in this module so `mvn verify` from the root runs integration tests there.
Surefire should continue to run unit tests in normal modules.

### Keep module names explicit and stable

Use artifact IDs that include the project prefix: `sqlcomparer-api`, `sqlcomparer-impl`, `sqlcomparer-cli`, and `sqlcomparer-integration-tests`.
This avoids ambiguity in local Maven repositories and IDE module lists.
Directories should match artifact IDs for predictability.

### Move files before changing behavior

The implementation should first create the Maven module skeleton and move files to their target modules while preserving package names and imports where possible.
Only after compilation errors reveal dependency gaps should POM dependencies or visibility be adjusted.
This reduces the risk of mixing behavior changes with structural moves.

### Root build behavior remains the compatibility contract

A clean checkout should still support `mvn test` for unit tests and `mvn verify` for integration tests from the root.
The integration-test module should be skipped or fail clearly only when Docker is unavailable, consistent with the current project behavior.

## Risks / Trade-offs

- [Risk] Module boundaries may reveal hidden dependencies between API and implementation types.
  → Mitigation: Move contracts first, then adjust dependencies or introduce small interfaces rather than adding implementation dependencies to the API module.
- [Risk] Spring Boot plugin behavior can change when moved from root to CLI module.
  → Mitigation: Configure the plugin only in `sqlcomparer-cli` and verify root `mvn package` or `mvn verify` still builds all modules.
- [Risk] Integration tests may lose access to resources after being moved.
  → Mitigation: Move SQL fixtures, approval files, and logging properties with the integration-test source tree and verify classpath resource loading.
- [Risk] IDE run configurations may point at old source paths.
  → Mitigation: Prefer Maven lifecycle validation and update any documented run instructions after the module move.
- [Risk] Dependency scopes can accidentally pull test-only libraries into production modules.
  → Mitigation: Keep Testcontainers, ApprovalTests, JUnit, AssertJ, and SQL Server harness-only dependencies scoped to test or isolated in the integration-test module.
