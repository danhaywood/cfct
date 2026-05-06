## Context

The current Playwright coverage in the integration-test module is broad and stable, but many tests still inline selectors, wait logic, and interaction sequences.
This makes routine UI changes expensive because a single component change requires updates across multiple test classes.
The current `PlaywrightSqlServerFixture` uses static helper methods, which hides dependencies and makes fixture lifecycle behavior harder to compose with Spring-managed test contexts.
The repository already uses a Maven multi-module structure, so introducing a dedicated page-object module can isolate reusable browser abstractions while keeping test execution in existing integration-test flows.

## Goals / Non-Goals

**Goals:**
- Introduce a dedicated Maven module that owns Playwright page objects and shared UI interaction abstractions.
- Refactor existing Playwright tests to consume page objects rather than direct selector-heavy interaction code.
- Replace static fixture access in `PlaywrightSqlServerFixture` with a Spring-managed service/bean that is injected into tests.
- Preserve existing Playwright scenario coverage and deterministic SQL Server harness behavior.

**Non-Goals:**
- Rewriting or expanding functional coverage beyond what current Playwright tests already assert.
- Changing production webapp behavior, API contracts, or SQL fixture semantics.
- Migrating away from Playwright, JUnit 5, or existing Testcontainers-based harness infrastructure.

## Decisions

### Decision: Add a dedicated test-support module for Playwright page objects
Create a new Maven module in the reactor for Playwright page objects and browser-interaction support classes.
Keep executable browser tests in the integration-test module, which will depend on the new page-object module with test scope as appropriate.
This keeps reusable UI abstractions versioned and discoverable without mixing them with scenario orchestration classes.
Alternative considered was keeping page objects inside the integration-test module.
That alternative was rejected because it does not improve module-level separation and makes reuse by future browser test suites harder.

### Decision: Use constructor-injected Spring bean fixture services instead of static fixture helpers
Refactor `PlaywrightSqlServerFixture` into a regular Spring-managed bean with explicit collaborators and lifecycle hooks.
Inject the fixture bean into Playwright test classes or test support components through Spring test context wiring.
This makes dependencies explicit, improves testability, and allows future fixture variants without static global state assumptions.
Alternative considered was preserving static methods and wrapping them in thin adapters.
That alternative was rejected because it retains hidden dependencies and static coupling.

### Decision: Move selector and interaction logic into page-object classes aligned to UI areas
Define page objects around stable UI regions and workflows, such as app shell, footer status, table selection, and compare-result stage.
Expose intention-revealing methods from page objects and keep assertion semantics in test classes unless a shared assertion helper is clearly reusable.
This balances reuse with test readability while avoiding overly abstract page-object APIs.
Alternative considered was centralizing both interactions and assertions in a monolithic DSL-style helper.
That alternative was rejected because it tends to hide test intent and increases indirection.

## Risks / Trade-offs

- [Page-object abstractions can become too generic or leaky] → Mitigation: Start with workflow-focused objects tied to current scenarios and evolve incrementally.
- [Spring test wiring changes could increase startup complexity] → Mitigation: Keep fixture bean scope narrow and align with existing integration-test configuration patterns.
- [Module split may introduce dependency drift] → Mitigation: Enforce explicit reactor module dependencies and keep page-object module free of unrelated harness concerns.
- [Refactor could accidentally reduce scenario coverage] → Mitigation: Preserve existing test names and assertions where possible and validate with full Playwright suite execution.

## Migration Plan

Add the new page-object Maven module and include it in the root reactor modules list.
Move or create page-object classes in the new module and update package imports in Playwright tests.
Refactor `PlaywrightSqlServerFixture` to bean-based service form and update Spring test configuration to inject it.
Update integration-test module dependencies to consume the page-object module and remove obsolete static helper usage.
Run `mvn test` and `mvn verify` to confirm module wiring and browser test behavior.
If regressions are found, rollback by reverting the module and fixture wiring commits while preserving unaffected test changes.

## Open Questions

Should the page-object module include only page objects, or also shared assertion helpers for repeated status/footer expectations.
Should fixture service lifecycle be singleton-per-context or reset-per-test-class for best balance of determinism and runtime cost.
