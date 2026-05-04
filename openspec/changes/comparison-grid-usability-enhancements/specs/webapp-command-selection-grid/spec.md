## ADDED Requirements

### Requirement: Compare action remains unobstructed by business table selection content
The command and table selection layout SHALL reserve dedicated space for the `Compare` primary action.
The business table selection grid SHALL NOT overlap or visually obscure the `Compare` action row at any supported viewport size.
The `Compare` action SHALL remain fully visible and clickable while users interact with command and table selections.

#### Scenario: Business table grid does not overlap compare action
- **WHEN** the left navigation drawer renders command, business table, and compare controls
- **THEN** the business table selection grid ends before the compare action row
- **AND** no overlap exists between grid content and compare action controls

#### Scenario: Compare remains accessible during selection changes
- **WHEN** users scroll or resize within the selection area while table data is present
- **THEN** the compare action remains visible and actionable without being covered by selection-grid content