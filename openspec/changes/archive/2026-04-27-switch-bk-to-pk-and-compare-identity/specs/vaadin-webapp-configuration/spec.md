## MODIFIED Requirements

### Requirement: Home page enforces `_BK` eligibility in manual selection
The webapp SHALL evaluate table eligibility for manual selection based on `_PK` requirement rules.
The webapp SHALL render ineligible tables in a visually disabled style.
The webapp SHALL disable checkbox interaction for ineligible tables.

#### Scenario: Ineligible table is visible but disabled
- **WHEN** the table list includes a table that does not satisfy `_PK` requirement rules
- **THEN** the table row is shown in greyed or disabled styling and its checkbox cannot be selected
