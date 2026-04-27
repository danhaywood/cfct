## ADDED Requirements

### Requirement: Comparison output approval artifacts can be generated for inspection
The system SHALL provide a test that generates inspectable approval-style artifacts for configured comparison outputs.
The generated artifacts SHALL include the detailed JSON output for a representative configured comparison.
The generated artifacts SHALL include the Excel workbook output for the same representative configured comparison.
The generated artifacts SHALL be written to deterministic paths under the build output directory so they can be opened or reviewed after the test run.

#### Scenario: JSON approval artifact is written
- **WHEN** the approval artifact test runs for a configured JSON comparison
- **THEN** it writes a formatted JSON artifact to a deterministic build output path

#### Scenario: Excel approval artifact is written
- **WHEN** the approval artifact test runs for a configured Excel comparison
- **THEN** it writes an `.xlsx` workbook artifact to a deterministic build output path

#### Scenario: Approval artifacts use the same fixture
- **WHEN** the approval artifact test generates JSON and Excel output
- **THEN** both outputs are produced from the same representative comparison fixture

#### Scenario: Approval artifact paths are easy to inspect
- **WHEN** the approval artifact test completes successfully
- **THEN** the generated artifact paths are stable and located under `target` for manual inspection
