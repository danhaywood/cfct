## 1. API service contract extraction

- [ ] 1.1 Identify comparison orchestration entry points in `sqlcomparer-impl` that are currently called from `sqlcomparer-cli` and `sqlcomparer-webapp`.
- [ ] 1.2 Add API interfaces in `sqlcomparer-api` for single-table and multi-table orchestration use cases.
- [ ] 1.3 Update implementation classes in `sqlcomparer-impl` to implement the new API interfaces without changing comparison behavior.

## 2. Spring wiring and module boundary enforcement

- [ ] 2.1 Add or update `sqlcomparer-impl` Spring configuration classes that expose API interface beans.
- [ ] 2.2 Refactor `sqlcomparer-cli` to consume API interfaces and only import implementation wiring configuration.
- [ ] 2.3 Refactor `sqlcomparer-webapp` to consume API interfaces and only import implementation wiring configuration.
- [ ] 2.4 Add architecture tests in CLI and webapp modules that fail on direct non-configuration type references to `sqlcomparer-impl`.

## 3. Interface-first implementation naming migration

- [ ] 3.1 Rename affected implementation classes from `<Qualifier><Interface>` to `<Interface><Qualifier>`, including the SQL Server CLI executor example.
- [ ] 3.2 Update Spring bean declarations, imports, and tests to use renamed implementation class names.
- [ ] 3.3 Add or update naming-convention tests or checks to prevent reintroduction of the legacy naming pattern for interface implementations.

## 4. Validation and regression checks

- [ ] 4.1 Run CLI module tests to confirm argument parsing, execution, and output behavior remain unchanged after API-boundary refactor.
- [ ] 4.2 Run webapp module tests to confirm startup wiring and comparison preparation remain unchanged after API-boundary refactor.
- [ ] 4.3 Run reactor build and integration tests to verify no regressions across API, implementation, CLI, and webapp modules.
- [ ] 4.4 Update README or developer docs with the new naming convention and module-boundary rules.
