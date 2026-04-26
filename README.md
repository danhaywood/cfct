# sqlcomparer

This project provides the initial Spring Boot scaffold for SQL Server regression-comparison work.
The current focus is a Testcontainers-backed harness that starts one SQL Server 2022 instance and provisions two logical databases inside it.

## Running the build

Run `mvn test` to compile the project and execute non-integration tests.
Run `mvn verify` in a Docker-enabled environment to execute the SQL Server harness integration tests.

## Local and CI prerequisites

Docker must be installed and running before the integration harness can start SQL Server.
The harness uses the `mcr.microsoft.com/mssql/server:2022-latest` image and may start slowly on Apple Silicon because SQL Server container support can rely on emulation.
CI jobs that run `mvn verify` should provide Docker access and enough startup time for SQL Server readiness checks.

## Testing conventions

Use AssertJ for fluent assertions in harness tests.
Use JUnit 5 parameterized tests with `@EnumSource` when the same behavior must be checked across the left and right logical databases or similar modes.
Use Approvals for stable textual or tabular outputs when characterization-style verification is clearer than many small assertions.

## Scope guardrail

The harness is intentionally narrow.
It owns container lifecycle, logical database creation, connectivity, and smoke-test initialization.
It does not yet implement comparison logic, reporting, or a broader database support matrix.
