## 1. CLI Argument Model

- [x] 1.1 Add a CLI output format enum or equivalent parser for `text`, `json`, and `excel`.
- [x] 1.2 Extend `CliArguments` to carry the selected output format.
- [x] 1.3 Extend `CliArgumentsParser` to accept `--output-format` and default it to `text` when omitted.
- [x] 1.4 Reject unsupported output format values with a clear validation error.
- [x] 1.5 Add parser tests for default text output, supported output formats, and unsupported output formats.
- [x] 1.6 Extend `CliArguments` to carry an optional `-o` output file path.
- [x] 1.7 Extend `CliArgumentsParser` to accept `-o` and require it for `excel` output.
- [x] 1.8 Add parser tests for optional text/JSON output files and required Excel output file validation.

## 2. CLI Output Result Handling

- [x] 2.1 Introduce a CLI execution output value object that can carry bytes and output metadata.
- [x] 2.2 Update `CliComparisonExecutor` to return the new output value object instead of a string.
- [x] 2.3 Update `CliCommandRunner` to write successful output bytes to stdout without corrupting binary Excel data.
- [x] 2.4 Preserve existing stderr and exit-code behavior for validation and execution failures.
- [x] 2.5 Update command-runner tests for byte output and existing failure behavior.
- [x] 2.6 Update `CliCommandRunner` to write successful output bytes to the `-o` file when provided.
- [x] 2.7 Add command-runner tests for file output and Excel-without-file validation behavior.

## 3. Renderer Dispatch

- [x] 3.1 Inject or otherwise use the existing text, JSON, and Excel multi-table renderers from the CLI execution path.
- [x] 3.2 Dispatch to the text renderer when the selected format is `text`.
- [x] 3.3 Dispatch to the JSON renderer when the selected format is `json`.
- [x] 3.4 Dispatch to the Excel renderer when the selected format is `excel`.
- [x] 3.5 Add executor tests that verify renderer dispatch and output bytes for supported formats.

## 4. Documentation and Demo Script Updates

- [x] 4.1 Update README direct CLI usage to document `--output-format`.
- [x] 4.2 Document that `excel` output should normally be redirected to an `.xlsx` file.
- [x] 4.3 Update `scripts/run-demo.sh` help text or examples to mention passing `--output-format` through to the CLI.
- [x] 4.4 Update README to document `-o` and that Excel requires `-o`.
- [x] 4.5 Update `scripts/run-demo.sh` help text and examples to use `-o` for Excel output.

## 5. Validation

- [x] 5.1 Run the CLI module test suite with reactor dependencies.
- [x] 5.2 Run shell syntax checks for changed scripts where applicable.
- [x] 5.3 Manually review OpenSpec tasks and README command examples for consistency.
