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
The Grid SHALL support Space-key toggling of the focused eligible row.
The selection drawer SHALL NOT display a `Select tables` heading or selected-table count.
The schema column SHALL auto-size to fit visible schema values.
The selection control column SHALL be center-aligned.
The selection control column header SHALL be blank and SHALL NOT render `Select` text.
The manual selection state SHALL be available as input to the later comparison-execution stage.
The business table selection section SHALL provide a `Selected only` checkbox control.
The `Selected only` checkbox SHALL be checked by default.
When `Selected only` is checked, the table grid SHALL show only currently selected business table rows.
When `Selected only` is unchecked, the table grid SHALL show selected and unselected business table rows subject to existing table-identity filters.
Toggling `Selected only` SHALL change row visibility only and SHALL NOT directly change underlying row selection state.

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

#### Scenario: Focused eligible row toggles with Space
- **WHEN** keyboard focus is on an eligible row in the table-selection Grid and user presses Space
- **THEN** that row selection is toggled using the same selection-state update path as checkbox interaction

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

#### Scenario: Selected only filter defaults to checked
- **WHEN** the business table selection section is first rendered
- **THEN** the `Selected only` checkbox is checked
- **AND** only selected business table rows are visible

#### Scenario: User reveals all business tables by unchecking selected only
- **WHEN** the user unchecks `Selected only`
- **THEN** the grid shows both selected and unselected business table rows
- **AND** previously selected rows remain selected

### Requirement: Manual selection supports future auto-selection overlays
The manual selection model SHALL support future auto-selection defaults with user include and exclude overrides.
The model SHALL preserve deterministic final selected-table output after applying overrides.

#### Scenario: Manual overrides are preserved over defaults
- **WHEN** default selection candidates are provided and a user applies include or exclude overrides
- **THEN** the final selected-table set reflects explicit user overrides deterministically

### Requirement: Manual business table grid excludes system support tables
The webapp SHALL exclude command-log, audit-trail, and logical-type mapping tables from the manual business table grid.
The exclusion SHALL include `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
Excluded system support tables SHALL not appear in manual table-grid filtering or selection results.

#### Scenario: Business table grid omits command and audit support tables
- **WHEN** the home page loads manual table-selection data
- **THEN** `causewayExtCommandLog.CommandLogEntry` and `causewayExtAuditTrail.AuditTrailEntry` are not shown in the manual table grid

#### Scenario: Business table grid omits logical-type mapping support table
- **WHEN** the home page loads manual table-selection data
- **THEN** `util.LogicalTypeTableMapping` is not shown in the manual table grid

#### Scenario: Remaining business tables still participate in grid interactions
- **WHEN** the user filters or selects rows in the manual table grid
- **THEN** only non-excluded business tables are considered for filtering matches and selection state

### Requirement: Manual table grid accepts command-driven programmatic selections
The manual table grid SHALL accept programmatic selection updates from command footprint resolution.
Programmatic updates SHALL only affect rows that are present in the visible business table catalog.
Programmatic updates SHALL not fail when touched tables are unmapped or absent from the catalog.
When `Selected only` is checked, command-driven programmatic selections SHALL update visible grid rows so newly selected matches become visible.
When `Selected only` is checked, initial command selection and subsequent command selection changes SHALL refresh selection and visibility in the same update cycle.
Users SHALL NOT need to toggle `Selected only` to make newly command-selected business rows visible.
When command-driven recomputation deselects rows while `Selected only` is checked, those rows SHALL no longer remain visible.

#### Scenario: Command-driven selections apply only to visible business rows
- **WHEN** command footprint resolution returns a touched table set
- **THEN** only matching business table rows that exist in the manual table grid are selected
- **AND** unmatched touched tables are ignored

#### Scenario: Manual filtering remains stable with command-driven selections
- **WHEN** command-driven table selections are active
- **AND** the user applies or clears business table filters
- **THEN** selected-state consistency is preserved for the underlying selected table set

#### Scenario: Compare readiness reflects command-driven selected tables
- **WHEN** command-driven updates select one or more eligible business table rows
- **THEN** compare readiness is evaluated using the updated selected table set

#### Scenario: Command-driven selected rows stay visible with selected-only enabled
- **WHEN** `Selected only` is checked
- **AND** command selection changes produce a new touched-table union
- **THEN** rows that are newly selected by the command-driven update are visible in the business table grid

#### Scenario: Initial command selection shows selected rows without selected-only toggle
- **WHEN** `Selected only` is checked
- **AND** the user selects a command row for the first time in the current session state
- **THEN** corresponding selected business rows become visible immediately
- **AND** the user does not need to uncheck and re-check `Selected only`

#### Scenario: Command-driven deselected rows are hidden with selected-only enabled
- **WHEN** `Selected only` is checked
- **AND** command deselection recomputation removes previously selected business rows
- **THEN** those rows are no longer visible in the business table grid

### Requirement: Manual table selection state supports full reset from command section
The manual table selection state SHALL support clearing all selected business tables from a command-section clear action.
A command-section clear action SHALL remove both manual and command-driven programmatic table selections.

#### Scenario: Command clear removes programmatic and manual table selections
- **WHEN** the user triggers clear from the command section
- **THEN** all selected business table rows are deselected
- **AND** compare readiness is recalculated from an empty selected table set

### Requirement: Manual-table selection parameter changes reset comparison status report
The webapp SHALL clear any previously rendered comparison progress status report when manual-table selection parameters change.
Manual-table selection parameters SHALL include business-table row selection changes and business-table filter changes.
The webapp SHALL clear any row-level comparison completion cues when manual-table selection parameters change.
The webapp SHALL hide any compare-adjacent completed counter when manual-table selection parameters change.
The webapp SHALL preserve underlying business-table selection semantics while clearing only stale comparison status reporting and progress cues.

#### Scenario: Manual table selection change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user selects or deselects one or more eligible business table rows
- **THEN** the prior comparison status report is cleared
- **AND** prior row completion cues are cleared
- **AND** any compare-adjacent completed counter is hidden

#### Scenario: Manual table filter change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user changes one or more business-table filter parameters
- **THEN** the prior comparison status report is cleared
- **AND** prior row completion cues are cleared
- **AND** any compare-adjacent completed counter is hidden

### Requirement: Compare run surfaces row-level completion cues and live counter
The webapp SHALL show per-table comparison completion cues in the manual business table grid while a compare run is active.
The webapp SHALL apply a completion background cue to a business table row as soon as that table comparison completes.
The webapp SHALL display a live completed counter adjacent to the Compare action using `completed of total` format.
The webapp SHALL update the completed counter each time a table completion or failure event is received.
The webapp SHALL clear row completion cues and hide the completed counter once the active run ends and a new selection workflow begins.

#### Scenario: Completed table row is highlighted during active run
- **WHEN** a compare run is active and a selected table finishes comparison
- **THEN** the corresponding business table row renders with the configured completion background cue

#### Scenario: Completed counter updates as tables finish
- **WHEN** a compare run starts with five selected tables
- **THEN** the compare-adjacent progress label starts at `0 of 5`
- **AND** advances to `1 of 5`, `2 of 5`, and so on as tables complete

#### Scenario: Counter and cues clear before next selection workflow
- **WHEN** a prior compare run has displayed row completion cues and a completed counter
- **AND** the user begins a new selection workflow
- **THEN** the webapp clears all prior row completion cues and hides the completed counter before the next run

### Requirement: Manual table selection supports shift-based multi-select interactions
The manual table-selection grid SHALL support additive multi-selection through `Shift+Click` on eligible rows.
The manual table-selection grid SHALL support additive multi-selection through `Shift+Space` when focus is on an eligible row.
Shift-based multi-selection SHALL use the same eligibility rules as single-row selection.
Shift-based multi-selection SHALL NOT select disabled or ineligible rows.
Shift-based multi-selection SHALL preserve previously selected rows unless explicitly deselected by a non-shift action.

#### Scenario: User adds multiple rows with shift-click
- **WHEN** a user has an existing selection anchor on an eligible row and performs `Shift+Click` on another eligible row
- **THEN** the grid selects the eligible rows in the interaction range
- **AND** any disabled rows in that range remain unselected

#### Scenario: User adds multiple rows with shift-space
- **WHEN** keyboard focus is on an eligible row and the user performs `Shift+Space`
- **THEN** the grid applies additive multi-selection using the current anchor and focused row
- **AND** disabled rows are skipped and remain unselected

#### Scenario: Shift multi-select does not alter disabled rows
- **WHEN** a shift-based selection interaction includes one or more disabled rows
- **THEN** disabled rows remain unselected
- **AND** selectable rows in scope are still processed for selection

