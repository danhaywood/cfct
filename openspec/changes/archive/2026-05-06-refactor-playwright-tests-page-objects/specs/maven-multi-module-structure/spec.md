## ADDED Requirements

### Requirement: Reactor includes dedicated Playwright page-object module
The root Maven reactor SHALL include a dedicated module that owns Playwright page objects and shared browser-interaction abstractions.
The integration-test module SHALL depend on this page-object module for browser test support.
The page-object module SHALL not declare production runtime responsibilities outside browser-test support.

#### Scenario: Reactor declares Playwright page-object module
- **WHEN** the root Maven project is inspected
- **THEN** the modules list includes a dedicated Playwright page-object module used by integration tests

#### Scenario: Integration tests resolve page-object classes via module dependency
- **WHEN** Playwright integration tests are compiled and executed
- **THEN** page-object classes are resolved from the dedicated module through declared Maven dependencies
