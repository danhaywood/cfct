## 1. CLI Argument Model

- [ ] 1.1 Add a CLI output format enum or equivalent parser for `text`, `json`, and `excel`.
- [ ] 1.2 Extend `CliArguments` to carry the selected output format.
- [ ] 1.3 Extend `CliArgumentsParser` to accept `--output-format` and default it to `text` when omitted.
- [ ] 1.4 Reject unsupported output format values with a clear validation error.
- [ ] 1.5 Add parser tests for default text output, supported output formats, and unsupported output formats.

## 2. CLI Output Result Handling

- [ ] 2.1 Introduce a CLI execution output value object that can carry bytes and output metadata.
- [ ] 2.2 Update `CliComparisonExecutor` to return the new output value object instead of a string.
- [ ] 2.3 Update `CliCommandRunner` to write successful output bytes to stdout without corrupting binary Excel data.
- [ ] 2.4 Preserve existing stderr and exit-code behavior for validation and execution failures.
- [ ] 2.5 Update command-runner tests for byte output and existing failure behavior.

## 3. Renderer Dispatch

- [ ] 3.1 Inject or otherwise use the existing text, JSON, and Excel multi-table renderers from the CLI execution path.
- [ ] 3.2 Dispatch to the text renderer when the selected format is `text`.
- [ ] 3.3 Dispatch to the JSON renderer when the selected format is `json`.
- [ ] 3.4 Dispatch to the Excel renderer when the selected format is `excel`.
- [ ] 3.5 Add executor tests that verify renderer dispatch and output bytes for supported formats.

## 4. Documentation and Demo Script Updates

- [ ] 4.1 Update README direct CLI usage to document `--output-format`.
- [ ] 4.2 Document that `excel` output should normally be redirected to an `.xlsx` file.
- [ ] 4.3 Update `scripts/run-demo.sh` help text or examples to mention passing `--output-format` through to the CLI.

## 5. Validation

- [ ] 5.1 Run the CLI module test suite with reactor dependencies.
- [ ] 5.2 Run shell syntax checks for changed scripts where applicable.
- [ ] 5.3 Manually review OpenSpec tasks and README command examples for consistency.
