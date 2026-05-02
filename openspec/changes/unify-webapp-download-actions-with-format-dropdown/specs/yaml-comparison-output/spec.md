## MODIFIED Requirements

### Requirement: CLI can emit YAML comparison output
The CLI SHALL accept `yaml` as a valid output format selection.
The CLI SHALL emit deterministic YAML output to stdout when `--output-format yaml` is selected and `-o` is omitted.
The CLI SHALL write deterministic YAML output to a file when `--output-format yaml` is selected with `-o`.
The webapp SHALL make YAML output downloadable from the comparison results stage.

#### Scenario: YAML output format is selected to stdout
- **WHEN** the user runs the CLI with `--output-format yaml` and omits `-o`
- **THEN** the CLI writes deterministic YAML comparison output to stdout

#### Scenario: YAML output format is selected to file
- **WHEN** the user runs the CLI with `--output-format yaml` and `-o comparison.yaml`
- **THEN** the CLI writes deterministic YAML comparison output to `comparison.yaml`

#### Scenario: YAML output is downloadable from webapp results
- **WHEN** a user selects `yaml` in the webapp results download format selector
- **THEN** the webapp downloads deterministic YAML output for the latest comparison run
