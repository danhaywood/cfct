## 1. Maven Reactor Skeleton

- [x] 1.1 Convert the root `pom.xml` to a parent/aggregator POM with `pom` packaging.
- [x] 1.2 Add module declarations for `cfct-api`, `cfct-impl`, `cfct-cli`, and `cfct-integration-tests` in dependency order.
- [x] 1.3 Move shared properties, dependency management, plugin management, Java release configuration, and encoding configuration to the parent POM.
- [x] 1.4 Create child POMs for all four modules with appropriate parent references and artifact IDs.

## 2. API Module

- [x] 2.1 Create `cfct-api/src/main/java` and move public comparison contract types into it.
- [x] 2.2 Ensure API module contains request, result, table, column, key, option, exception, and service-interface types needed by callers and implementations.
- [x] 2.3 Ensure API module has no dependency on implementation, CLI, Spring Boot application packaging, Testcontainers, ApprovalTests, Jackson, POI, or Picocli.
- [x] 2.4 Compile the API module independently and fix module-boundary issues.
- [x] 2.5 Split API contracts into responsibility-focused packages: `model`, `request`, `spi`, and `exception`.

## 3. Implementation Module

- [x] 3.1 Create `cfct-impl/src/main/java` and move comparison services, SQL Server readers, JSON request loading, configured comparison execution, and report renderers into it.
- [x] 3.2 Configure `cfct-impl` to depend on `cfct-api` and required implementation libraries such as Spring, Jackson, POI, and SQL Server JDBC as appropriate.
- [x] 3.3 Move implementation unit tests into `cfct-impl/src/test/java`.
- [x] 3.4 Configure implementation test dependencies for JUnit, AssertJ, and Spring test support.
- [x] 3.5 Verify `cfct-impl` does not depend on CLI or integration-test modules.
- [x] 3.6 Move implementation comparer services into a responsibility-focused `comparison` package.

## 4. CLI Module

- [x] 4.1 Create `cfct-cli/src/main/java` and move `CfctApplication` into it.
- [x] 4.2 Configure `cfct-cli` to depend on `cfct-impl`.
- [x] 4.3 Move Spring Boot Maven plugin and executable application packaging configuration to the CLI module.
- [x] 4.4 Add Picocli to the CLI module only if still required by the application scaffold.
- [x] 4.5 Verify the CLI module packages with `CfctApplication` as its main class.

## 5. Integration-Test Module

- [x] 5.1 Create `cfct-integration-tests/src/test/java` and move `*IT` classes, `SqlServerTestHarness`, and `DatabaseSide` into it.
- [x] 5.2 Move SQL fixtures, comparison request fixtures, logging configuration, approval files, and generated workbook test resources to `cfct-integration-tests/src/test/resources` as needed.
- [x] 5.3 Configure `cfct-integration-tests` to depend on `cfct-impl` and required test libraries.
- [x] 5.4 Move Maven Failsafe plugin configuration to the integration-test module.
- [x] 5.5 Verify integration tests can still load classpath resources and approval files after the move.

## 6. Build and Dependency Cleanup

- [x] 6.1 Remove old root-level `src/main` and `src/test` trees after all source files and resources have been moved.
- [x] 6.2 Remove production dependencies from modules that no longer need them.
- [x] 6.3 Ensure Testcontainers, ApprovalTests, and harness-only dependencies do not leak into API, implementation, or CLI runtime consumers.
- [x] 6.4 Ensure package names and imports compile after file moves.
- [x] 6.5 Update README build or project-layout notes if they reference the old single-module structure.

## 7. Validation

- [x] 7.1 Run `mvn test` from the repository root and fix unit-test failures.
- [x] 7.2 Run `mvn verify` from the repository root in a Docker-enabled environment and fix integration-test failures.
- [x] 7.3 Validate module boundaries by inspecting module dependency trees or effective dependencies.
- [x] 7.4 Run OpenSpec validation for `refactor-maven-multi-module` and fix proposal, design, spec, or task issues.
