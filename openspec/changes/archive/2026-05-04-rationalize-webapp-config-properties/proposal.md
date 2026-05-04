## Why

Current webapp configuration mixes webapp-only and CLI-oriented properties under `cfct.webapp.comparison.*`, which makes ownership and intent unclear.
A clearer property taxonomy will reduce deployer confusion and simplify long-term configuration maintenance.

## What Changes

- Move webapp database selection keys from `cfct.webapp.comparison.connection.*` to `cfct.webapp.connection.*`.
- Move webapp validation keys from `cfct.webapp.comparison.validation.*` to `cfct.webapp.validation.*`.
- Remove `cfct.webapp.comparison.env-file`, `cfct.webapp.comparison.output.format`, and `cfct.webapp.comparison.output.file` from webapp runtime configuration because they are CLI-oriented.
- Update defaults, docs, and tests to reflect the new property names and removed keys.
- **BREAKING**: old `cfct.webapp.comparison.*` keys are removed or renamed.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `vaadin-webapp-configuration`: update required webapp property names and remove CLI-oriented webapp config requirements.
- `demo-scripts-and-docs`: update README and configuration reference to document renamed keys and removed keys with migration guidance.

## Impact

Webapp configuration binding classes, application defaults, runtime property lookup, tests, and documentation are affected.
Deployers must migrate renamed keys and remove deprecated webapp keys from configuration files and environment overrides.
