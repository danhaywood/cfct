## 1. Webapp DataSource migration

- [x] 1.1 Identify webapp services and execution paths that currently depend on direct `Connection` handling.
- [x] 1.2 Refactor webapp comparison execution services to use injected DataSource beans and local try-with-resources connection scope.
- [x] 1.3 Refactor webapp connectivity validation and table-catalog discovery to use DataSource-managed connections consistently.
- [x] 1.4 Update webapp unit tests to verify DataSource acquisition, cleanup behavior, and equivalent comparison outcomes.

## 2. Shared contract and implementation alignment

- [x] 2.1 Evaluate existing API service contracts for Connection-oriented leakage at entry-point boundaries.
- [x] 2.2 Introduce or adjust API and impl service methods where needed to support DataSource-oriented orchestration without behavior drift.
- [x] 2.3 Update Spring wiring so webapp receives DataSource-capable comparison services via existing module-boundary rules.
- [x] 2.4 Update CLI call paths only where contract changes require compatibility adjustments.

## 3. Regression safety and boundary preservation

- [x] 3.1 Extend architecture and naming tests to ensure webapp still avoids non-configuration impl references after DataSource refactor.
- [x] 3.2 Run webapp unit tests and container-backed connectivity tests to verify startup validation remains deterministic.
- [x] 3.3 Run headless Playwright connectivity tests to confirm UI behavior remains stable.
- [x] 3.4 Run full reactor verification (`mvn verify`) and refresh any deterministic approval artifacts if needed.
- [x] 3.5 Update README and developer guidance to document DataSource-based webapp execution behavior.
