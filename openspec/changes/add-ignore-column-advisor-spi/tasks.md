## 1. API SPI Introduction

- [ ] 1.1 Add `IgnoreColumnAdvisor` contract to `cfct-api` with column-metadata-based ignore decision method.
- [ ] 1.2 Update API and implementation wiring so core comparison services can depend on the new SPI without implementation-module coupling.
- [ ] 1.3 Add or update API-level tests/compilation checks for the new SPI contract.

## 2. Core Advisor Composition Refactor

- [ ] 2.1 Refactor core column-partitioning logic to consume injected `List<IgnoreColumnAdvisor>`.
- [ ] 2.2 Implement OR-style advisor consultation so any advisor can mark a column as ignored.
- [ ] 2.3 Preserve behavior with default advisors enabled and update characterization tests for ignored-column outputs.

## 3. Default Advisor Implementations

- [ ] 3.1 Add `IgnoreColumnAdvisorForIdentityColumns` as a Spring-managed service/bean.
- [ ] 3.2 Add `IgnoreColumnAdvisorForUuidColumns` as a Spring-managed service/bean.
- [ ] 3.3 Add `IgnoreColumnAdvisorForTimestamps` as a Spring-managed service/bean.

## 4. Typed Configuration Properties

- [ ] 4.1 Add `@ConfigurationProperties` for ignore-advisor enablement flags.
- [ ] 4.2 Wire each default advisor to its own enabled flag with default value `true`.
- [ ] 4.3 Add or update configuration-binding tests for defaults and overrides.

## 5. Verification and Documentation

- [ ] 5.1 Run relevant unit and integration tests covering ignored-column partitioning behavior.
- [ ] 5.2 Update README or configuration docs with ignore-advisor property names and defaults.
- [ ] 5.3 Verify disabling one advisor leaves other advisor behaviors active.
