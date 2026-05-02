## Context

Column-ignore behavior is currently embedded in core table-metadata partitioning logic.
The behavior covers identity columns, guid/uuid names and types, and timestamp/version-like technical columns.
The request requires a public extension point in `cfct-api` and composable advisor implementations in Spring-managed modules.
The request also requires per-advisor enablement flags using typed `@ConfigurationProperties` and default enabled behavior.

## Goals / Non-Goals

**Goals:**
- Introduce a public `IgnoreColumnAdvisor` SPI in `cfct-api`.
- Compose ignore decisions from an injected ordered `List<IgnoreColumnAdvisor>`.
- Split current built-in logic into three independent advisor beans for identity, uuid/guid, and timestamp/version behavior.
- Add typed configuration properties to enable or disable each advisor independently, defaulting to enabled.
- Preserve current default comparison behavior when all advisors are enabled.

**Non-Goals:**
- Change business-key discovery or row matching semantics.
- Add user-interface controls for advisor toggles in this change.
- Introduce runtime plugin loading outside Spring dependency injection.

## Decisions

- Define `IgnoreColumnAdvisor` in `cfct-api` with a column-metadata-based method returning a boolean ignore decision.
An API-level SPI keeps caller and implementation contracts stable and testable.
Alternative considered was keeping the interface in `cfct-impl`.
That was rejected because consumers cannot implement or reuse it without implementation coupling.

- Core comparison services will receive `List<IgnoreColumnAdvisor>` via constructor injection.
The metadata partitioning flow will treat a column as ignored when any enabled advisor returns true.
Alternative considered was a single composite advisor bean with internal branching.
That was rejected because separate advisors are easier to test and toggle.

- Implement three default advisor beans named for responsibility.
`IgnoreColumnAdvisorForIdentityColumns` handles identity metadata.
`IgnoreColumnAdvisorForUuidColumns` handles `uuid` and `guid` name and type rules.
`IgnoreColumnAdvisorForTimestamps` handles timestamp/version technical columns currently excluded by default behavior.

- Add typed properties under a dedicated prefix for default advisor enablement.
Each advisor checks its own enabled flag before returning true.
All flags default to true.
Alternative considered was a single comma-separated disabled-advisor list.
That was rejected for weaker type safety and poorer discoverability.

## Risks / Trade-offs

- [Advisor ordering ambiguity if future advisors overlap] → Use deterministic Spring list ordering and define any-order-safe OR semantics.
- [Behavior drift during refactor] → Add characterization tests asserting ignored columns remain unchanged when defaults are enabled.
- [Configuration sprawl] → Keep properties narrowly scoped and documented with defaults.
