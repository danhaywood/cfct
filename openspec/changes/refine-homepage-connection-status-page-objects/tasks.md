## 1. Page-object API uplift

- [x] 1.1 Identify all direct `Page` selector and wait calls currently used by `HomePageConnectionStatusPlaywrightSuccessTest` and map each call to the owning page object.
- [x] 1.2 Add or refine page-object methods to encapsulate those interactions with intent-revealing names for selection readiness and compare readiness outcomes.
- [x] 1.3 Replace or deprecate `comparisonPage.waitForTwoCheckboxStates(...)` with semantic readiness methods and keep a short-lived compatibility delegate only if needed for incremental migration.

## 2. Scenario test refactor

- [x] 2.1 Refactor `HomePageConnectionStatusPlaywrightSuccessTest` so scenario setup and interaction steps use page-object methods only, with no direct raw `Page` locator/wait choreography.
- [x] 2.2 Preserve existing success-path assertions while updating call sites to the new semantic page-object methods.
- [x] 2.3 Remove any now-unused local selector constants or helper logic from the scenario test after page-object adoption.

## 3. Validation and cleanup

- [x] 3.1 Run the connectivity-status Playwright success-focused tests and confirm they remain deterministic and green.
- [x] 3.2 Update adjacent page-object tests or usage sites, if any, that still depend on deprecated low-level wait helpers.
- [x] 3.3 Add a lightweight contributor note in test code comments (or existing test docs) clarifying that scenario tests should avoid direct `Page` selector/wait APIs for domain interactions.
