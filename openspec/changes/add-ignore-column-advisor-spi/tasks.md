## 1. API SPI Introduction

- [x] 1.1 Add `IgnoreColumnAdvisor` contract to `cfct-api` with column-metadata-based ignore decision method.
- [x] 1.2 Update API and implementation wiring so core comparison services can depend on the new SPI without implementation-module coupling.
- [x] 1.3 Add or update API-level tests/compilation checks for the new SPI contract.

## 2. Core Advisor Composition Refactor

- [x] 2.1 Refactor core column-partitioning logic to consume injected `List<IgnoreColumnAdvisor>`.
- [x] 2.2 Implement OR-style advisor consultation so any advisor can mark a column as ignored.
- [x] 2.3 Preserve behavior with default advisors enabled and update characterization tests for ignored-column outputs.

## 3. Default Advisor Implementations

- [x] 3.1 Add `IgnoreColumnAdvisorForIdentityColumns` as a Spring-managed service/bean.
- [x] 3.2 Add `IgnoreColumnAdvisorForUuidColumns` as a Spring-managed service/bean.
- [x] 3.3 Add `IgnoreColumnAdvisorForTimestamps` as a Spring-managed service/bean.

## 4. Typed Configuration Properties

- [x] 4.1 Add `@ConfigurationProperties` for ignore-advisor enablement flags.
- [x] 4.2 Wire each default advisor to its own enabled flag with default value `true`.
- [x] 4.3 Add or update configuration-binding tests for defaults and overrides.

## 5. Verification and Documentation

- [x] 5.1 Run relevant unit and integration tests covering ignored-column partitioning behavior.
- [x] 5.2 Update README or configuration docs with ignore-advisor property names and defaults.
- [x] 5.3 Verify disabling one advisor leaves other advisor behaviors active.
