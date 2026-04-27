## ADDED Requirements

### Requirement: Comparison JSON file supports configured output formats
The system SHALL accept `json` and `excel` as supported output types in a comparison JSON file.
The system SHALL reject all other output types.

#### Scenario: JSON output type is accepted
- **WHEN** a JSON comparison file specifies output type `json`
- **THEN** the system accepts the requested output type

#### Scenario: Excel output type is accepted
- **WHEN** a JSON comparison file specifies output type `excel`
- **THEN** the system accepts the requested output type

#### Scenario: Unsupported output type is rejected
- **WHEN** a JSON comparison file specifies an output type other than `json` or `excel`
- **THEN** the system rejects the file with a clear validation error identifying the unsupported output type

## REMOVED Requirements

### Requirement: JSON is the only supported output type
**Reason**: Excel is now a supported output format in addition to JSON.
**Migration**: Use `json` when deterministic JSON output is required, or use `excel` when workbook output is required.
