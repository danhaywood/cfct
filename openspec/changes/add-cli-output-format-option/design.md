## Context

The CLI currently returns text comparison output through `CliComparisonExecutor.execute`, and `CliCommandRunner` writes that string to stdout.
The implementation module already contains JSON and Excel renderers used by configured comparison execution.
Excel output is binary, so the CLI path must stop assuming every successful result is a `String` if Excel is made available from command-line table selection.

## Goals / Non-Goals

**Goals:**

- Add a CLI output format option, named `--output-format`, with supported values `text`, `json`, and `excel`.
- Keep `text` as the default when no output format is specified.
- Reuse the existing text, JSON, and Excel renderers rather than duplicating rendering logic.
- Write JSON and text as UTF-8 bytes and write Excel as raw workbook bytes.
- Preserve current validation and error behavior for connection and table arguments.

**Non-Goals:**

- Do not add an output file path option in this change.
- Do not change the JSON comparison-file workflow.
- Do not change the structure of text, JSON, or Excel reports.
- Do not introduce new output formats beyond `text`, `json`, and `excel`.

## Decisions

- Represent CLI output format with a small CLI-specific enum rather than reusing `ComparisonOutputType` directly.
  The existing enum covers configured request formats `json` and `excel`, while the CLI also needs `text` as its backward-compatible default.
  Alternative considered: extend `ComparisonOutputType` with `TEXT`, but that could imply text is valid in JSON comparison files where the current contract only supports JSON and Excel.

- Add `--output-format` as the primary option name.
  A long option is explicit and avoids consuming short flags that may be useful for future output file behavior.
  Alternative considered: use `-f`, but short flags are less self-documenting and the existing CLI already uses several single-letter connection flags.

- Change the successful CLI execution result from `String` to a small value object that carries bytes and metadata such as media type and file extension.
  This allows the command runner to write binary Excel output safely through an `OutputStream`.
  Alternative considered: keep returning `String`, but that would corrupt Excel workbook bytes and force unsafe encoding assumptions.

- Keep stdout as the output destination for all formats.
  This preserves the existing CLI shape and lets users redirect JSON or Excel to a file using shell redirection.
  Alternative considered: require a file path for Excel, but that would add a separate output-destination feature that is not required for format selection.

- Extend command-runner tests to use bytes for successful output assertions.
  Validation errors should continue to go to stderr with exit code `2`, and execution failures should continue to use exit code `1`.

## Risks / Trade-offs

- Binary Excel data written to an interactive terminal can appear garbled → README and/or usage text should recommend redirecting Excel output to an `.xlsx` file.
- Introducing a CLI output value object touches executor and runner interfaces → Keep the value object small and update tests in one pass.
- Users may expect JSON and Excel output to match configured comparison output exactly → Reuse the existing renderers so behavior remains consistent.
- The term “format” can be confused with file destination → Name the option `--output-format` and avoid adding implicit file writing behavior.
