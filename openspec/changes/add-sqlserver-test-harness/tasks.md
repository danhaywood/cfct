## 1. Project foundation

- [x] 1.1 Correct the Maven/Spring Boot application scaffold so the project has a valid main application class and standard source layout.
- [x] 1.2 Add the dependencies and plugin configuration needed for Spring Boot testing, JUnit 5, AssertJ, the Microsoft SQL Server JDBC driver, Testcontainers, and Approvals.
- [x] 1.3 Add baseline test configuration so integration tests and parameterized tests can run predictably from Maven in Docker-enabled environments.

## 2. Single-instance SQL Server harness

- [x] 2.1 Create a reusable integration-test harness that provisions one SQL Server 2022 container for the test run.
- [x] 2.2 Add support for creating and addressing separate left and right logical databases within that SQL Server instance.
- [x] 2.3 Configure readiness checks, startup timeouts, and any required container settings so failures surface clearly when SQL Server does not become available.

## 3. Initialization and smoke tests

- [x] 3.1 Add lightweight test setup assets or helper code to create and initialize each logical database independently.
- [x] 3.2 Add a smoke test that verifies the SQL Server instance starts and both logical databases accept JDBC connections.
- [x] 3.3 Add a smoke test that proves database isolation by applying distinct setup to each logical database and confirming the changes do not leak across databases.
- [x] 3.4 Use AssertJ for fluent assertions and use parameterized tests with enum sources where they improve left/right or mode-driven smoke coverage.
- [x] 3.5 Introduce Approvals for any stable textual or tabular harness output that benefits from characterization-style verification.

## 4. Documentation and follow-through

- [x] 4.1 Document local and CI prerequisites for running the SQL Server container harness, including Docker expectations and likely platform caveats.
- [x] 4.2 Document the testing conventions for AssertJ, enum-driven parameterized tests, and Approvals within the harness.
- [x] 4.3 Review the harness structure to ensure it remains a small foundation for later database-comparison work rather than embedding comparison logic early.
