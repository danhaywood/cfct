## Why

The CLI currently always renders comparison results as text, even though the project already supports structured JSON and Excel output in the implementation module.
Users need to choose the CLI output file format so automated scripts can consume JSON or write an Excel workbook without switching to the JSON comparison-file workflow.

## What Changes

- Add a CLI option for selecting the comparison output format.
- Support at least the existing text output and the already implemented JSON and Excel renderers.
- Keep text output as the default for backward-compatible stdout behavior.
- Write binary Excel output safely when Excel is requested.
- Validate unsupported output formats with a clear error.
- Update CLI tests for parsing, renderer dispatch, and output behavior.

## Capabilities

### New Capabilities

### Modified Capabilities
- `cli-argument-driven-comparison`: Extend CLI execution to accept an output format option and render the comparison in the selected format.

## Impact

- Affects CLI argument parsing, CLI argument model, execution wiring, and command-runner output handling.
- Reuses existing text, JSON, and Excel rendering services where possible.
- May require the CLI executor to return bytes or metadata instead of only a string so binary output is handled correctly.
- Requires tests for default text behavior, JSON output, Excel output, and unsupported format validation.
