## Context

The connectivity-status Playwright suite already introduced page objects, but `HomePageConnectionStatusPlaywrightSuccessTest` still reaches into raw Playwright `Page` selectors and waits for several flows.
This creates mixed abstraction levels where scenario tests own both business intent and locator choreography.
The current `comparisonPage.waitForTwoCheckboxStates(...)` helper encodes implementation detail rather than observable readiness semantics, which reduces readability and makes future UI changes harder to absorb.

## Goals / Non-Goals

**Goals:**
- Ensure scenario tests interact with the UI only through page-object APIs for setup and interactions.
- Raise page-object method semantics from locator-state detail to workflow-intent language.
- Preserve existing assertion coverage and deterministic test timing for the success workflow.
- Keep migration incremental so existing page-object consumers are not destabilized.

**Non-Goals:**
- Rewriting the full Playwright suite outside the targeted connectivity-status success-path tests.
- Changing production Vaadin UI behavior or backend comparison logic.
- Introducing a new Playwright framework or assertion library.

## Decisions

### Decision 1: Treat raw `Page` calls in scenario tests as a contract violation.
Scenario classes will no longer call `page.waitForSelector`, `page.locator`, or equivalent direct selector APIs for domain interactions.
This enforces a single ownership boundary where selectors, waiting strategy, and control discovery stay inside page objects.
Alternative considered was permitting limited “read-only” direct locators in tests, but this was rejected because it still leaks fragile structure concerns.

### Decision 2: Replace low-level state-wait helpers with intent-driven APIs.
`comparisonPage.waitForTwoCheckboxStates(...)` will be replaced or wrapped by methods named for business readiness outcomes.
Examples include methods such as waiting until selection controls are ready, waiting until eligible-table selections are actionable, and waiting until compare prerequisites are satisfied.
Alternative considered was keeping the old helper and documenting call conventions, but this was rejected because naming would remain opaque.

### Decision 3: Consolidate repeated success-flow choreography into page-object workflow methods.
Where tests repeatedly perform the same sequence (open selection area, filter/select rows, wait for readiness), page objects will expose one higher-level operation with explicit parameters.
Assertions remain in tests so behavior expectations stay visible while setup complexity is encapsulated.
Alternative considered was a test utility helper outside page objects, but this was rejected because it duplicates navigation/locator responsibilities.

### Decision 4: Keep compatibility shims briefly only if needed for phased updates.
If existing tests still call old methods, page objects may keep transitional delegating methods marked for removal.
This allows safe refactoring without wide breakage while preserving forward progress toward semantic APIs.

## Risks / Trade-offs

- [Risk] Semantic wrapper methods can become too broad and hide important behavior edges. → Mitigation: keep methods scoped to explicit readiness or workflow outcomes and keep assertions in scenario tests.
- [Risk] Refactoring waits can introduce timing flakiness. → Mitigation: preserve deterministic wait predicates and run targeted Playwright tests after each refactor slice.
- [Risk] Temporary compatibility methods can linger and dilute the new standard. → Mitigation: track removal in tasks and fail review on new usages of deprecated helper names.
- [Trade-off] More page-object methods increase class surface area. → Mitigation: prefer composable, intention-revealing methods over generic locator exposure.

## Migration Plan

Update page-object classes first by adding semantic readiness and workflow APIs while preserving existing behavior.
Refactor `HomePageConnectionStatusPlaywrightSuccessTest` to consume only page-object methods and remove direct raw `Page` interactions.
Remove or deprecate low-level helper methods once no scenario tests depend on them.
Run focused Playwright tests for connectivity-status success scenarios to confirm behavior and stability.

## Open Questions

Do we want a strict lint or static check to block direct `Page` usage in scenario test classes in a follow-up change.
Should we converge naming conventions for readiness methods across all existing page objects now or in a separate cleanup change.
