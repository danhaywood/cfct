## Why

The core library can compare explicitly requested tables, but callers still need a portable way to describe a comparison outside Java code.
A JSON comparison file provides a stable configuration boundary that future CLI and web layers can consume while keeping the core library testable now.

## What Changes

- Add support for loading a comparison request from a JSON file.
- The JSON file will specify the tables to compare and the requested output type.
- The only supported output type for this change is `json`.
- Reject missing, unknown, or unsupported output types with a clear error.
- Produce deterministic JSON output for comparison results.
- Update approval coverage so the approved output becomes JSON rather than the previous text report for the affected comparison scenario.
- Keep this as a library-level capability and do not add a CLI command in this change.

## Capabilities

### New Capabilities

- `json-comparison-file`: Allows a comparison request to be loaded from a JSON file and rendered as deterministic JSON output.

### Modified Capabilities

- None.

## Impact

- Adds JSON request model and parsing support for comparison configuration files.
- Adds JSON rendering support for comparison results.
- Adds test resource JSON files for comparison scenarios.
- Updates Approval tests and approved output to use JSON for the configured comparison scenario.
- May use existing Spring Boot JSON/Jackson dependencies if already available through the project dependency graph.
