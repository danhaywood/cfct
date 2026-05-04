## 1. Remove unused SelectionPlan runtime path

- [ ] 1.1 Remove SelectionPlan classes and configuration (`SelectionPlan`, `ExplicitSelectionPlan`, `ExplicitSelectionPlanProperties`, `SelectionPlanConfiguration`).
- [ ] 1.2 Remove `WebappComparisonPreparationService` and any bean wiring that depends on SelectionPlan.
- [ ] 1.3 Remove tests that only validate the removed SelectionPlan path.

## 2. Update configuration and documentation

- [ ] 2.1 Remove `cfct.webapp.selection-plan.explicit.tables` from `cfct-webapp/src/main/resources/application.yml` defaults.
- [ ] 2.2 Update `README.adoc` and related config references to remove SelectionPlan property documentation.
- [ ] 2.3 Add migration note indicating removal of `cfct.webapp.selection-plan.explicit.tables`.

## 3. Align specs and verify behavior

- [ ] 3.1 Add/update delta specs for `vaadin-webapp-configuration` and `demo-scripts-and-docs` to remove SelectionPlan expectations.
- [ ] 3.2 Run `cfct-webapp` and related module tests and fix regressions.
- [ ] 3.3 Verify manual and command-driven table selection behavior remains unchanged.
