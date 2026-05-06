## Why

The Playwright test suite has grown enough that direct selector usage and static fixture helpers are making test maintenance and reuse harder.
Refactoring to page objects and service-oriented fixture wiring now will reduce duplication, improve readability, and keep test infrastructure aligned with Spring and Maven module boundaries.

## What Changes

- Refactor webapp Playwright tests to use a page object model instead of embedding selectors and UI operations directly in test classes.
- Introduce a dedicated Maven module for Playwright page objects and shared test interaction abstractions.
- Update Maven reactor module declarations and inter-module dependencies so Playwright tests consume the new page-object module.
- Convert `PlaywrightSqlServerFixture` from static utility methods to a regular Spring-managed service/bean used through dependency injection.
- Update Playwright test setup and supporting wiring to use injected fixture services and preserve deterministic SQL Server scenario behavior.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-playwright-connectivity-status`: Playwright coverage remains the same while test structure is reorganized around reusable page objects.
- `maven-multi-module-structure`: The reactor adds a dedicated module for Playwright page objects and updates dependency direction for test support code.
- `sqlserver-two-databases-test-harness`: Playwright fixture access moves from static helpers to Spring bean-based services while preserving deterministic harness behavior.

## Impact

This change affects Playwright integration test sources, test-support classes, and Maven POM files in modules that currently host browser tests.
It also affects Spring test configuration and fixture lifecycle code used by browser-level SQL Server connectivity scenarios.
