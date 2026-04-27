## Context

The webapp scaffold currently starts and binds configuration, but it does not yet prove that configured SQL Server endpoints and logical databases are valid for comparison use.
The project already has SQL Server Testcontainers infrastructure in the integration-test module, but webapp-specific connectivity validation behavior is not exercised.
Before UI execution flows are built, startup/application-level validation should catch connectivity and database-presence issues with clear diagnostics.

## Goals / Non-Goals

**Goals:**
- Add webapp infrastructure services that validate SQL Server connectivity using configured server, credentials, and logical database names.
- Validate that configured left and right logical databases exist and are accessible.
- Provide clear failure messages for unreachable servers, authentication failures, and missing databases.
- Add deterministic Testcontainers-backed coverage for these validation paths in automation.

**Non-Goals:**
- Do not add browser E2E tooling such as Playwright in this change.
- Do not implement full UI comparison workflows.
- Do not alter CLI argument parsing or CLI execution behavior.

## Decisions

Add a dedicated webapp connectivity validation service rather than placing validation logic inside Vaadin views.
This keeps validation testable, reusable, and independent of UI concerns.
Alternative considered was validating from UI layer directly, but that would entangle infrastructure checks with presentation flow.

Validate both connectivity and configured database existence at application startup for fail-fast behavior.
This ensures misconfiguration is surfaced immediately before users interact with the app.
Alternative considered was lazy validation at first comparison request, but that delays feedback and complicates operational troubleshooting.

Reuse Testcontainers SQL Server setup patterns from existing harness capabilities while adding webapp-focused test cases.
This avoids introducing a second independent container orchestration approach.
Alternative considered was only mock-based tests, but mocks would not prove real JDBC connectivity and catalog checks.

Provide optional helper script(s) for consistent local verification flow around the Testcontainers-backed tests.
This gives contributors a repeatable path without introducing Playwright complexity.
Alternative considered was no scripts, relying purely on Maven commands, but scripted helpers improve discoverability and repeatability.

## Risks / Trade-offs

[Risk] Startup fail-fast validation can block app boot in partially configured development environments.
→ Mitigation is to document required configuration and support profile-based overrides for local scenarios.

[Risk] Containerized tests may be slower and more environment-sensitive than unit tests.
→ Mitigation is to keep unit tests for non-IO logic and scope container tests to representative connectivity scenarios.

[Risk] Database existence checks may become vendor-specific over time.
→ Mitigation is to isolate SQL Server-specific checks behind service boundaries for future adaptation.

## Migration Plan

Add connectivity validation service(s) in `sqlcomparer-webapp` and wire startup checks.
Implement clear exception/error mapping for common connectivity failures.
Add Testcontainers-backed validation tests in the appropriate module scope.
Add/update helper scripts and README notes for running validation checks locally.

## Open Questions

Should startup validation be always-on or guarded by a profile/property toggle for developer convenience.
Should database-existence checks verify only catalog presence or also minimum table-level prerequisites for future workflows.
