## Context

The repository is at an early stage and does not yet contain the database-comparison engine it is intended to grow into. The immediate need is a dependable integration-test scaffold that proves the project can start a SQL Server 2022 environment under Docker, create two logical databases within that server, connect to each over JDBC, and prepare them independently for future comparison scenarios.

The project already uses Maven and Spring Boot as its application foundation, and the user prefers to continue in that stack. The preferred testing style is AssertJ for fluent assertions, JUnit 5 parameterized tests using enum sources where they clarify repeated scenarios, and Approvals for characterization-style output verification. SQL Server is materially heavier than lightweight databases often used in tests, so startup time, readiness checks, and local Docker compatibility are important constraints. The development machine is Apple Silicon, which increases the risk of slow or emulated SQL Server container startup.

## Goals / Non-Goals

**Goals:**
- Establish a Spring Boot-based project structure that can support future database comparison work.
- Add a repeatable JUnit/Testcontainers harness that starts one SQL Server 2022 container and provisions two separate databases within that instance.
- Make both databases independently addressable, with distinct database names and isolated initialization using the same server-level connection details.
- Add smoke tests that prove the SQL Server instance is live, both databases are reachable, and state does not leak between them.
- Use AssertJ, parameterized tests with enum sources where appropriate, and Approvals where characterization-style assertions add value.
- Keep the scaffold small enough that future comparison logic can be layered on top without undoing early choices.

**Non-Goals:**
- Implement database diffing, schema comparison, or data comparison rules.
- Define the long-term comparison DSL, reporting format, or CLI UX.
- Support multiple database engines in this change.
- Solve all local platform issues beyond documenting and mitigating the most likely container startup risks.

## Decisions

### Use Spring Boot for project structure, but keep the initial harness test-centered
The project will use Spring Boot as the base application framework so later comparison services, configuration, and command-line entry points can live in a familiar structure. However, the first proof point will be integration tests, not application runtime behavior. The harness will therefore be centered around JUnit 5 and Testcontainers rather than requiring a full Spring application context merely to validate container startup.

**Rationale:** This keeps the scaffold aligned with the preferred stack while minimizing moving parts in the first milestone.

**Alternatives considered:**
- Make the first harness entirely plain Java without Spring Boot. Rejected because it would likely be reworked once application structure appears.
- Require every harness test to start a Spring context. Rejected because it adds cost and complexity before it adds value.

### Use one SQL Server container with two databases inside the instance
The scaffold will provision a single SQL Server 2022 instance and create two logical databases within it, conceptually "left" and "right", rather than starting two separate SQL Server containers.

**Rationale:** This better matches the desired initial scaffold, reduces container startup cost, and keeps the first milestone focused on deterministic database-level isolation rather than server-level isolation. For the immediate regression-testing foundation, separate databases within one instance are sufficient and much lighter to operate on a development machine.

**Alternatives considered:**
- Two SQL Server containers. Rejected for the scaffold because the extra isolation is not currently needed and the added startup/runtime cost is high, especially on Apple Silicon.
- One database with schema-level separation. Rejected because the intended mental model is still "left versus right" databases, and separate databases preserve that more clearly.

### Verify database isolation with minimal, explicit smoke tests
The first tests will assert that the SQL Server container starts, both databases can be reached over JDBC, and each can be initialized independently with a small amount of SQL. The smoke tests should verify both liveness and separation, such as by creating or querying different objects in each database.

**Rationale:** The first milestone should prove infrastructure, not comparison semantics.

**Alternatives considered:**
- Build a first-pass comparison assertion immediately. Rejected because it would mix infrastructure proof with comparison design too early.

### Keep initialization lightweight and deterministic
Initialization for the scaffold will rely on simple, repo-controlled test resources such as SQL scripts or equivalent lightweight setup code rather than introducing a full migration strategy unless it becomes necessary for reliability. Setup should include creation of the two logical databases and allow distinct initialization inside each.

**Rationale:** Raw, deterministic setup is the shortest path to a trustworthy smoke harness. Migration tooling can be added later once real schema evolution scenarios exist.

**Alternatives considered:**
- Introduce Flyway or Liquibase immediately. Deferred because the current goal is harness proof, not migration governance.

### Standardize on AssertJ, enum-driven parameterized tests, and Approvals in the test harness
The harness tests should use AssertJ for core assertions, JUnit 5 parameterized tests with enum sources for repeated left/right or mode-driven scenarios, and Approvals where textual or tabular outputs are better verified as approved snapshots than as many small assertions.

**Rationale:** This aligns the scaffold with the preferred testing style and encourages readable tests from the start.

**Alternatives considered:**
- Use plain JUnit assertions everywhere. Rejected because it does not match the preferred fluent style.
- Delay Approvals until comparison output exists. Partially deferred, but the design explicitly allows it so characterization-style tests can be introduced as soon as useful.

### Design the harness so local and CI execution can share the same path
The harness should avoid assumptions that only hold on a developer laptop. Container configuration, startup timeouts, database creation steps, and readiness checks should be explicit so the same tests can run in CI with minimal drift.

**Rationale:** Infrastructure tests lose value if they only pass in one environment.

**Alternatives considered:**
- Optimize exclusively for local development. Rejected because the project is explicitly about regression confidence.

## Risks / Trade-offs

- **SQL Server container startup may be slow or unreliable on Apple Silicon** → Use explicit readiness checks, conservative timeouts, and document expected Docker prerequisites.
- **A single SQL Server instance reduces realism compared with two server-level environments** → Keep database naming and setup clearly separated so the harness can evolve to multiple instances later if needed.
- **The scaffold could accidentally hard-code choices that constrain future comparison features** → Keep responsibilities narrow: container lifecycle, database creation, connectivity, and independent initialization only.
- **Docker availability may become a hidden external dependency for contributors and CI** → Make Docker/Testcontainers expectations explicit in project documentation and test naming.
- **Using raw setup SQL now may require later refactoring if schema evolution becomes central** → Keep setup assets small and modular so migration tooling can replace them incrementally.
- **Approvals can introduce snapshot churn if used too early or too broadly** → Reserve them for stable textual outputs and characterization cases where they improve clarity over many granular assertions.

## Migration Plan

This is an additive change to a young codebase, so there is no production migration plan. Adoption consists of:
1. Update build dependencies and test configuration.
2. Add the Spring Boot scaffold and a single-instance integration-test harness.
3. Add database-creation and per-database initialization support inside that instance.
4. Run the smoke tests in a Docker-enabled environment.
5. Use the harness as the base for subsequent database-comparison features.

Rollback is straightforward: remove the new test harness, test resources, and related dependencies if the approach proves unsuitable.

## Open Questions

- Which exact SQL Server 2022 container image/tag should be standardized for local and CI use?
- Is local Apple Silicon support a hard requirement for the first implementation, or is CI/Linux support sufficient for the first milestone?
- Should the initial scaffold expose reusable helper abstractions immediately, or begin with one well-factored integration test and extract later?
- At what point should migration tooling replace raw SQL initialization assets?
- Where will Approvals provide the most value first: harness diagnostics, future comparison reports, or both?
