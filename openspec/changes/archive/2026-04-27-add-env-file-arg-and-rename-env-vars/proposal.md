## Why

The comparison wrapper should support a clear CLI flag for env file selection so users can override configuration without relying on environment variables.
The current SQLCOMPARE_* environment variable prefix is inconsistent with the `comparedb.sh` script naming and should be standardized.

## What Changes

- Add support in `comparedb.sh` for specifying env file path via `--env-file <path>`.
- Keep backward compatibility by continuing to honor existing env-file behavior while introducing explicit argument-based selection.
- Rename script environment variable names from `SQLCOMPARE_*` to `COMPAREDB_*` throughout scripts and documentation.
- Update help text and README examples to use `COMPAREDB_*` names and the new `--env-file` usage.
- Remove stale references to `SQLCOMPARE_*` in active docs and script usage output.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `demo-scripts-and-docs`: Update wrapper usage requirements and documentation to support `--env-file` argument selection and `COMPAREDB_*` environment variable names.

## Impact

This change affects `comparedb.sh`, wrapper help output, README command examples, and OpenSpec docs related to script usage.
This change does not modify Java CLI argument parsing or comparison engine behavior.
