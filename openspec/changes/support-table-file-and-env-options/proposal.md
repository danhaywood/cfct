## Why

The current CLI requires all connection options and the table list to be supplied directly on the command line, which makes repeatable comparisons verbose and can expose credentials in shell history.
Users need a lightweight way to keep table selections and common connection settings in files while still preserving simple argument-driven execution.

## What Changes

- Add a CLI option for supplying the table selection from a flat text file with one table reference per line.
- Continue supporting the existing comma-separated `-t` table list for direct command-line usage.
- Allow `-S`, `-U`, `-P`, `-l`, and `-r` to be omitted from the command line when corresponding values are present in a `.env` file.
- Define precedence so explicit CLI arguments override `.env` values.
- Keep validation errors clear when required connection values or table selections cannot be resolved.

## Capabilities

### New Capabilities

### Modified Capabilities
- `cli-argument-driven-comparison`: Extend CLI input resolution to support table files and optional `.env` defaults for connection arguments.

## Impact

- Affects CLI argument parsing, validation, and request construction.
- Adds file-based input handling for table references.
- Adds `.env` loading for selected connection settings without changing the comparison library API.
- Requires automated tests for table-file parsing, `.env` fallback, precedence, and missing-value errors.
