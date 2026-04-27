## 1. Maven Reactor Skeleton

- [ ] 1.1 Convert the root `pom.xml` to a parent/aggregator POM with `pom` packaging.
- [ ] 1.2 Add module declarations for `sqlcomparer-api`, `sqlcomparer-impl`, `sqlcomparer-cli`, and `sqlcomparer-integration-tests` in dependency order.
- [ ] 1.3 Move shared properties, dependency management, plugin management, Java release configuration, and encoding configuration to the parent POM.
- [ ] 1.4 Create child POMs for all four modules with appropriate parent references and artifact IDs.

## 2. API Module

- [ ] 2.1 Create `sqlcomparer-api/src/main/java` and move public comparison contract types into it.
- [ ] 2.2 Ensure API module contains request, result, table, column, key, option, exception, and service-interface types needed by callers and implementations.
- [ ] 2.3 Ensure API module has no dependency on implementation, CLI, Spring Boot application packaging, Testcontainers, ApprovalTests, Jackson, POI, or Picocli.
- [ ] 2.4 Compile the API module independently and fix module-boundary issues.

## 3. Implementation Module

- [ ] 3.1 Create `sqlcomparer-impl/src/main/java` and move comparison services, SQL Server readers, JSON request loading, configured comparison execution, and report renderers into it.
- [ ] 3.2 Configure `sqlcomparer-impl` to depend on `sqlcomparer-api` and required implementation libraries such as Spring, Jackson, POI, and SQL Server JDBC as appropriate.
- [ ] 3.3 Move implementation unit tests into `sqlcomparer-impl/src/test/java`.
- [ ] 3.4 Configure implementation test dependencies for JUnit, AssertJ, and Spring test support.
- [ ] 3.5 Verify `sqlcomparer-impl` does not depend on CLI or integration-test modules.

## 4. CLI Module

- [ ] 4.1 Create `sqlcomparer-cli/src/main/java` and move `SqlComparerApplication` into it.
- [ ] 4.2 Configure `sqlcomparer-cli` to depend on `sqlcomparer-impl`.
- [ ] 4.3 Move Spring Boot Maven plugin and executable application packaging configuration to the CLI module.
- [ ] 4.4 Add Picocli to the CLI module only if still required by the application scaffold.
- [ ] 4.5 Verify the CLI module packages with `SqlComparerApplication` as its main class.

## 5. Integration-Test Module

- [ ] 5.1 Create `sqlcomparer-integration-tests/src/test/java` and move `*IT` classes, `SqlServerTestHarness`, and `DatabaseSide` into it.
- [ ] 5.2 Move SQL fixtures, comparison request fixtures, logging configuration, approval files, and generated workbook test resources to `sqlcomparer-integration-tests/src/test/resources` as needed.
- [ ] 5.3 Configure `sqlcomparer-integration-tests` to depend on `sqlcomparer-impl` and required test libraries.
- [ ] 5.4 Move Maven Failsafe plugin configuration to the integration-test module.
- [ ] 5.5 Verify integration tests can still load classpath resources and approval files after the move.

## 6. Build and Dependency Cleanup

- [ ] 6.1 Remove old root-level `src/main` and `src/test` trees after all source files and resources have been moved.
- [ ] 6.2 Remove production dependencies from modules that no longer need them.
- [ ] 6.3 Ensure Testcontainers, ApprovalTests, and harness-only dependencies do not leak into API, implementation, or CLI runtime consumers.
- [ ] 6.4 Ensure package names and imports compile after file moves.
- [ ] 6.5 Update README build or project-layout notes if they reference the old single-module structure.

## 7. Validation

- [ ] 7.1 Run `mvn test` from the repository root and fix unit-test failures.
- [ ] 7.2 Run `mvn verify` from the repository root in a Docker-enabled environment and fix integration-test failures.
- [ ] 7.3 Validate module boundaries by inspecting module dependency trees or effective dependencies.
- [ ] 7.4 Run OpenSpec validation for `refactor-maven-multi-module` and fix proposal, design, spec, or task issues.
