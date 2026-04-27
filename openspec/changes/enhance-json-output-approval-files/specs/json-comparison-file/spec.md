## MODIFIED Requirements

### Requirement: Configured comparison produces deterministic detailed JSON output
The system SHALL render comparison results as deterministic JSON when the comparison file requests JSON output.
The JSON output SHALL preserve requested table order and stable difference ordering.
The JSON output SHALL include report-level difference status and one result entry per requested table.
Each table result SHALL include table identity, business-key metadata, compared columns, ignored columns, summary counts, rows only in left, rows only in right, and differing rows.
Rows only in left and rows only in right SHALL include the row key and available side values.
Differing rows SHALL include the row key, left values, right values, and changed column details.

#### Scenario: JSON output includes compared tables
- **WHEN** a configured comparison compares multiple selected tables
- **THEN** the JSON output contains one result entry per requested table in request order

#### Scenario: JSON output includes table comparison metadata
- **WHEN** a table result contains business-key metadata, compared columns, or ignored columns
- **THEN** the JSON output includes those values using stable field names

#### Scenario: JSON output includes table summary counts
- **WHEN** a table result contains compared columns, ignored columns, missing rows, or differing rows
- **THEN** the JSON output includes summary counts for those items using stable field names

#### Scenario: JSON output includes rows only in left with values
- **WHEN** a table result contains a row only in the left database
- **THEN** the JSON output includes the row key and left-side values for that row

#### Scenario: JSON output includes rows only in right with values
- **WHEN** a table result contains a row only in the right database
- **THEN** the JSON output includes the row key and right-side values for that row

#### Scenario: JSON output includes differing row side-by-side values
- **WHEN** a matched row has differing compared values
- **THEN** the JSON output includes the row key, left-side values, right-side values, and changed column details for that row

#### Scenario: JSON output is approval-tested
- **WHEN** the configured comparison integration test runs
- **THEN** the approved output is a deterministic detailed JSON document
