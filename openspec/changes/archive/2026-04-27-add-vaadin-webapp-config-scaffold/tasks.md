## 1. Reactor and module scaffolding

- [x] 1.1 Add `cfct-webapp` to the root Maven reactor modules list.
- [x] 1.2 Create `cfct-webapp/pom.xml` with Spring Boot web dependencies and Vaadin Flow dependency pinned to stable 25.x.
- [x] 1.3 Add a minimal Spring Boot application entry point class in `cfct-webapp`.
- [x] 1.4 Add a minimal Vaadin route or view so the webapp can start and serve a placeholder page.

## 2. Configuration model and defaults

- [x] 2.1 Add `application.yml` in `cfct-webapp` with baseline keys for server, username, password, left database, right database, table list, tables-file, env-file, output-format, and output-file.
- [x] 2.2 Add typed `@ConfigurationProperties` classes for the webapp configuration namespace.
- [x] 2.3 Add validation rules to reject conflicting table source settings when both inline tables and tables-file are configured.
- [x] 2.4 Add mapping documentation in code or docs showing equivalence between webapp properties and existing CLI options.

## 3. Module boundary and dependency checks

- [x] 3.1 Ensure `cfct-webapp` depends on implementation or API layers without depending on CLI or integration-test modules.
- [x] 3.2 Ensure existing module layering constraints remain valid after adding webapp.

## 4. Documentation and verification

- [x] 4.1 Update README with webapp module purpose, startup command, and configuration file guidance.
- [x] 4.2 Add or update tests that verify configuration property binding and table-source validation behavior.
- [x] 4.3 Run reactor build and relevant module tests to verify the new module scaffolding compiles and starts.
