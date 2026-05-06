## Why

The current webapp configuration model mixes `@ConfigurationProperties` with field-level `@Value` injections for datasource defaults.
This weakens type safety, makes metadata generation less reliable, and increases the risk of misconfigured or undocumented runtime properties.

## What Changes

- Replace remaining field-level `@Value` configuration injections in the webapp configuration model with `@ConfigurationProperties`-backed types.
- Model datasource defaults and webapp-specific settings as typed property groups that can be validated and documented consistently.
- Keep existing external property keys and runtime behavior stable while improving internal binding style.
- Update tests and configuration documentation where needed to reflect the fully typed configuration binding approach.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `vaadin-webapp-configuration`: Require all supported webapp configuration keys to bind through `@ConfigurationProperties` types instead of mixed `@Value` field injection.

## Impact

Affected code is primarily in `cfct-webapp` configuration classes and related tests.
The change impacts Spring property binding and configuration metadata generation but does not introduce new runtime APIs.
Documentation updates may be needed for configuration examples and implementation notes.
