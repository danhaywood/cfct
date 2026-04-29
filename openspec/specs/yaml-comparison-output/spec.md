# yaml-comparison-output Specification

## Purpose
TBD - created by archiving change add-yaml-output-format. Update Purpose after archive.
## Requirements
### Requirement: Library can serialize deterministic comparison results to YAML
The system SHALL serialize comparison results to YAML using the same logical report structure used for JSON output.
The YAML output SHALL preserve requested table order and stable difference ordering.
The YAML output SHALL include report-level difference status and one result entry per requested table.
Each table result SHALL include table identity, business-key metadata, compared columns, ignored columns, summary counts, rows only in left, rows only in right, and differing rows.

#### Scenario: YAML output includes compared tables in request order
- **WHEN** a configured comparison compares multiple selected tables and YAML output is requested
- **THEN** the YAML output contains one result entry per requested table in request order

#### Scenario: YAML output includes row-level detail
- **WHEN** a comparison result includes rows only in one side and differing rows
- **THEN** the YAML output includes row keys, side values, and changed column details for those rows

### Requirement: CLI can emit YAML comparison output
The CLI SHALL accept `yaml` as a valid output format selection.
The CLI SHALL emit deterministic YAML output to stdout when `--output-format yaml` is selected and `-o` is omitted.
The CLI SHALL write deterministic YAML output to a file when `--output-format yaml` is selected with `-o`.

#### Scenario: YAML output format is selected to stdout
- **WHEN** the user runs the CLI with `--output-format yaml` and omits `-o`
- **THEN** the CLI writes deterministic YAML comparison output to stdout

#### Scenario: YAML output format is selected to file
- **WHEN** the user runs the CLI with `--output-format yaml` and `-o comparison.yaml`
- **THEN** the CLI writes deterministic YAML comparison output to `comparison.yaml`

