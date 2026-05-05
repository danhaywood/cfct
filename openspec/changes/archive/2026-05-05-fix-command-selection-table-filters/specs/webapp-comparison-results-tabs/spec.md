## MODIFIED Requirements

### Requirement: Result grid hides MATCH rows by default with explicit opt-in
The comparison result grid SHALL exclude rows classified as `MATCH` by default when a comparison result tab is first shown.
The comparison stage SHALL NOT render a `Show MATCH rows` checkbox in the results controls.
The comparison stage SHALL keep MATCH-row visibility fixed to excluded in standard result exploration.

#### Scenario: MATCH rows are hidden by default
- **WHEN** a comparison run completes and a table result tab is opened
- **THEN** rows classified as `MATCH` are not shown in the initial grid view

#### Scenario: Results controls omit MATCH visibility toggle
- **WHEN** comparison succeeds and result controls are rendered
- **THEN** no `Show MATCH rows` checkbox is visible in the results controls
