## MODIFIED Requirements

### Requirement: CLI supports selectable comparison output formats
The CLI SHALL accept an `--output-format` argument for selecting the comparison output format.
The CLI SHALL support `text`, `json`, `yaml`, and `excel` output format values.
The CLI SHALL use `text` output when `--output-format` is omitted.
The CLI SHALL reject unsupported output format values with a clear validation error.
The CLI SHALL render JSON, YAML, and Excel output using the same report structures as their corresponding renderers.

#### Scenario: Text output is the default
- **WHEN** the user runs the CLI without `--output-format`
- **THEN** the CLI emits the existing deterministic text comparison report to stdout

#### Scenario: JSON output format is selected
- **WHEN** the user runs the CLI with `--output-format json`
- **THEN** the CLI emits deterministic JSON comparison output

#### Scenario: YAML output format is selected
- **WHEN** the user runs the CLI with `--output-format yaml`
- **THEN** the CLI emits deterministic YAML comparison output

#### Scenario: Excel output format is selected
- **WHEN** the user runs the CLI with `--output-format excel` and `-o comparison.xlsx`
- **THEN** the CLI writes a valid `.xlsx` workbook byte stream to `comparison.xlsx`

#### Scenario: Unsupported output format is rejected
- **WHEN** the user runs the CLI with an unsupported `--output-format` value
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying the unsupported output format

### Requirement: CLI writes successful output to an optional file destination
The CLI SHALL accept a `-o` argument for the successful output file path.
The CLI SHALL write successful output to the specified file when `-o` is provided.
The CLI SHALL write text, JSON, and YAML successful output to stdout when `-o` is omitted.
The CLI SHALL require `-o` when the selected output format is `excel`.
The CLI SHALL reject `--output-format excel` without `-o` with a clear validation error.
The CLI SHALL continue to write validation and execution errors to stderr.

#### Scenario: Text output is written to a file
- **WHEN** the user runs the CLI with `--output-format text` and `-o comparison.txt`
- **THEN** the CLI writes the deterministic text comparison report to `comparison.txt`

#### Scenario: JSON output is written to stdout by default
- **WHEN** the user runs the CLI with `--output-format json` and omits `-o`
- **THEN** the CLI writes deterministic JSON comparison output to stdout

#### Scenario: JSON output is written to a file
- **WHEN** the user runs the CLI with `--output-format json` and `-o comparison.json`
- **THEN** the CLI writes deterministic JSON comparison output to `comparison.json`

#### Scenario: YAML output is written to stdout by default
- **WHEN** the user runs the CLI with `--output-format yaml` and omits `-o`
- **THEN** the CLI writes deterministic YAML comparison output to stdout

#### Scenario: YAML output is written to a file
- **WHEN** the user runs the CLI with `--output-format yaml` and `-o comparison.yaml`
- **THEN** the CLI writes deterministic YAML comparison output to `comparison.yaml`

#### Scenario: Excel output requires output file
- **WHEN** the user runs the CLI with `--output-format excel` and omits `-o`
- **THEN** the CLI exits with a non-zero status and reports a clear validation error identifying that `-o` is required for Excel output
