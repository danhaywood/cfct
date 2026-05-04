## 1. Rename webapp configuration keys

- [ ] 1.1 Update webapp configuration binding classes to consume `cfct.webapp.connection.left-database` and `cfct.webapp.connection.right-database` instead of `cfct.webapp.comparison.connection.*`.
- [ ] 1.2 Update webapp configuration binding classes to consume `cfct.webapp.validation.enabled` and `cfct.webapp.validation.fail-fast` instead of `cfct.webapp.comparison.validation.*`.
- [ ] 1.3 Update default `application.yml` values and runtime property lookups to match renamed keys.

## 2. Remove CLI-oriented keys from webapp config surface

- [ ] 2.1 Remove webapp handling of `cfct.webapp.comparison.env-file`, `cfct.webapp.comparison.output.format`, and `cfct.webapp.comparison.output.file`.
- [ ] 2.2 Remove or update tests that assert removed webapp keys.
- [ ] 2.3 Verify login and validation runtime behavior remains unchanged after removing CLI-oriented keys.

## 3. Update docs and migration guidance

- [ ] 3.1 Update `README.adoc` config reference to the new `cfct.webapp.connection.*` and `cfct.webapp.validation.*` paths.
- [ ] 3.2 Add migration mapping notes for renamed/removed `cfct.webapp.comparison.*` keys.
- [ ] 3.3 Ensure README no longer documents removed webapp runtime keys.

## 4. Validate and align specs

- [ ] 4.1 Add or finalize delta specs for `vaadin-webapp-configuration` and `demo-scripts-and-docs` reflecting renamed/removed keys.
- [ ] 4.2 Run `cfct-webapp` and related module tests and fix regressions.
- [ ] 4.3 Verify examples and startup commands still run with the new property names.
