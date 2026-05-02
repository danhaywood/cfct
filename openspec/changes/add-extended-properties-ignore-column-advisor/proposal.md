## Why

Some teams need fine-grained per-column ignore behavior that can be managed in database metadata rather than in application code.
SQL Server extended properties provide a natural place to mark columns as comparison-ignored.

## What Changes

- Add a new default advisor implementation named `IgnoreColumnAdvisorUsingExtendedProperties`.
- Make this advisor check SQL Server extended properties for a `cfct.ignored` attribute on each column.
- Treat truthy `cfct.ignored` values as “ignore this column” in comparison.
- Integrate this advisor into the existing advisor-composition flow.
- Add a typed configuration property to enable or disable this advisor, defaulting to enabled.
- Document expected extended-property usage in README or configuration docs.

## Capabilities

### New Capabilities
- `extended-properties-ignore-column-advisor`: Ignore-column behavior based on SQL Server `sp_extendedproperty` metadata.

### Modified Capabilities
- `core-single-table-comparison`: Extend advisor-driven column partitioning so SQL Server extended-property metadata can mark columns ignored.
- `vaadin-webapp-configuration`: Add typed configuration property for enabling/disabling the extended-properties ignore advisor.

## Impact

- Affected modules: `cfct-impl` for metadata lookup/advisor implementation and wiring.
- Affected tests: advisor logic tests and integration/fixture tests for extended-property truthy handling.
- Minor docs/config updates for the new property and metadata convention.
