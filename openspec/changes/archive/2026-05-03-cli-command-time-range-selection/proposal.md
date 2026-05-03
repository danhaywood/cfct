## Why

Selecting command IDs manually for CLI comparison is slow and error-prone when users already know the command time window they want to analyze.
Allowing time-range selection makes command-driven table inference faster and more repeatable for operational troubleshooting.

## What Changes

- Extend the CLI with time-range arguments for command selection.
- Treat commands within the provided time range as the selected command set, with both range boundaries inclusive.
- Infer business tables from those selected commands using existing command-audit-footprint resolution, then run comparison against the inferred table set.
- Keep existing explicit table selection behavior available and validate mutually-exclusive input modes clearly.
- Update automated tests and README usage examples/documentation for the new CLI flow.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `cli-argument-driven-comparison`: Add CLI argument mode for inclusive command-time-range selection and table inference from selected commands.

## Impact

This affects CLI argument parsing, validation rules, and execution request construction.
This affects integration between CLI and command-footprint table resolution for command-driven selection.
This affects CLI unit/integration tests and README command examples.
No comparison-engine semantics or output-format behavior are changed.
