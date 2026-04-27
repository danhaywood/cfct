## Why

The current wrapper is named and documented as a demo helper, but it is intended to be a production-facing convenience script for running database comparisons.
The naming, environment variables, file layout, and build behavior should reflect production usage rather than demo-only usage.

## What Changes

- Rename the CLI wrapper from `scripts/run-demo.sh` to a repository-root script named `comparedb.sh`.
- Rename wrapper variables and environment overrides so they no longer use the `DEMO_` prefix.
- Move the production wrapper script to the repository root.
- Rename `demo/sqlcomparer.env` to `demo/.env` for the fixture example.
- Add a root `.env.TEMPLATE` file for users to copy and customize.
- Remove automatic jar rebuild logic from the wrapper and instead fail clearly when required jar files are missing.
- Update README and OpenSpec documentation to reflect the new script name, environment files, variables, and build expectations.

## Capabilities

### New Capabilities

### Modified Capabilities
- `demo-scripts-and-docs`: Rework the wrapper script and documentation from demo-only naming to production-oriented `comparedb.sh` usage and env-file conventions.

## Impact

- Affects repository-level script layout and documentation.
- Renames committed example env file paths used by documentation and examples.
- Removes wrapper-managed Maven build behavior, so users must build jars before running the wrapper.
- Does not change CLI argument parsing, comparison behavior, output formats, fixture SQL data, or the fixture SQL Server lifecycle script.
