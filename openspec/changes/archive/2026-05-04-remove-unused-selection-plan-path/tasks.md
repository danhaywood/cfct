## 1. Remove unused SelectionPlan runtime path

- [x] 1.1 Remove SelectionPlan classes and configuration (`SelectionPlan`, `ExplicitSelectionPlan`, `ExplicitSelectionPlanProperties`, `SelectionPlanConfiguration`).
- [x] 1.2 Remove `WebappComparisonPreparationService` and any bean wiring that depends on SelectionPlan.
- [x] 1.3 Remove tests that only validate the removed SelectionPlan path.

## 2. Update configuration and documentation

- [x] 2.1 Remove `cfct.webapp.selection-plan.explicit.tables` from `cfct-webapp/src/main/resources/application.yml` defaults.
- [x] 2.2 Update `README.adoc` and related config references to remove SelectionPlan property documentation.
- [x] 2.3 Add migration note indicating removal of `cfct.webapp.selection-plan.explicit.tables`.

## 3. Align specs and verify behavior

- [x] 3.1 Add/update delta specs for `vaadin-webapp-configuration` and `demo-scripts-and-docs` to remove SelectionPlan expectations.
- [x] 3.2 Run `cfct-webapp` and related module tests and fix regressions.
- [x] 3.3 Verify manual and command-driven table selection behavior remains unchanged.
