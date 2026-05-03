## Context

The repository currently documents the Maven modules and Testcontainers integration harness, but it does not provide a simple shell-based demo workflow.
The CLI can now read connection settings from `.env` and tables from a flat file, which makes it practical to ship demo inputs instead of requiring a long command line.
The integration-test SQL fixtures already define representative left and right database state, so the demo should reuse those fixtures rather than inventing unrelated data.

## Goals / Non-Goals

**Goals:**

- Provide a wrapper script that builds or locates the CLI jar and invokes it with demo `.env` and table-file inputs.
- Provide a fixture SQL Server lifecycle script that can start, initialize, report status for, and stop a local fixture container.
- Provide demo input files that are safe to commit and clearly marked as non-production examples.
- Update README instructions for build, test, fixture lifecycle, CLI demo usage, and current argument behavior.
- Keep the workflow usable from a clean checkout with Docker and Maven installed.

**Non-Goals:**

- Do not replace the Testcontainers integration-test harness.
- Do not make fixture container state part of normal production runtime.
- Do not add support for databases other than SQL Server.
- Do not require real user credentials in committed demo files.
- Do not change comparison algorithms or report output semantics.

## Decisions

- Put scripts under a repository-level `scripts/` directory.
  This keeps developer automation discoverable without coupling it to one Maven module.
  Alternative considered: put scripts under `cfct-cli/`, but the fixture lifecycle spans the CLI and integration-test fixture resources.

- Put demo input files under a repository-level `demo/` directory.
  This separates committed sample configuration from generated files and avoids confusing the demo `.env` with a user's local `.env`.
  Alternative considered: store demo files under `scripts/`, but inputs are easier to reference and copy from a dedicated demo directory.

- Use names such as `demo/cfct.env` and `demo/tables.txt` rather than committing a root `.env`.
  This avoids accidentally making the repository root look configured for a real environment.
  Alternative considered: commit `.env.example`, but the CLI can consume an explicit `--env-file`, so a named demo env file is more direct.

- Implement the fixture lifecycle script around Docker and the SQL Server 2022 container image used by the test harness.
  The script should use a stable container name, a deterministic host port, the test-harness password, and left/right database names that match the demo env file.
  Alternative considered: invoke Testcontainers from a helper Java class, but that would make a simple start/stop shell script depend on a long-running JVM process.

- Reuse the integration-test fixture SQL resources to initialize the demo databases.
  The script should apply schema and left/right data for the tables listed in the demo table file.
  Alternative considered: duplicate SQL under `demo/`, but duplication increases drift between tests and demo behavior.

- Make the CLI wrapper default to the demo env and tables file while still allowing callers to pass extra CLI options.
  This gives a one-command demo path and preserves escape hatches for custom invocations.
  Alternative considered: hard-code all inputs with no overrides, but that would make the wrapper less useful after the demo.

## Risks / Trade-offs

- SQL Server container startup can be slow → The fixture script should wait for readiness and report clear progress.
- SQL Server images may vary in bundled `sqlcmd` path → The script should detect a supported sqlcmd path inside the container or fail with a clear error.
- Port `14333` could already be in use → The script should allow a port override through an environment variable.
- Demo files contain a known password → The README and file comments should state that values are fixture-only and must not be reused for production.
- README can drift again → Keep commands concise and reference the scripts as the source of truth where possible.
