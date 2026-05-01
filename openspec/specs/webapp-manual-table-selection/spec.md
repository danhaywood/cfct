# webapp-manual-table-selection Specification

## Purpose
TBD - created by archiving change add-webapp-manual-table-selection-panel. Update Purpose after archive.
## Requirements
### Requirement: Webapp exposes a manual table catalog for selection
The webapp SHALL discover and present a manual table catalog for comparison selection.
The webapp SHALL present the catalog in a Vaadin Grid in the AppLayout navigation area.
The manual table-selection grid SHALL be positioned below the command-selection grid when both sections are present.
Each catalog row SHALL include table identity.
The Grid SHALL NOT include a dedicated eligibility column.
The Grid SHALL make ineligible rows non-selectable and expose the eligibility reason as a tooltip.
The Grid SHALL support sorting table rows by visible table-identity columns.
The Grid SHALL support filtering table rows by visible table-identity values without requiring a separate apply-filter action.
The selection drawer SHALL NOT display a `Select tables` heading or selected-table count.
The schema column SHALL auto-size to fit visible schema values.
The selection control column SHALL be center-aligned.
The selection control column header SHALL be blank and SHALL NOT render `Select` text.
The manual selection state SHALL be available as input to the later comparison-execution stage.

#### Scenario: Catalog lists candidate tables
- **WHEN** the home page initializes table-selection data
- **THEN** users see a Vaadin Grid in the navigation area with one row per table and per-row selection controls for eligible rows

#### Scenario: Selection drawer omits redundant labels
- **WHEN** the home page renders the table-selection drawer
- **THEN** the drawer does not show a `Select tables` heading or selected-table count

#### Scenario: User sorts candidate tables
- **WHEN** a user sorts the table-selection Grid by a visible table-identity column
- **THEN** the visible candidate table rows are reordered according to the selected sort direction

#### Scenario: User filters candidate tables
- **WHEN** a user enters a table-identity filter in the table-selection Grid
- **THEN** the visible candidate table rows are narrowed to rows matching the filter without pressing an apply-filter button

#### Scenario: Ineligible row is non-selectable
- **WHEN** the table-selection Grid includes an ineligible table
- **THEN** the table row cannot be selected and exposes an eligibility reason tooltip

#### Scenario: Schema column auto-sizes for content
- **WHEN** the table-selection Grid renders schema values
- **THEN** the schema column width auto-sizes to fit visible schema content without unnecessary truncation

#### Scenario: Select column is centered with blank header
- **WHEN** the table-selection Grid renders selection controls
- **THEN** the selection control column is center-aligned and its header does not display `Select`

#### Scenario: Selected tables become execution input
- **WHEN** a user marks eligible tables as selected
- **THEN** the resulting selected-table set is available as the stage-one output for comparison execution

#### Scenario: Manual table grid remains below command selection
- **WHEN** command selection and manual table selection sections are both visible
- **THEN** the manual table-selection grid is rendered below the command-selection grid

### Requirement: Manual selection supports future auto-selection overlays
The manual selection model SHALL support future auto-selection defaults with user include and exclude overrides.
The model SHALL preserve deterministic final selected-table output after applying overrides.

#### Scenario: Manual overrides are preserved over defaults
- **WHEN** default selection candidates are provided and a user applies include or exclude overrides
- **THEN** the final selected-table set reflects explicit user overrides deterministically

