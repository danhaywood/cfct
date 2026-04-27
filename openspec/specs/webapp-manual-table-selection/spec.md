# webapp-manual-table-selection Specification

## Purpose
TBD - created by archiving change add-webapp-manual-table-selection-panel. Update Purpose after archive.
## Requirements
### Requirement: Webapp exposes a manual table catalog for selection
The webapp SHALL discover and present a manual table catalog for comparison selection.
Each catalog row SHALL include table identity and whether it is currently eligible for selection.
The manual selection state SHALL be available as input to the later comparison-execution stage.

#### Scenario: Catalog lists candidate tables
- **WHEN** the home page initializes table-selection data
- **THEN** users see a list of candidate tables with one row per table and per-row selection controls

#### Scenario: Selected tables become execution input
- **WHEN** a user marks eligible tables as selected
- **THEN** the resulting selected-table set is available as the stage-one output for comparison execution

### Requirement: Manual selection supports future auto-selection overlays
The manual selection model SHALL support future auto-selection defaults with user include and exclude overrides.
The model SHALL preserve deterministic final selected-table output after applying overrides.

#### Scenario: Manual overrides are preserved over defaults
- **WHEN** default selection candidates are provided and a user applies include or exclude overrides
- **THEN** the final selected-table set reflects explicit user overrides deterministically

