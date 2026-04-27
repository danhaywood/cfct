## 1. Reactor and module scaffolding

- [ ] 1.1 Add `sqlcomparer-webapp` to the root Maven reactor modules list.
- [ ] 1.2 Create `sqlcomparer-webapp/pom.xml` with Spring Boot web dependencies and Vaadin Flow dependency pinned to stable `24.9.2`.
- [ ] 1.3 Add a minimal Spring Boot application entry point class in `sqlcomparer-webapp`.
- [ ] 1.4 Add a minimal Vaadin route or view so the webapp can start and serve a placeholder page.

## 2. Configuration model and defaults

- [ ] 2.1 Add `application.yml` in `sqlcomparer-webapp` with baseline keys for server, username, password, left database, right database, table list, tables-file, env-file, output-format, and output-file.
- [ ] 2.2 Add typed `@ConfigurationProperties` classes for the webapp configuration namespace.
- [ ] 2.3 Add validation rules to reject conflicting table source settings when both inline tables and tables-file are configured.
- [ ] 2.4 Add mapping documentation in code or docs showing equivalence between webapp properties and existing CLI options.

## 3. Module boundary and dependency checks

- [ ] 3.1 Ensure `sqlcomparer-webapp` depends on implementation or API layers without depending on CLI or integration-test modules.
- [ ] 3.2 Ensure existing module layering constraints remain valid after adding webapp.

## 4. Documentation and verification

- [ ] 4.1 Update README with webapp module purpose, startup command, and configuration file guidance.
- [ ] 4.2 Add or update tests that verify configuration property binding and table-source validation behavior.
- [ ] 4.3 Run reactor build and relevant module tests to verify the new module scaffolding compiles and starts.
