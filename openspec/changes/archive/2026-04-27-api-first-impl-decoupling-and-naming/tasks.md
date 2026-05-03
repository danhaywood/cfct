## 1. API service contract extraction

- [x] 1.1 Identify comparison orchestration entry points in `cfct-impl` that are currently called from `cfct-cli` and `cfct-webapp`.
- [x] 1.2 Add API interfaces in `cfct-api` for single-table and multi-table orchestration use cases.
- [x] 1.3 Update implementation classes in `cfct-impl` to implement the new API interfaces without changing comparison behavior.

## 2. Spring wiring and module boundary enforcement

- [x] 2.1 Add or update `cfct-impl` Spring configuration classes that expose API interface beans.
- [x] 2.2 Refactor `cfct-cli` to consume API interfaces and only import implementation wiring configuration.
- [x] 2.3 Refactor `cfct-webapp` to consume API interfaces and only import implementation wiring configuration.
- [x] 2.4 Add architecture tests in CLI and webapp modules that fail on direct non-configuration type references to `cfct-impl`.

## 3. Interface-first implementation naming migration

- [x] 3.1 Rename affected implementation classes from `<Qualifier><Interface>` to `<Interface><Qualifier>`, including the SQL Server CLI executor example.
- [x] 3.2 Update Spring bean declarations, imports, and tests to use renamed implementation class names.
- [x] 3.3 Add or update naming-convention tests or checks to prevent reintroduction of the legacy naming pattern for interface implementations.

## 4. Validation and regression checks

- [x] 4.1 Run CLI module tests to confirm argument parsing, execution, and output behavior remain unchanged after API-boundary refactor.
- [x] 4.2 Run webapp module tests to confirm startup wiring and comparison preparation remain unchanged after API-boundary refactor.
- [x] 4.3 Run reactor build and integration tests to verify no regressions across API, implementation, CLI, and webapp modules.
- [x] 4.4 Update README or developer docs with the new naming convention and module-boundary rules.
