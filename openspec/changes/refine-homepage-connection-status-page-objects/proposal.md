## Why

`HomePageConnectionStatusPlaywrightSuccessTest` still performs direct `Page`-level selector and wait calls, which leaks UI structure details into scenario tests and weakens page-object boundaries.
The current `comparisonPage.waitForTwoCheckboxStates(...)` API is also too low-level, making intent hard to read and maintain as UI behaviors evolve.

## What Changes

- Refactor `HomePageConnectionStatusPlaywrightSuccessTest` to remove direct `page` selector and wait choreography from scenario tests.
- Expand page-object APIs so tests use workflow-level and intent-level methods for command selection, comparison setup, and assertion preconditions.
- Replace low-level checkbox-state waiting helpers with semantically named methods that express business intent (for example, “wait until eligible selections are ready” and “wait until comparison controls are actionable”).
- Update or add focused tests that prove the new page-object methods cover existing success-path interactions without reducing assertion clarity.

## Capabilities

### New Capabilities
- _None._

### Modified Capabilities
- `webapp-playwright-connectivity-status`: Tighten the page-object requirement so scenario tests avoid direct `Page` selector usage and rely on semantically meaningful page-object interaction methods.

## Impact

- Affected code is primarily in Playwright test classes and page-object classes under the webapp test suite.
- No production API, runtime behavior, or dependency changes are expected.
- Existing connectivity-status Playwright workflows remain covered, but with clearer ownership boundaries and more maintainable test intent.
