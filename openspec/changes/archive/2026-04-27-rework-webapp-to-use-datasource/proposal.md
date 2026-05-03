## Why

The webapp currently works with raw `Connection` handling patterns where `DataSource`-managed acquisition is the safer and more idiomatic Spring approach.
Moving the webapp to `DataSource` reduces connection-lifecycle risk and creates a cleaner boundary for future pooling, transaction, and observability improvements.

## What Changes

- Rework webapp comparison execution paths to use injected `DataSource` objects rather than direct `Connection` orchestration in web-layer services.
- Update webapp connectivity validation and table-catalog discovery services to acquire and close connections via `DataSource` consistently.
- Refactor shared comparison-service contracts and implementation wiring where necessary so DataSource-driven execution can be used without leaking low-level details into the web layer.
- Update tests to verify DataSource-based behavior in unit, integration, and Playwright-backed flows.
- **BREAKING**: API or implementation service method signatures may change if lower-level refactoring is required to eliminate direct `Connection` usage from application entry points.

## Capabilities

### New Capabilities
- `datasource-based-comparison-execution`: Defines DataSource-driven comparison execution responsibilities for application modules.

### Modified Capabilities
- `vaadin-webapp-configuration`: Webapp comparison preparation and validation requirements change to prefer DataSource-managed connection acquisition.
- `api-service-contracts`: Service contract expectations change where DataSource-based orchestration replaces direct Connection-oriented entry-point usage.

## Impact

Affected modules are expected to include `cfct-webapp`, `cfct-api`, `cfct-impl`, and potentially `cfct-cli` if shared service signatures are adjusted.
Spring bean wiring and tests will be updated to keep module boundaries intact while introducing DataSource-based execution paths.
