## ADDED Requirements

### Requirement: Home page provides a manual table-selection stage
The webapp home page SHALL provide a manual table-selection panel on the left side of the page before comparison execution.
The left panel SHALL list discovered tables with one checkbox control per table.
The layout SHALL reserve a left selection region and a right comparison region, with the left region sized for selection-focused interaction.
The webapp SHALL provide live feedback showing how many tables are selected.

#### Scenario: User selects and deselects eligible tables
- **WHEN** a user toggles checkboxes for eligible tables in the left panel
- **THEN** the selected-table feedback updates immediately to reflect the current count

#### Scenario: Selection stage is separate from comparison stage
- **WHEN** a user changes table selections in the left panel
- **THEN** comparison execution is not triggered until an explicit run action is invoked

### Requirement: Home page enforces `_BK` eligibility in manual selection
The webapp SHALL evaluate table eligibility for manual selection based on `_BK` requirement rules.
The webapp SHALL render ineligible tables in a visually disabled style.
The webapp SHALL disable checkbox interaction for ineligible tables.

#### Scenario: Ineligible table is visible but disabled
- **WHEN** the table list includes a table that does not satisfy `_BK` requirement rules
- **THEN** the table row is shown in greyed or disabled styling and its checkbox cannot be selected
