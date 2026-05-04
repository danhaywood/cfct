## MODIFIED Requirements

### Requirement: Core library reports structured row differences
The system SHALL return a structured table comparison result.
The result SHALL distinguish rows only in the left table, rows only in the right table, and matched rows with differing compared column values.
The system SHALL compute row-difference candidates using database-side SQL set operations instead of client-side full-rowset comparison.
The system SHALL fetch only rows classified as left-only, right-only, or value-different by raw SQL value checks.
The system SHALL apply client-side `ColumnValueNormalizer` processing to compared values for matched rows returned as value-different candidates.
The system SHALL suppress a matched-row difference when all compared column values are equal after normalization.
The system SHALL NOT materialize fully matching rows in client memory.

#### Scenario: Row exists only on left
- **WHEN** a business-key value exists in the left table but not the right table
- **THEN** the result records that row as only in left

#### Scenario: Row exists only on right
- **WHEN** a business-key value exists in the right table but not the left table
- **THEN** the result records that row as only in right

#### Scenario: Matched row has differing values
- **WHEN** a business-key value exists on both sides and at least one compared column value differs after normalization
- **THEN** the result records the row key and each differing column with left and right values

#### Scenario: Matched row differs only in ignored values
- **WHEN** a business-key value exists on both sides and differences are limited to ignored columns
- **THEN** the result does not record a row difference for that business-key value

#### Scenario: Matched row differs only in normalizable mask fragments
- **WHEN** a business-key value exists on both sides and raw SQL detects a compared-column difference that becomes equal after mask-based normalization
- **THEN** the result suppresses that matched-row difference

#### Scenario: Matching rows are not returned to the client
- **WHEN** a business-key value exists on both sides and all compared columns are equal after normalization
- **THEN** the comparison result payload does not include that row as a difference
