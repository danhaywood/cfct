## Why

The CLI currently always renders comparison results as text, even though the project already supports structured JSON and Excel output in the implementation module.
Users need to choose the CLI output file format so automated scripts can consume JSON or write an Excel workbook without switching to the JSON comparison-file workflow.
Excel is binary output, so it should be written directly to a named file rather than streamed to an interactive stdout by default.

## What Changes

- Add a CLI option for selecting the comparison output format.
- Add an optional `-o` output file option for writing successful output to a file.
- Require `-o` when the selected output format is `excel`.
- Support at least the existing text output and the already implemented JSON and Excel renderers.
- Keep text output as the default for backward-compatible stdout behavior.
- Write binary Excel output safely to the requested output file.
- Validate unsupported output formats and missing Excel output files with clear errors.
- Update CLI tests for parsing, renderer dispatch, file output, and output behavior.

## Capabilities

### New Capabilities

### Modified Capabilities
- `cli-argument-driven-comparison`: Extend CLI execution to accept an output format option, optional output file path, and render the comparison in the selected format.

## Impact

- Affects CLI argument parsing, CLI argument model, execution wiring, and command-runner output handling.
- Reuses existing text, JSON, and Excel rendering services where possible.
- Requires the CLI executor to return bytes or metadata instead of only a string so file and binary output are handled correctly.
- Requires tests for default text behavior, JSON output, Excel output, file output, required Excel output file validation, and unsupported format validation.
