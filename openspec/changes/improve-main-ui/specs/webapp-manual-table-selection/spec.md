## MODIFIED Requirements

### Requirement: Webapp exposes a manual table catalog for selection
The webapp SHALL discover and present a manual table catalog for comparison selection.
The webapp SHALL present the catalog in a Vaadin Grid on the left-hand side of the home page.
Each catalog row SHALL include table identity and whether it is currently eligible for selection.
The Grid SHALL support sorting table rows by visible table-identity columns.
The Grid SHALL support filtering table rows by visible table-identity values.
The manual selection state SHALL be available as input to the later comparison-execution stage.

#### Scenario: Catalog lists candidate tables
- **WHEN** the home page initializes table-selection data
- **THEN** users see a Vaadin Grid of candidate tables with one row per table and per-row selection controls

#### Scenario: User sorts candidate tables
- **WHEN** a user sorts the table-selection Grid by a visible table-identity column
- **THEN** the visible candidate table rows are reordered according to the selected sort direction

#### Scenario: User filters candidate tables
- **WHEN** a user enters a table-identity filter in the table-selection Grid
- **THEN** the visible candidate table rows are narrowed to rows matching the filter

#### Scenario: Selected tables become execution input
- **WHEN** a user marks eligible tables as selected
- **THEN** the resulting selected-table set is available as the stage-one output for comparison execution

### Requirement: Manual selection supports future auto-selection overlays
The manual selection model SHALL support future auto-selection defaults with user include and exclude overrides.
The model SHALL preserve deterministic final selected-table output after applying overrides.

#### Scenario: Manual overrides are preserved over defaults
- **WHEN** default selection candidates are provided and a user applies include or exclude overrides
- **THEN** the final selected-table set reflects explicit user overrides deterministically
