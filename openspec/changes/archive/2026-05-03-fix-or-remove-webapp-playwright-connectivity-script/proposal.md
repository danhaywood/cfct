## Why

The README currently documents `./scripts/test-webapp-playwright-connectivity.sh`, but that path appears broken in current project usage.
This creates a false verification path for contributors and CI users who rely on README commands as the contract for supported workflows.

## What Changes

- Audit the current Playwright connectivity test execution path, including script location, assumptions, and command wiring.
- Either repair the script and its invocation contract, or remove/replace it with a reliable documented command if the script no longer provides value.
- Align README webapp testing guidance with the final supported execution path.
- Add or update guardrails so future drift between documented commands and executable test paths is less likely.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-playwright-connectivity-status`: Clarify and enforce the required scriptable headless execution path for connectivity Playwright tests.
- `demo-scripts-and-docs`: Update README expectations so documented webapp Playwright test commands match actual supported behavior.

## Impact

Affected areas include `scripts/test-webapp-playwright-connectivity.sh`, webapp Playwright test wiring, and README webapp testing documentation.
There is no expected runtime production impact, but developer workflow and CI reproducibility may change depending on whether the script is fixed or retired.
