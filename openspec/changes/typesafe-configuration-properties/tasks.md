## 1. Refactor configuration bindings

- [ ] 1.1 Replace field-level `@Value` datasource injections in `cfct-webapp` configuration with typed `@ConfigurationProperties` binding.
- [ ] 1.2 Ensure `cfct.webapp.*` runtime settings and datasource defaults are exposed through cohesive typed property models consumed by the webapp.
- [ ] 1.3 Remove obsolete injection paths and keep existing external property keys and defaults behavior-compatible.

## 2. Verify behavior and coverage

- [ ] 2.1 Update or add tests that verify default binding and external override behavior for datasource and webapp runtime properties.
- [ ] 2.2 Add or update an assertion that supported webapp runtime property consumption no longer relies on field-level `@Value` injection.
- [ ] 2.3 Run module tests for `cfct-webapp` and fix regressions introduced by the binding refactor.

## 3. Align documentation

- [ ] 3.1 Update configuration documentation or inline comments impacted by the typed binding refactor.
- [ ] 3.2 Verify examples remain accurate for existing property keys and startup defaults.
