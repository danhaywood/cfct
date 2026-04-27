## Context

The webapp already performs startup SQL Server connectivity validation and currently fails startup with diagnostics when validation fails.
The UI does not currently expose a stable connection-status summary that is easy to assert in browser-level tests.
The new goal is to show clear home-page status details while keeping headless execution and Testcontainers-backed SQL Server provisioning.

## Goals / Non-Goals

**Goals:**
- Show a deterministic home-page connection-status indicator with explicit OK and FAILED states.
- Surface concise validation failure details on the home page for operator and developer troubleshooting.
- Add headless Playwright tests that validate home-page status behavior in success and failure scenarios.
- Reuse Testcontainers SQL Server provisioning so browser tests and service-level tests validate the same runtime shape.

**Non-Goals:**
- Replacing startup validation with lazy validation after the UI is loaded.
- Adding non-SQL-Server database support.
- Introducing visual-regression tooling or screenshot baselines.
- Adding non-headless browser test modes as part of this change.

## Decisions

### Decision: Publish validation outcome via a UI-facing connection-status model
The webapp will expose validation outcome as a small application model that represents status, timestamp, and optional failure summary.
The home page will bind to this model and render a deterministic status block suitable for human reading and Playwright selectors.
This keeps status logic centralized and avoids ad-hoc exception parsing in UI components.
Alternative considered was to infer status from logs, which was rejected because it is brittle and not user-visible.

### Decision: Keep startup behavior fail-fast while still presenting status details
The preferred runtime mode remains fail-fast for invalid connectivity to preserve safety and deterministic deployment behavior.
A test-friendly mode will allow app startup with captured validation failure state so the home page can render FAILED details for browser assertions.
This balances production safety with testability.
Alternative considered was disabling fail-fast entirely, which was rejected because it weakens operational guarantees.

### Decision: Use Playwright headless tests integrated with existing Maven test flow
Playwright tests will run in headless mode by default and be callable from Maven or a helper script.
Tests will assert semantic UI content and status markers rather than fragile DOM structure details.
Alternative considered was Vaadin component unit tests only, which was rejected because browser-level rendering and route behavior would remain unverified.

### Decision: Reuse Testcontainers SQL Server as the single backend fixture source
Playwright scenarios will point to SQL Server endpoints provisioned through the existing Testcontainers fixture approach.
This avoids environment drift between integration tests and browser tests.
Alternative considered was static external SQL Server dependencies, which was rejected due to lower reproducibility.

## Risks / Trade-offs

- [Status model can drift from actual validator behavior] → Derive status model directly from validation service outcomes and add focused mapping tests.
- [Headless Playwright timing flakiness on CI] → Use explicit readiness waits and deterministic selectors on stable status elements.
- [Longer test runtime from browser + containers] → Keep scenario count minimal and scope assertions to essential happy-path and failure-path checks.
- [Fail-fast versus UI visibility tension] → Keep fail-fast as default and use an explicit test-mode override only in targeted test runs.

## Migration Plan

Introduce status-model and home-page rendering first, then add Playwright harness and test scenarios.
Run current module tests plus new Playwright scenarios in local and CI headless modes.
Document new commands and expected outcomes in README and test helper scripts.
Rollback is straightforward by removing Playwright wiring and reverting home-page status rendering without data migration.

## Open Questions

Should FAILED status display only a sanitized summary or include a deeper expandable technical detail area.
Should Playwright run in the standard `mvn test` phase or in a dedicated profile to control runtime cost.
Should connection status be refreshed on-demand after startup in a future change.
