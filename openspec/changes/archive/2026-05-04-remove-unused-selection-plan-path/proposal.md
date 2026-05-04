## Why

The `cfct.webapp.selection-plan.explicit.tables` configuration path is not used by the production webapp comparison flow.
Keeping unused SelectionPlan wiring and related configuration increases maintenance cost and documentation noise without runtime value.

## What Changes

- Remove the unused SelectionPlan-based preparation path from the webapp runtime wiring.
- Remove `cfct.webapp.selection-plan.explicit.tables` configuration support from active webapp defaults and docs.
- Remove associated tests that only validate the unused SelectionPlan path.
- Update specs and documentation so table selection is described only via the active manual and command-driven UI flow.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `vaadin-webapp-configuration`: remove requirement surface that implies SelectionPlan-based explicit-table configuration is part of active runtime behavior.
- `demo-scripts-and-docs`: remove documentation and config-reference entries for `cfct.webapp.selection-plan.explicit.tables`.

## Impact

This affects webapp selection/configuration classes, tests covering the unused path, and README/application.yml documentation.
No comparison algorithm or result format behavior is expected to change.
