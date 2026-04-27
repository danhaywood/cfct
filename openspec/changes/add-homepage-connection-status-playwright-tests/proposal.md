## Why

The home page currently gives no explicit signal about whether SQL Server connectivity validation succeeded or failed.
This makes local troubleshooting slower and hides useful runtime diagnostics from users and testers.

## What Changes

- Add a home-page connection-status panel that shows SQL connectivity state as either OK or FAILED.
- Display a concise failure summary on the home page when startup connectivity validation fails.
- Keep startup behavior headless-friendly and preserve Testcontainers as the SQL Server source for automated tests.
- Add Playwright-based browser tests that run headlessly and verify both happy-path and failure-path home-page status behavior.

## Capabilities

### New Capabilities
- `webapp-playwright-connectivity-status`: Headless Playwright coverage for home-page SQL connection-status behavior using the existing Testcontainers-backed SQL Server fixture model.

### Modified Capabilities
- `vaadin-webapp-configuration`: Extend requirements so the home page surfaces SQL connectivity outcome and failure detail for users.
- `sqlserver-two-databases-test-harness`: Extend requirements so harness-driven validation supports Playwright browser tests in addition to integration tests.

## Impact

This affects `sqlcomparer-webapp` UI composition, startup validation-to-UI state mapping, and test infrastructure.
It adds Playwright test dependencies, configuration, and CI-compatible headless execution commands.
It keeps Testcontainers-based SQL Server provisioning as the authoritative test backend for connectivity scenarios.
