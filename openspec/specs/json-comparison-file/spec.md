# json-comparison-file Specification

## Purpose
TBD - created by archiving change add-json-comparison-file. Update Purpose after archive.
## Requirements
### Requirement: Comparison request can be loaded from JSON
The system SHALL load a comparison request from a JSON file or stream.
The JSON request SHALL specify an output type and a non-empty list of table references.

#### Scenario: JSON request specifies selected tables
- **WHEN** a JSON comparison file contains table references with schema and table names
- **THEN** the system creates a comparison request for those tables in the same order

#### Scenario: JSON request requires at least one table
- **WHEN** a JSON comparison file contains no table references
- **THEN** the system rejects the file with a clear validation error

#### Scenario: JSON request requires output type
- **WHEN** a JSON comparison file omits the output type
- **THEN** the system rejects the file with a clear validation error

### Requirement: JSON is the only supported output type
The system SHALL accept `json` as the only output type in a comparison JSON file.
The system SHALL reject all other output types.

#### Scenario: JSON output type is accepted
- **WHEN** a JSON comparison file specifies output type `json`
- **THEN** the system accepts the requested output type

#### Scenario: Unsupported output type is rejected
- **WHEN** a JSON comparison file specifies an output type other than `json`
- **THEN** the system rejects the file with a clear validation error identifying the unsupported output type

### Requirement: Configured comparison produces deterministic JSON output
The system SHALL render comparison results as deterministic JSON when the comparison file requests JSON output.
The JSON output SHALL preserve requested table order and stable difference ordering.

#### Scenario: JSON output includes compared tables
- **WHEN** a configured comparison compares multiple selected tables
- **THEN** the JSON output contains one result entry per requested table in request order

#### Scenario: JSON output includes table comparison details
- **WHEN** a table result contains business-key metadata, compared columns, ignored columns, missing rows, or differing rows
- **THEN** the JSON output includes those details using stable field names

#### Scenario: JSON output is approval-tested
- **WHEN** the configured comparison integration test runs
- **THEN** the approved output is a JSON document

