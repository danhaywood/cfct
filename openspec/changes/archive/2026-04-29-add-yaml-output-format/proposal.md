## Why

The library and CLI currently support JSON output but do not provide YAML output for users who prefer human-readable configuration-style formats.
Adding YAML output now improves interoperability with teams and tools that standardize on YAML for reports and automation inputs.

## What Changes

- Extend the library output model to support serialization of comparison results to YAML in addition to JSON.
- Add CLI support to select `yaml` as an output format option.
- Ensure file output naming and writer selection handle YAML consistently with existing output formats.
- Update documentation and usage examples to include YAML output invocations.

## Capabilities

### New Capabilities
- `yaml-comparison-output`: Provide YAML output generation for comparison results from both library and CLI flows.

### Modified Capabilities
- `cli-argument-driven-comparison`: Extend output format argument behavior to accept YAML as a valid format.
- `json-comparison-file`: Generalize output file generation behavior so requirements cover both JSON and YAML structured outputs.

## Impact

This change affects library serialization code, CLI argument parsing and output dispatch, and output-related documentation.
No external API endpoints are changed, and a YAML serialization dependency may be added if no existing dependency is suitable.
